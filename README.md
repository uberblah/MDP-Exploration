# Acquiring the code

`git clone git@github.com:uberblah/MDP-Exploration.git`

# Running the code

1. Install Gradle.
2. Use `markov/build.gradle`'s `run` target.

Alternatively, it should be possible to use IntelliJ or Eclipse to load the project using `markov/build.gradle`.

The data will be produced in the `markov/output` directory, but the running experiment will open up a series of windows to
display that data in a human-readable manner, including all of the diagrams from the paper.

I have included the paper in this repo, just for redundancy's sake, as `mgrl3-analysis.pdf`.
