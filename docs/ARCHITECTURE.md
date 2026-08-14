# Architecture

## Package layout

```
src/
  board/         The physical board: houses, coordinates, dice.
  player/        Players and their pieces.
  common/        GameLog - the narration callback, with no UI dependency.
  gameLogic/     Rules engine, AI search, game state, and save-relevant model.
  persistence/   Save/load of a game session to disk.
  ui/            The Swing GUI: window, board, console, controls, controller.
  senet/         Application entry point.
```

The dependency direction flows one way: `board` and `player` know nothing
about `gameLogic` or Swing; `gameLogic` knows about `board`, `player`, and
`common.GameLog`, but nothing about `ui` or `persistence`; `persistence`
depends on `gameLogic`; `ui` depends on everything below it. No package
below `ui` imports anything from `javax.swing` — all rendering and input
handling is confined to `ui`.

`common.GameLog` is the one narrow seam between the rules engine and
whatever is displaying its output: `GameLogic` calls `log.log(message)` for
every capture, exit, or special-house effect, without knowing or caring
whether that message ends up in a Swing console panel, a terminal, or
nowhere at all. The Swing `ConsolePanel` implements it directly; the AI
search always uses `GameLog.SILENT`, a no-op implementation, since its
millions of hypothetical moves never actually happen in the real game and
must never appear in the console.

## Class responsibilities

| Class | Responsibility |
|-------|-----------------|
| `board.Coordinate` | A position on the S-shaped path, convertible to/from a 1-30 house number. |
| `board.HouseType` | Enum for the seven kinds of house, replacing the original implementation's raw strings. |
| `board.House` | A single square: number, type, position, current occupant. |
| `board.Dice` | Simulates the four throwing sticks and exposes their true probability distribution. |
| `board.Board` | Owns the 30 houses and lays out the start-of-game position. |
| `player.PlayerType` | Distinguishes a human player from the computer. |
| `player.Piece` | A single pawn: id, colour, board position, exited flag. |
| `player.Player` | A participant: name, colour, type, and seven pieces. |
| `common.GameLog` | Narration callback used by `GameLogic`; `SILENT` is the no-op implementation used during AI search. |
| `gameLogic.Move` | A candidate move: piece id plus step count. |
| `gameLogic.Difficulty` | Named difficulty presets mapped to a default Expectiminimax search depth. |
| `gameLogic.State` | A complete, deep-copyable snapshot of a game (board, both players, current turn, dice). |
| `gameLogic.GameLogic` | The rules engine: legality checks, move application, captures, special-house effects, narrated through a `GameLog`. |
| `gameLogic.GameEngine` | Stateless helpers used only by the AI search: apply a move to a *copy* of a state (always silently), or list legal moves. |
| `gameLogic.GameLogicForAI` | The Expectiminimax search and static evaluation function (see `AI_DESIGN.md`). |
| `persistence.SaveData` | Serializable bundle of a `State` plus the AI settings in effect. |
| `persistence.SaveManager` | Reads and writes named save slots under `saves/`. |
| `ui.MainFrame` | The top-level window: lays out the board, console, and control panels, and builds the menu bar. |
| `ui.GameController` | The only class that mutates the live `State`. Drives the turn flow, runs the AI search on a background `SwingWorker`, and handles pause/save/load/new-game requests. |
| `ui.BoardPanel` | Renders the 30 houses and pieces, and reports clicks back to the controller by house number. Has no notion of whose turn it is. |
| `ui.ConsolePanel` | The scrolling, colour-coded, timestamped log. Implements `common.GameLog`. |
| `ui.ControlPanel` | Status labels and the action buttons; wires them to a `GameController` once attached. |
| `ui.NewGameDialog` | Modal setup wizard: name, difficulty, search depth, debug logging. |
| `ui.RulesDialog` | Modal dialog showing the rules reference. |
| `senet.Senet` | `main()` — launches `ui.MainFrame` on the Swing Event Dispatch Thread. |

