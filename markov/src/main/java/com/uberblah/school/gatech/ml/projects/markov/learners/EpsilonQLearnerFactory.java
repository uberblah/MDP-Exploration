package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;

public class EpsilonQLearnerFactory implements IMyLearnerFactory {

    @Override
    public String getLearnerName() {
        return "EpsilonQ";
    }

    @Override
    public LearningAgentFactory getLearnerFactory(OOSADomain domain, HashableStateFactory hashingFactory) {
        return new LearningAgentFactory() {
            public String getAgentName() {
                return "Epsilon Q";
            }
            public LearningAgent generateAgent() {
                return new QLearning(
                        domain,
                        0.999,
                        hashingFactory,
                        0.0,
                        0.9,
                        1000
                );
            }
        };
    }
}
