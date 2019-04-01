package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;

public interface IMyLearnerFactory {
    String getLearnerName();
    LearningAgentFactory getLearnerFactory(OOSADomain domain, HashableStateFactory hashingFactory);
}
