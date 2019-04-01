package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;

public class OptimisticQLearnerFactory implements IMyLearnerFactory {

    @Override
    public String getLearnerName() {
        return "OptimisticQ";
    }

    @Override
    public LearningAgentFactory getLearnerFactory(OOSADomain domain, HashableStateFactory hashingFactory) {
        return new LearningAgentFactory() {
            public String getAgentName() {
                return "Optimistic Q";
            }
            public LearningAgent generateAgent() {
                QLearning agent = new QLearning(
                        domain,
                        0.999,
                        hashingFactory,
                        0.0,
                        0.9,
                        1000
                );
                agent.setLearningPolicy(new GreedyQPolicy(agent));
                return agent;
            }
        };
    }
}
