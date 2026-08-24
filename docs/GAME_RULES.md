# Senet Rules Reference (Kendall's Rules)

[⬅ Main README](../README.md) · [العربية](GAME_RULES.ar.md)

This document is the authoritative rules reference for the ruleset implemented
by this project. Senet's original rules were never recorded directly; this
implementation follows the widely used reconstruction known as **Kendall's
Rules**, assembled from tomb paintings and surviving game sets.

## Historical background

The name *Senet* derives from an ancient Egyptian word meaning "passage," a
reference to the journey toward the afterlife. The oldest known Senet boards
date to the Middle Kingdom (circa 2050-1710 BCE), and depictions of the game
appear in tomb art as early as the 25th century BCE. Senet remained a common
funerary and recreational game for millennia before gradually disappearing
under Roman rule, around 30 BCE.

## The board

The board is a rectangular grid of three rows of ten squares each ("houses"),
numbered 1 to 30 along a boustrophedon ("S-shaped") path:

```
 1  2  3  4  5  6  7  8  9 10
20 19 18 17 16 15 14 13 12 11
21 22 23 24 25 26 27 28 29 30
```

Each player has seven pieces. At the start of the game they are placed
alternately on houses 1 through 14. A player wins by moving all seven pieces
off the far end of the board first.

## Throwing sticks

Each turn, a player casts four two-sided throwing sticks. Each stick lands
either dark side up (worth 1) or light side up (worth 0). The four results
are summed to a value from 0 to 4; a throw of 0 (all sticks light) is instead
counted as a special throw of 5. This produces the following distribution:

| Roll | Probability |
|------|-------------|
| 1    | 4/16        |
| 2    | 6/16        |
| 3    | 4/16        |
| 4    | 1/16        |
| 5    | 1/16        |

The player then moves one piece forward by exactly that many houses.

## Movement and capture

- A piece may move to any house that is empty or occupied by a single
  opposing piece.
- Landing on a house held by an opposing piece **swaps** the two pieces'
  positions (the ancient equivalent of a capture).
- A piece may never land on a house already occupied by one of the same
  player's own pieces.
- If neither player has any legal move for the roll they receive, that turn
  is forfeit and passes to the opponent.

## The five houses of the end game (26-30)

The last stretch of the board carries the game's special rules:

| House | Name                | Effect |
|-------|----------------------|--------|
| 15    | House of Rebirth     | The fallback square: any piece sent back by House 27, or by failing to leave houses 28-30 in time, is relocated to the nearest free house at or before 15 (searching downward from 15). |
| 26    | House of Happiness   | A mandatory checkpoint. No piece may move from before house 26 to a house after 26 in a single move; it must land exactly on 26. |
| 27    | House of Water       | Landing here is punished immediately: the piece is sent straight back to the House of Rebirth. |
| 28    | House of Three Truths| A piece that lands here may only leave on the very next turn, and only with a roll of exactly 3. If that turn passes without the piece leaving with a 3, it is sent back to the House of Rebirth. |
| 29    | House of Re-Atoum    | The same rule as House 28, but the required roll is exactly 2. |
| 30    | House of Horus       | A piece here may leave with any roll, but only on the very next turn. If it does not move that turn, it is sent back to the House of Rebirth. |

A piece leaves the board entirely (scores) once it moves past house 30.

## Winning

The first player to move all seven of their pieces off the end of the board
wins the game.

## Reference

The Expectiminimax search described in
[`AI_DESIGN.md`](AI_DESIGN.md) is the algorithm this project uses to have the
computer opponent play under exactly this ruleset.
