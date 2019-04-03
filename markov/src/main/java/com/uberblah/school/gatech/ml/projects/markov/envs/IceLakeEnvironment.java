package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;
import lombok.Getter;

@Getter
public class IceLakeEnvironment implements IMyEnvironment {
    private int width;
    private int height;
    private double successProbability;
    private double passivePunishment;
    private double fallPunishment;
    private double goalRewardFunc;
    private double holeProbability;
    private double boulderProbability;

    private GridWorldDomain gwdg;
    private GridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private State initialState;
    private SimulatedEnvironment env;
    private HashableStateFactory hashingFactory;

    public IceLakeEnvironment() {
        successProbability = 0.9;
        passivePunishment = 0.1;
        fallPunishment = 5.0;
        goalRewardFunc = 10.0;
        width = 20;
        height = 20;
        holeProbability = 0.1;
        boulderProbability = 0.1;

        gwdg = new GridWorldDomain(width, height);
        gwdg.setProbSucceedTransitionDynamics(successProbability);
        gwrf = new GridWorldRewardFunction(width, height);
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();

        // TODO: STUFF HERE

        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
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
