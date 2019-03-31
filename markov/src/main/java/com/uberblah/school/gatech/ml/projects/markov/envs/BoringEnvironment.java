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
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import lombok.Getter;

@Getter
public class BoringEnvironment implements IMyEnvironment {
    private final int width = 11;
    private final int height = 11;

    private GridWorldDomain gwdg;
    private OOSADomain domain;
    private TerminalFunction tf;
    private StateConditionTest goalCondition;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

    public BoringEnvironment() {
        this(11, 11);
    }

    public BoringEnvironment(int w, int h) {
        gwdg = new GridWorldDomain(w, h);
        gwdg.setMapToFourRooms();
        tf = new GridWorldTerminalFunction(w-1, h-1);
        gwdg.setTf(tf);
        goalCondition = new TFGoalCondition(tf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(
                new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(w-1, h-1, "loc0"));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    @Override
    public String getEnvironmentName() {
        return "Boring";
    }
}
