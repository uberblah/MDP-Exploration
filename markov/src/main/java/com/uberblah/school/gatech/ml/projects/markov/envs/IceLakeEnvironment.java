package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.valuefunction.ValueFunction;
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
import com.uberblah.school.gatech.ml.projects.markov.learners.QLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.util.MyGridWorldRewardFunction;
import com.uberblah.school.gatech.ml.projects.markov.util.RandomChooser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public class IceLakeEnvironment implements IMyEnvironment {

    private enum Type {
        ICE,
        HOLE,
        WALL
    }

    private long seed;
    private int n;
    private int width;
    private int height;
    private double successProbability;
    private double passivePunishment;
    private double fallPunishment;
    private double goalReward;
    private double holeProbability;
    private double wallProbability;
    private double iceProbability;

    private RandomChooser<Type> chooser;
    private GridWorldDomain gwdg;
    private MyGridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private State initialState;
    private SimulatedEnvironment env;
    private HashableStateFactory hashingFactory;

    public IceLakeEnvironment() {
        seed = 0xdeadbeef;
        n = 5;
        successProbability = 0.9;
        passivePunishment = 0.1;
        fallPunishment = 10.0;
        goalReward = 10.0;
        width = n;
        height = n;
        holeProbability = 0.1;
        wallProbability = 0.1;
        iceProbability = 1.0 - (holeProbability + wallProbability);

        List<RandomChooser.Choice<Type>> choices = new ArrayList<>();
        choices.add(new RandomChooser.Choice<>(Type.ICE, iceProbability));
        choices.add(new RandomChooser.Choice<>(Type.HOLE, holeProbability));
        choices.add(new RandomChooser.Choice<>(Type.WALL, wallProbability));
        chooser = new RandomChooser<>(choices);

        gwdg = new GridWorldDomain(width, height);
        gwdg.setProbSucceedTransitionDynamics(successProbability);
        gwrf = new MyGridWorldRewardFunction(width, height);
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                switch(chooser.sample()) {
                    case ICE:
                        gwrf.setReward(x, y, -passivePunishment);
                        break;
                    case HOLE:
                        gwrf.setReward(x, y, -fallPunishment);
                        gwtf.markAsTerminalPosition(x, y);
                        break;
                    case WALL:
                        gwdg.setObstacleInCell(x, y);
                        break;
                }
            }
        }
        gwrf.setReward(width-1, height-1, goalReward);
        gwtf.markAsTerminalPosition(width-1, height-1);
        gwdg.clearLocationOfWalls(width-1, height-1);

        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    @Override
    public String getEnvironmentName() {
        return String.format("IceLake%d", n);
    }

    @Override
    public int[][] getMap() {
        return gwdg.getMap();
    }

    @Override
    public IMyLearnerFactory[] getLearners() {
        IMyLearnerFactory[] factories = {
                QLearnerFactory.builder()
                        .learnerName("BasiQ")
                        .learningRate(0.01)
                        .build(),
                QLearnerFactory.builder()
                        .learnerName("OptimistiQ")
                        .learningRate(0.01)
                        .qInit(5.0)
                        .learningPolicy(new GreedyQPolicy())
                        .build()
        };
        return factories;
    }

    @Override
    public int getNumEpisodes() {
        return 1000;
    }

    @Override
    public int getNumTrials() {
        return 5;
    }

    @Override
    public IMyPlannerFactory[] getPlanners() {
        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder()
                        .build(),
                PolicyIterationPlannerFactory.builder()
                        .build()
        };
        return planners;
    }
}
