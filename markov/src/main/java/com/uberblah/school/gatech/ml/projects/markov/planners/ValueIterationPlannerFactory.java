package com.uberblah.school.gatech.ml.projects.markov.planners;

import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.*;

@Getter
@Builder
public class ValueIterationPlannerFactory implements IMyPlannerFactory {

    @NonNull
    @Builder.Default
    private double gamma = 0.99;
    @NonNull
    @Builder.Default
    private double maxDelta = 0.001;
    @NonNull
    @Builder.Default
    private int maxIterations = 100;

    @Override
    public String getPlannerName() {
        return "ValueIteration";
    }

    @Override
    public Planner getPlanner(SADomain domain, HashableStateFactory hashingFactory) {
        return new ValueIteration(domain, gamma, hashingFactory, maxDelta, maxIterations);
    }
}
