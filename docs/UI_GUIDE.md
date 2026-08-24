# UI Guide

[⬅ Main README](../README.md) · [العربية](UI_GUIDE.ar.md)

A walkthrough of every screen in the application, in the order you would actually meet them: starting a game, playing it, pausing it, saving it, loading it back, and checking the rules. Each section names the class that owns that screen — see [`ARCHITECTURE.md`](ARCHITECTURE.md) for how they fit together.

## Starting a new game

**Game > New Game...**, the **New Game** button, or launching the app for the first time all open the setup dialog (`ui.NewGameDialog`):

<img src="assets/ui/new-game-dialog.png" alt="New Game dialog: name field, difficulty dropdown, search depth spinner, verbose logging checkbox" width="340" />

- **Your name** — labels your pieces in the console and status panel; defaults to "Player" if left blank.
- **Difficulty** — one of the five [`gameLogic.Difficulty`](../src/gameLogic/Difficulty.java) presets (Easy/Medium/Hard/Expert/Custom). Choosing a preset fills in its default search depth automatically and shows a one-line description underneath, exactly as pictured (Easy: *"the computer looks two half-moves ahead"*).
- **Search depth** — greyed out for the four presets, editable only when Difficulty is set to `CUSTOM`, from 1 to 8. See [`AI_DESIGN.md`](AI_DESIGN.md#search-depth-and-difficulty) for what depth actually controls.
- **Verbose AI search logging** — when checked, every MAX/MIN/CHANCE node the search visits is printed to the **terminal** (not the in-app console — see [`AI_DESIGN.md`](AI_DESIGN.md#debug-logging) for why). Leave it unchecked for normal play; it produces a very large amount of output.

**Start Game** builds a fresh board and begins; **Cancel** closes the dialog without starting anything (or, if a game is already running, leaves it exactly as it was).

## The main window

Once a game starts, the window splits into the three regions [`ui.MainFrame`](../src/ui/MainFrame.java) lays out:

<img src="assets/ui/main-window.png" alt="Main window: board on the left, empty console in the middle, status and action buttons on the right" width="800" />

- **Board (left, `ui.BoardPanel`)** — the 30 houses in their authentic S-shaped path. Each house's fill colour is its type (see the legend below); the small letter in a house's top-right corner is that type's symbol, matching [`board.HouseType`](../src/board/HouseType.java). Pieces are filled circles — blue for the human player, white/cream for the computer — with their piece number inside.
- **Console (middle, `ui.ConsolePanel`)** — starts empty and fills up as the game is played (see the next section).
- **Status and actions (right, `ui.ControlPanel`)** — whose turn it is, the current roll, both players' "pieces home" counts, the active difficulty and search depth, and the six action buttons: Roll Dice, Pause, Save Game, Load Game, New Game, Rules.

| House colour | Type | Symbol |
|---|---|---|
| Cream | Normal | *(none)* |
| Green | House of Rebirth | `R` |
| Gold | House of Happiness | `H` |
| Blue | House of Water | `W` |
| Purple | House of Three Truths | `T` |
| Mauve | House of Re-Atoum | `A` |
| Orange | House of Horus | `O` |

What each one does to a piece that lands there is in [`GAME_RULES.md`](GAME_RULES.md#the-five-houses-of-the-end-game-26-30).

## Playing a turn

Click **Roll Dice**; the console reports the throw, and — as pieces move, capture, and trigger special-house effects — every event is timestamped and colour-tagged by who caused it: blue for `PLAYER`, orange for `COMPUTER`, grey for neutral `GAME`/`SYSTEM` narration:

<img src="assets/ui/gameplay-console.png" alt="A game in progress: the console shows alternating player and computer turns, moves, and capture narration" width="800" />

Notice the computer's lines: *"Computer is thinking (difficulty: EASY, search depth: 2)..."* followed by *"(explored 51 search nodes)"* once it moves — that node count is the actual size of the Expectiminimax tree explored for that one decision, small at Easy and much larger at higher difficulties. A capture (landing on a lone opposing piece) always narrates as its own `GAME -> Capture!` line naming which piece was bumped back and to which house.

Only moves that really happened ever appear here — the thousands of hypothetical positions the AI evaluates internally while searching are never printed to this panel; see [`ARCHITECTURE.md`](ARCHITECTURE.md#notable-fixes-from-the-original-prototype) for why that used to leak into the console before this project's rewrite.

A later point in the same kind of game looks like this — more captures, more of both players' pieces further along the S-path, the AI's explored-node counts shifting move to move:

<img src="assets/ui/midgame-progress.png" alt="The same board later in the game, with several pieces further along the path and more captures narrated" width="800" />

If you have no legal move for your roll, the turn is skipped automatically and the console explains why — you never have to click anything to pass.

## Pausing and resuming

Click **Pause** at any point during your turn to freeze input without losing your position; the board and console stay exactly as they were, the status line reads **Paused**, and the button becomes **Resume**:

<img src="assets/ui/paused-state.png" alt="A paused game: the status line reads Paused and the button reads Resume" width="800" />

Click **Resume** to continue exactly where you left off. Pausing does not write anything to disk by itself — it only blocks input in `ui.GameController`'s turn state machine; use **Save Game** separately if you want the position to survive closing the app.

## Saving a game

Click **Save Game** (or **Game > Save Game...**) at any point during your turn to write the current position to a named slot:

<img src="assets/ui/save-dialog.png" alt="Save slot name prompt, defaulting to 'quicksave'" width="300" />

The name you type becomes the file `saves/<name>.senetsave`, written by [`persistence.SaveManager`](../src/persistence/SaveManager.java). It bundles the full board, both players, whose turn it is, and the active difficulty/search-depth/logging settings — everything needed to resume exactly where you left off, even in a later session. See [`ARCHITECTURE.md`](ARCHITECTURE.md#save-files) for the file format.

## Loading a game

**Load Game** (button or **Game > Load Game...**) behaves differently depending on whether any save exists yet. With none:

<img src="assets/ui/load-dialog-empty.png" alt="Load Game with no saves: 'There are no saved games yet.'" width="300" />

With at least one slot on disk, a dropdown lists every save under `saves/` by name:

<img src="assets/ui/load-dialog-select.png" alt="Load Game dropdown listing the 'quicksave' slot" width="300" />

Choosing a slot and pressing **OK** restores the board, both players, whose turn it is, and the difficulty/search-depth settings exactly as they were saved, and the game continues from there.

## Rules reference

**Help > How to Play...**, or the **Rules** button, opens the full ruleset in a scrollable dialog (`ui.RulesDialog`) without leaving the game:

<img src="assets/ui/rules-dialog.png" alt="How to Play dialog: throwing sticks, movement and capture, and the five special end-game houses" width="420" />

This is the same Kendall's Rules reference as [`GAME_RULES.md`](GAME_RULES.md), reformatted for in-app reading — useful for a quick check mid-game without alt-tabbing away from the window.
