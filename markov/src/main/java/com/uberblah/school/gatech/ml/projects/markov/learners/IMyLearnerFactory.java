package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;

public interface IMyLearnerFactory {
    String getLearnerName();
    LearningAgentFactory getLearnerFactory(OOSADomain domain, HashableStateFactory hashingFactory);
    Policy planFromState(LearningAgent agent, State initialState);
    void saveToFile(LearningAgent agent, String filename);
    LearningAgent loadFromFile(LearningAgent agent, String filename);
}
