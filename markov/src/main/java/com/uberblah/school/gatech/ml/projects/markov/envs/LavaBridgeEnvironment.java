package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class LavaBridgeEnvironment implements IMyEnvironment {
    private int width;
    private int height;

    private GridWorldDomain gwdg;
    private GridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private TerminalFunction tf;
    private StateConditionTest goalCondition;
    private State initialState;
    private SimulatedEnvironment env;
    private HashableStateFactory hashingFactory;

    public LavaBridgeEnvironment(
            final int nOptions,
            final Function<Integer, Double> punishmentCurve,
            final Function<Integer, Double> rewardCurve
    ) {
        this.width = nOptions;
        this.height = 2;

        gwdg = new GridWorldDomain(nOptions, 2);
        gwrf = new GridWorldRewardFunction(nOptions, 2);
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();
        // TODO: set up Terminals, Walls and Rewards
        tf = gwtf;
        gwdg.setTf(tf);
        gwdg.setRf(gwrf);
        goalCondition = new TFGoalCondition(gwtf);
        domain = gwdg.generateDomain();

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    public LavaBridgeEnvironment() {
        this(20, x -> 0.1, x -> 0.11 * (x + 1));
    }

    @Override
    public String getEnvironmentName() {
        return "LavaBridge";
    }

    @Override
    public int[][] getMap() {
        return gwdg.getMap();
    }

    @Override
    public IMyLearnerFactory[] getLearners() {
        return new IMyLearnerFactory[0];
    }

    @Override
    public int getNumEpisodes() {
        return 100;
    }

    @Override
    public int getNumTrials() {
        return 50;
    }

    @Override
    public IMyPlannerFactory[] getPlanners() {
        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder().build(),
                PolicyIterationPlannerFactory.builder().build()
        };
        return planners;
    }
}
