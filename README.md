# Acquiring the code

`git clone git@github.com:uberblah/MDP-Exploration.git`

# Running the code

### On Unix
```
cd markov
./gradlew run
```

### On Windows
```
cd markov
./gradlew.bat run
```

You will need to interrupt the program to stop it, once all the data have been generated and displayed.

Alternatively, it should be possible to use IntelliJ or Eclipse to load the project.

The data will be produced in the `markov/output` directory, but the running experiment will open up a series of windows to
display that data in a human-readable manner, including all of the diagrams from the paper.

I have included the paper in this repo, just for redundancy's sake, as `mgrl3-analysis.pdf`.

Some of the data are actually output to the terminal. Timestamps are in nanoseconds.
