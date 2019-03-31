package com.uberblah.school.gatech.ml.projects.markov.planners;

import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PolicyIterationPlannerFactory implements IMyPlannerFactory {

    @Builder.Default
    private double gamma = 0.99;
    @Builder.Default
    private double maxPIDelta = 0.001;
    @Builder.Default
    private double maxEvalDelta = 0.001;
    @Builder.Default
    private int maxEvaluationIterations = 100;
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

    @Override
    public void saveToFile(Planner planner, String fileName) {
        ((PolicyIteration)planner).writeValueTable(fileName);
    }

    @Override
    public Planner loadFromFile(Planner planner, String fileName) {
        PolicyIteration pi = (PolicyIteration)planner;
        pi.loadValueTable(fileName);
        return pi;
    }
}
