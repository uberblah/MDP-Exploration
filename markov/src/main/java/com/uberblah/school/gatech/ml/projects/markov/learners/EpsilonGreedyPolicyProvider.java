package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.QProvider;
import lombok.Builder;

@Builder
public class EpsilonGreedyPolicyProvider implements IMyLearningPolicyProvider {

    @Builder.Default
    public double epsilon = 0.1;

    public Policy getLearningPolicy(QProvider q) {
        return new EpsilonGreedy(q, epsilon);
    }
}