## Turn flow and threading

`GameController` is a small state machine (`NO_GAME`, `WAITING_TO_ROLL`,
`WAITING_FOR_SELECTION`, `COMPUTER_TURN`, `PAUSED`, `GAME_OVER`):

- Clicking **Roll Dice** rolls, computes the player's legal moves, and
  either skips the turn (no legal moves) or highlights the movable pieces
  on the board and waits for a click.
- Clicking a highlighted house on the board applies that move, checks for a
  win, switches the turn, and — if it is now the computer's turn — starts
  the computer's turn automatically after a short pause.
- The computer's turn rolls and, if it has a legal move, runs
  `GameLogicForAI.getBestMove` on a `javax.swing.SwingWorker` background
  thread, so the window stays responsive no matter how deep the search
  goes. The chosen move is applied back on the Swing Event Dispatch Thread
  in the worker's `done()` callback, which is also where the move is
  narrated to the console.
- The live `State` object is only ever mutated on the Event Dispatch
  Thread. The AI search only ever reads it and works on deep copies it
  makes itself (see `State.copy()`), so no additional synchronization is
  needed even though the search itself runs on a background thread.

## Notable fixes from the original prototype

This project began as a smaller console prototype. During the rewrite, the
following functional issues were identified and corrected:

1. **State construction argument order.** The computer's turn used to build
   its `State` with the constructor arguments in the wrong order, silently
   swapping which `Player` object played the role of "player one" versus
   "player two" inside the state seen by the AI search.
2. **Corrupted piece positions on every state copy.** `State.copy()` rebuilt
   its board by calling the *start-of-game* `Board` constructor and only
   afterwards patching in the correct house occupants. The start-of-game
   constructor resets every piece's position as a side effect, so it was
   silently overwriting the freshly copied positions on every single node of
   the AI search tree. `Board` now has a second constructor that accepts an
   already-built house array with no side effects, and `State.copy()` uses
   it.
3. **`Coordinate.equals` did not override `Object.equals`.** The original
   method only accepted a `Coordinate` argument, so it never actually
   overrode `Object.equals` and `hashCode` was never implemented at all,
   breaking the equals/hashCode contract for any future collection-based use.
   Both are now implemented correctly.
4. **Narration during AI search.** The rules engine used to print directly
   to the console on every move, capture, and special-house effect,
   including the thousands of hypothetical moves the AI evaluates internally
   while searching. `GameLogic` now narrates through the `common.GameLog`
   callback; the AI search always passes `GameLog.SILENT`, while the real
   game passes the on-screen `ConsolePanel`, so the console only ever shows
   moves that actually happened.
5. **Single-piece "forced house" tracking.** The original console loop only
   ever tracked one piece that might be forced back from houses 28-30, and
   the rule was invisible to the AI's own lookahead. It is now a general
   rule, `GameLogic.applyForcedHouseRule`, applied uniformly after every real
   move and inside the AI's simulated moves alike.
6. **Duplicated house-number conversion.** The conversion between a board
   `Coordinate` and a 1-30 house number was implemented three times
   (`Board`, `GameLogic`, `GameLogicForAI`). It now lives once, on
   `Coordinate` itself.
7. **Dead fields and parameters.** Several unused `Piece` flags
   (`mustMoveNextTurn`, `movedThisTurn`, `isWaitingSpecialExit`,
   `failedExitSpecialHouse`, `pendingForcedExit`) and an unused parameter on
   `returnToRebirthArea` were removed; none of them were ever read anywhere
   in the original code.

## Save files

A save slot is a Java-serialized `persistence.SaveData` object, written to
`saves/<slot-name>.senetsave`. It bundles the full `gameLogic.State` — board,
both players, whose turn it is, and the dice — together with the difficulty,
search depth, and debug-logging setting that were active. Loading a save
restores the game exactly where it left off.
