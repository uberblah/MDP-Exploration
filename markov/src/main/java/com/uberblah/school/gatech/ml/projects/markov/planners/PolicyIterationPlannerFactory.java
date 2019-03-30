package com.uberblah.school.gatech.ml.projects.markov.planners;

import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PolicyIterationPlannerFactory implements IMyPlannerFactory {

    @NonNull
    @Builder.Default
    private double gamma = 0.99;
    @NonNull
    @Builder.Default
    private double maxPIDelta = 0.001;
    @NonNull
    @Builder.Default
    private double maxEvalDelta = 0.001;
    @NonNull
    @Builder.Default
    private int maxEvaluationIterations = 100;
    @NonNull
    @Builder.Default
    private int maxPolicyIterations = 100;

    @Override
    public String getPlannerName() {
        return "PolicyIteration";
    }

    @Override
    public Planner getPlanner(SADomain domain, HashableStateFactory hashingFactory) {
        return new PolicyIteration(
                domain, gamma, hashingFactory, maxPIDelta, maxEvalDelta, maxEvaluationIterations, maxPolicyIterations);
    }
}
