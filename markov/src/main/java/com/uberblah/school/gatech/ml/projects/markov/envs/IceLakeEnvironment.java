package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.EpsilonGreedyPolicyProvider;
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

@Getter
public class IceLakeEnvironment implements IMyEnvironment {

    public enum Size {
        SMALL,
        LARGE
    }

    private enum Type {
        ICE,
        HOLE,
        WALL
    }

    private Size size;
    private long seed;
    private int n;
    private int width;
    private int height;
    private int nEpisodes;
    private int nTrials;
    private double maxDelta;
    private double successProbability;
    private double passivePunishment;
    private double fallPunishment;
    private double goalReward;
    private double holeProbability;
    private double wallProbability;
    private double iceProbability;
    private double gamma;

    private RandomChooser<Type> chooser;
    private GridWorldDomain gwdg;
    private MyGridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private State initialState;
    private SimulatedEnvironment env;
    private HashableStateFactory hashingFactory;

    private void smallInit() {
        n = 7;
        seed = 0xdeadbeef;
        goalReward = 3.0;
        fallPunishment = 3.0;
        nEpisodes = 100;
        nTrials = 50;
        maxDelta = 0.1;
    }

    private void largeInit() {
        n = 40;
        seed = 0xdeadbeef;
        gamma = 0.999;
        goalReward = 30.0;
        fallPunishment = 30.0;
        nEpisodes = 20000;
        nTrials = 5;
        maxDelta = 0.1;
    }

    public IceLakeEnvironment(Size size) {

        seed = 0xdeadbeef;
        n = 5;
        successProbability = 0.9;
        passivePunishment = 0.1;
        fallPunishment = 10.0;
        goalReward = 10.0;
        holeProbability = 0.1;
        wallProbability = 0.1;
        gamma = 0.99;

        this.size = size;
        switch (size) {
            case SMALL:
                smallInit();
                break;
            case LARGE:
                largeInit();
                break;
        }

        width = n;
        height = n;
        iceProbability = 1.0 - (holeProbability + wallProbability);

        List<RandomChooser.Choice<Type>> choices = new ArrayList<>();
        choices.add(new RandomChooser.Choice<>(Type.ICE, iceProbability));
        choices.add(new RandomChooser.Choice<>(Type.HOLE, holeProbability));
        choices.add(new RandomChooser.Choice<>(Type.WALL, wallProbability));
        chooser = new RandomChooser<>(choices, seed);

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

        initialState = new GridWorldState(new GridAgent(0, 0));
        gwdg.clearLocationOfWalls(0, 0);
        gwtf.unmarkTerminalPosition(0, 0);
        gwrf.setReward(0, 0, passivePunishment);

        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

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
                        .gamma(gamma)
                        .learnerName("BasiQ")
                        .learningRate(0.5)
                        .learningPolicyProvider(EpsilonGreedyPolicyProvider.builder()
                                .epsilon(0.05)
                                .build()
                        )
                        .build(),
                QLearnerFactory.builder()
                        .gamma(gamma)
                        .learnerName("OptimistiQ")
                        .learningRate(0.2)
                        .qInit(Math.log(goalReward))
                        .learningPolicyProvider(EpsilonGreedyPolicyProvider.builder()
                                .epsilon(0.0)
                                .build()
                        )
                        .build()
        };
        return factories;
    }

    @Override
    public int getNumEpisodes() {
        return nEpisodes;
    }

    @Override
    public int getNumTrials() {
        return nTrials;
    }

    @Override
    public IMyPlannerFactory[] getPlanners() {
        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder()
                        .gamma(gamma)
                        .maxIterations(1000)
                        .maxDelta(0.001)
                        .build(),
                PolicyIterationPlannerFactory.builder()
                        .gamma(gamma)
                        .maxPolicyIterations(333)
                        .maxEvaluationIterations(1)
                        .maxPIDelta(0.05 * goalReward)
                        .maxEvalDelta(0.0)
                        .build()
        };
        return planners;
    }
}
