package com.uberblah.school.gatech.ml.projects.markov.util;

import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import lombok.Builder;
import lombok.NonNull;

@Builder
public class VisualizableRewardFunction implements ValueFunction {

    @NonNull
    private MyGridWorldRewardFunction reward;

    @Override
    public double value(State s) {
        return reward.reward(null, null, s);
    }
}
