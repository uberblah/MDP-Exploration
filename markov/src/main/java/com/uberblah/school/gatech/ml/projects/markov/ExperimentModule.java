package com.uberblah.school.gatech.ml.projects.markov;

import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;

public interface ExperimentModule {
    IMyEnvironment[] getEnvironments();
}
