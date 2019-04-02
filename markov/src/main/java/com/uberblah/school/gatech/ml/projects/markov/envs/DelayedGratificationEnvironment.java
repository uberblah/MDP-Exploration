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
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.QLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.util.MyGridWorldRewardFunction;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class DelayedGratificationEnvironment implements IMyEnvironment {
    private int nOptions;
    private double gamma;
    private Function<Integer, Double> punishmentCurve;
    private Function<Integer, Double> rewardCurve;
    private int width;
    private int height;

    private GridWorldDomain gwdg;
    private MyGridWorldRewardFunction gwrf;
    private OOSADomain domain;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

    public DelayedGratificationEnvironment() {
        this.gamma = 0.99;
        this.nOptions = 20;
        this.punishmentCurve = x -> 0.1;
        this.rewardCurve = x -> 0.15*x;
        this.width = nOptions;
        this.height = 2;

        gwdg = new GridWorldDomain(nOptions, 2);
        gwrf = new MyGridWorldRewardFunction(nOptions, 2);
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();
        for (int x = 0; x < nOptions; x++) {
            System.out.println(String.format("(%d,%d) -> %f", x, 0, -this.punishmentCurve.apply(x)));
            System.out.println(String.format("(%d,%d) -> %f", x, 1, this.rewardCurve.apply(x)));
            gwrf.setReward(x, 0, -this.punishmentCurve.apply(x));
            gwrf.setReward(x, 1, this.rewardCurve.apply(x));
            gwtf.markAsTerminalPosition(x, 1);
            gwdg.vertical1DEastWall(1, 1, x);
        }
        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    @Override
    public String getEnvironmentName() {
        return "DelayedGratification";
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
                    .gamma(gamma)
                    .learningRate(0.01)
                    .learningPolicy(new EpsilonGreedy(0.02))
                    .build(),
                QLearnerFactory.builder()
                    .learnerName("OptimistiQ")
                    .gamma(gamma)
                    .learningPolicy(new EpsilonGreedy(0.02))
                    .learningRate(0.01)
                    .qInit(rewardCurve.apply(nOptions-1)) // max reward
                    .build()
        };
        return factories;
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

    @Override
    public int getNumEpisodes() {
        return 5000;
    }
}
