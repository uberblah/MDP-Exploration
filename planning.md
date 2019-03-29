# Brainstorming

## GridWorld Ideas
- Drunken Walker (LARGE)
  - Value iteration: Probably correct, quickly
  - Policy iteration: Probably correct, maybe slow
  - Greedy Q: Will take a super long time
  - Optimistic Q: Probably still a super long time
- Catwalk (SMALL)
  - Value iteration: Very correct, quickly
  - Policy iteration: Very correct, quickly
  - Greedy Q: Will take a long time
  - Optimistic Q: Should take way less time
- Labyrinth (LARGE)
  - Value iteration: Likely slow
  - Policy iteration: Likely super slow
  - Greedy Q: Will take a SUPER long time
  - Optimistic Q: Should take way less time :D
- Delayed Gratification (SMALL)
  - Value iteration: Fast and correct
  - Policy iteration: ???
  - Greedy Q: Will give up
  - Optimistic Q: Might actually figure it out
- Secret Passage (LARGE)
  - Value iteration: Slow but correct
  - Policy iteration: ???
  - Greedy Q: Takes passage, SUPER SLOW
  - Optimistic Q: Might find the optimal solution (???)

## Algorithms
1. Value Iteration
2. Policy Iteration
3. Q-Learning

## Value Iteration
Given R and T, solve for the utility of all (s, a) pairs.

## Policy Iteration
Given R and T, start with a random policy.

In each iteration, calculate the utility of the policy and then recalculate the optimal policy based on the new utilities.

## Q-Learning

### Hyperparameters
- Initialization
- Learning schedule
- Exploration strategy

### Version 1
- Initialization: Optimistic
- Learning schedule: Decaying
- Exploration: Greedy

### Version 2
- Initialization: Random
- Learning schedule: Decaying
- Exploration: Decaying Epsilon
