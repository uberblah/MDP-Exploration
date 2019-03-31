package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class DelayedGratificationEnvironment implements IMyEnvironment {
    private int nOptions;
    private Function<Integer, Double> punishmentCurve;
    private Function<Integer, Double> rewardCurve;

    private GridWorldDomain gwdg;
    private GridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private TerminalFunction tf;
    private StateConditionTest goalCondition;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

    public DelayedGratificationEnvironment(
            final int nOptions,
            final Function<Integer, Double> punishmentCurve,
            final Function<Integer, Double> rewardCurve
    ) {
        this.nOptions = nOptions;
        this.punishmentCurve = punishmentCurve;
        this.rewardCurve = rewardCurve;

        gwdg = new GridWorldDomain(nOptions, 2);
        gwrf = new GridWorldRewardFunction(nOptions, 2);
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();
        for (int x = 0; x < nOptions; x++) {
            gwtf.markAsTerminalPosition(x, 1);
            gwrf.setReward(x, 0, -this.punishmentCurve.apply(x));
            gwrf.setReward(x, 1, this.rewardCurve.apply(x));
        }
        tf = gwtf;
        gwdg.setTf(tf);
        gwdg.setRf(gwrf);
        goalCondition = new TFGoalCondition(gwtf);
        domain = gwdg.generateDomain();

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    public DelayedGratificationEnvironment() {
        this(20, x -> 0.1, x -> 0.11 * (x + 1));
    }

    @Override
    public String getEnvironmentName() {
        return "DelayedGratification";
    }

    @Override
    public OOSADomain getDomain() {
        return domain;
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    @Override
    public SimulatedEnvironment getSimulatedEnvironment() {
        return env;
    }

    @Override
    public HashableStateFactory getHashableStateFactory() {
        return hashingFactory;
    }
}
