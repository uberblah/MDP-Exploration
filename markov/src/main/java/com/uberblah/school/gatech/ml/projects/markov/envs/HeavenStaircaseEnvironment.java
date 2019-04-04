package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.policy.GreedyQPolicy;
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
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.QLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.util.MyGridWorldRewardFunction;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class HeavenStaircaseEnvironment implements IMyEnvironment {
    private double gamma;
    private int bridgeWidth;
    private int bridgeLength;
    private double successProbability;
    private double passivePunishment;
    private Function<Integer, Boolean> heavenHellSelector;
    private Function<Integer, Double> heavenCurve;
    private Function<Integer, Double> hellCurve;
    private Function<Integer, Double> goalRewardFunc;

    private int width;
    private int height;

    private GridWorldDomain gwdg;
    private MyGridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private GridWorldTerminalFunction gwtf;
    private State initialState;
    private SimulatedEnvironment env;
    private HashableStateFactory hashingFactory;

    public HeavenStaircaseEnvironment() {
        gamma = 0.99;
        bridgeWidth = 3;
        bridgeLength = 201;
        successProbability = 0.9;
        passivePunishment = 0.1;
        heavenHellSelector = x -> x % 10 == 0;
        heavenCurve = x -> x * 0.5;
        hellCurve = x -> x * 0.5;
        goalRewardFunc = x -> 10.0 / (x + 1);

        width = bridgeLength;
        height = bridgeWidth+2;

        gwdg = new GridWorldDomain(width, height);
        gwdg.setProbSucceedTransitionDynamics(successProbability);
        gwrf = new MyGridWorldRewardFunction(width, height);
        gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();

        for (int x = 0; x < width; x++) {
            // make the bridge rewards
            for (int y = 1; y < height - 1; y++) {
                gwrf.setReward(x, y, -passivePunishment);
            }
            // make the side rewards
            boolean isHeaven = heavenHellSelector.apply(x);
            double rewardHere = isHeaven ? heavenCurve.apply(x) : -hellCurve.apply(x);
            gwrf.setReward(x, 0, rewardHere);
            gwrf.setReward(x, height-1, rewardHere);
            gwtf.markAsTerminalPosition(x, 0);
            gwtf.markAsTerminalPosition(x, height-1);
            if (isHeaven) {
                gwdg.set1DEastWall(x, 0);
                gwdg.set1DEastWall(x, height-1);
            }
        }

        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

        initialState = new GridWorldState(new GridAgent(0, height / 2));
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
        IMyLearnerFactory[] factories = {
                QLearnerFactory.builder()
                        .gamma(gamma)
                        .learnerName("BasiQ")
                        .learningRate(0.05)
                        .learningPolicy(new EpsilonGreedy(0.1))
                        .build(),
                QLearnerFactory.builder()
                        .gamma(gamma)
                        .learnerName("OptimistiQ")
                        .learningRate(0.05)
                        .qInit(10.0)
                        .learningPolicy(new EpsilonGreedy(0.1))
                        .build()
        };
        return factories;
    }

    @Override
    public int getNumEpisodes() {
        return 10000;
    }

    @Override
    public int getNumTrials() {
        return 5;
    }

    @Override
    public IMyPlannerFactory[] getPlanners() {
        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder()
                        .gamma(gamma)
                        .build(),
                PolicyIterationPlannerFactory.builder()
                        .gamma(gamma)
                        .build()
        };
        return planners;
    }
}
