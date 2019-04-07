package com.uberblah.school.gatech.ml.projects.markov.planners;

import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValueIterationPlannerFactory implements IMyPlannerFactory {

    @Builder.Default
    private double gamma = 0.99;
    @Builder.Default
    private double maxDelta = 0.001;
    @Builder.Default
    private int maxIterations = 100;
    @Builder.Default
    private String plannerName = "ValueIteration";

    @Override
    public Planner getPlanner(SADomain domain, HashableStateFactory hashingFactory) {
        return new ValueIteration(domain, gamma, hashingFactory, maxDelta, maxIterations);
    }

    @Override
    public void saveToFile(Planner planner, String fileName) {
        ((ValueIteration)planner).writeValueTable(fileName);
    }

    @Override
    public Planner loadFromFile(Planner planner, String fileName) {
        ValueIteration vi = (ValueIteration)planner;
        vi.loadValueTable(fileName);
        return vi;
    }
}
