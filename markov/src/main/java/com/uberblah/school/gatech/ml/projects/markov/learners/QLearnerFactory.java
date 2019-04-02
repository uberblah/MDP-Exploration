package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
public class QLearnerFactory implements IMyLearnerFactory {
    @NonNull
    @Getter
    @Builder.Default
    private final String learnerName = "QLearner";
    @NonNull
    @Builder.Default
    private final double gamma = 0.99;
    @NonNull
    @Builder.Default
    private final double qInit = 0.0;
    @NonNull
    @Builder.Default
    private final double learningRate = 0.1;
    @NonNull
    @Builder.Default
    private final int maxEpisodeSize = 1000;
    @NonNull
    @Builder.Default
    private final Policy learningPolicy = new EpsilonGreedy(0.05);

    @Override
    public LearningAgentFactory getLearnerFactory(OOSADomain domain, HashableStateFactory hashingFactory) {
        return new LearningAgentFactory() {
            public String getAgentName() {
                return learnerName;
            }
            public LearningAgent generateAgent() {
                QLearning agent = new QLearning(
                        domain,
                        gamma,
                        hashingFactory,
                        qInit,
                        learningRate,
                        learningPolicy,
                        maxEpisodeSize
                );
                agent.setLearningPolicy(new GreedyQPolicy(agent));
                return agent;
            }
        };
    }
}
