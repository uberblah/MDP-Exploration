package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
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

public class DelayedGratificationEnvironment implements IMyEnvironment {

    @Override
    public String getEnvironmentName() {
        return "DelayedGratification";
    }

    @Override
    public SimulatedEnvironment getSimulatedEnvironment() {
        GridWorldDomain gwdg = new GridWorldDomain(11, 3);

        TerminalFunction tf = new GridWorldTerminalFunction(10, 10);
        gwdg.setTf(tf);
        StateConditionTest goalCondition = new TFGoalCondition(tf);
        OOSADomain domain = gwdg.generateDomain();

        State initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));

        SimulatedEnvironment env = new SimulatedEnvironment(domain, initialState);

        return env;
    }

    @Override
    public HashableStateFactory getHashableStateFactory() {
        return new SimpleHashableStateFactory();
    }
}
