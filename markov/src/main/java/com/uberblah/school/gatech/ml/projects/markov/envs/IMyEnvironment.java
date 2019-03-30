package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

public interface IMyEnvironment {
    String getEnvironmentName();
    SimulatedEnvironment getSimulatedEnvironment();
    HashableStateFactory getHashableStateFactory();
}
