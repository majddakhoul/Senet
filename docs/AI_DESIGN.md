# Computer Opponent: Expectiminimax Search

## Why not plain Minimax

Senet looks like a classic two-player adversarial game, the kind Minimax was
built for: two opponents alternate turns, each trying to maximise their own
outcome and minimise their opponent's. The one thing that breaks the
classic model is the dice. A player does not choose how far they can move —
the throwing sticks decide that for them, and only then does the player
choose *which* piece to move with the roll they were given.

That randomness sits between every pair of turns, so the search tree is not
purely adversarial: it alternates between decision nodes (a player choosing
the best move for a given roll) and chance nodes (the roll itself, drawn from
a known probability distribution). **Expectiminimax** extends Minimax with
exactly this third node type.

## The three node types

`gameLogic.GameLogicForAI` implements three mutually recursive methods that
mirror the three node types:

- **MAX nodes** (`maxValue`) — the computer's decision points. The value of a
  MAX node is the value of the chance node that follows it.
- **MIN nodes** (`minValue`) — the human's decision points, modelled as an
  opponent trying to minimise the computer's score. Structurally identical to
  a MAX node, just seeking the smallest resulting value instead.
- **CHANCE nodes** (`chanceValue`) — the dice roll. Instead of picking the
  best child like MAX or MIN, a chance node computes the **probability-
  weighted average** of the best outcome for every possible roll (1 through
  5), using the true Senet stick-throw distribution from `Dice.getProbability`.

```
        MAX (computer to move)
          |
        CHANCE (roll = 1..5, weighted by probability)
       / | | | \
     roll=1 ... roll=5
      |
    best legal move for that roll
      |
     MIN (human to move)
      |
    CHANCE
      ...
```

At the leaves — either a fixed search depth is reached, or a player has won —
the state is scored by a static evaluation function (see below) rather than
searched further.

## Search depth and difficulty

Each MAX/CHANCE/MIN/CHANCE cycle counts as one level of depth. The deeper the
search, the further into the future the computer plans, at the cost of
visiting exponentially more nodes (the branching factor is the number of
legal moves times five possible rolls). `gameLogic.Difficulty` maps each
difficulty preset to a default search depth:

| Difficulty | Default depth | Character |
|------------|---------------|-----------|
| Easy       | 2  | Looks only one exchange ahead; fast and beatable. |
| Medium     | 4  | A balanced, moderately challenging opponent. |
| Hard       | 6  | Plans several moves ahead; noticeably stronger. |
| Expert     | 8  | The deepest preset; slow but very difficult to beat. |
| Custom     | user-chosen | Pick any depth directly. |

The search depth can also be set directly when starting a new game.

## Static evaluation (the heuristic)

When the search bottoms out, `heuristic(State)` scores the position from the
computer's point of view (positive is good for the computer):

- Each piece that has already exited the board is worth ±10.
- Each piece still on the board earns a baseline value plus a bonus for how
  close it is to exiting.
- Sitting on a favourable special house (Happiness, Rebirth, Horus, Three
  Truths, Re-Atoum) adds a bonus; sitting on the House of Water subtracts one,
  since the very next move sends that piece back to the start area.
- A piece immediately behind an opposing piece is worth slightly more, since
  it threatens a capture on the following turn.

Every term is mirrored: a piece that helps the human by the same margin
subtracts the same amount from the score. This keeps the evaluation
symmetric regardless of which colour the computer is playing.

## Debug logging

Enabling verbose AI logging in the New Game dialog makes `GameLogicForAI`
print every node it visits — MAX, MIN, and CHANCE — along with the
heuristic value at each leaf, to the **terminal**, not the on-screen
console panel. This is extremely detailed, high-volume output intended for
understanding or debugging the search rather than for normal play, which is
why it stays out of the in-game console; the console always shows a summary
line for the computer's move (the piece moved, the step count, and how
many search nodes were explored) regardless of whether debug logging is on.
