package com.uberblah.school.gatech.ml.projects.markov.util;

import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;

public interface ExperimentModule {
    int getNumTests();
    IMyEnvironment[] getEnvironments();
}
