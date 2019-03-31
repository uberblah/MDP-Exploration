package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;

public interface IMyEnvironment {
    String getEnvironmentName();
    OOSADomain getDomain();
    State getInitialState();
    SimulatedEnvironment getSimulatedEnvironment();
    HashableStateFactory getHashableStateFactory();
}
