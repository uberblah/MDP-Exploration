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
        this.punishmentCurve = x -> Math.pow(0.02, x*2);
        this.rewardCurve = x -> Math.pow(1.05, x*2);
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
                    .learningRate(1.0)
                    .learningPolicyProvider(EpsilonGreedyPolicyProvider.builder()
                            .epsilon(0.1)
                            .build()
                    )
                    .build(),
                QLearnerFactory.builder()
                    .learnerName("OptimistiQ")
                    .gamma(gamma)
                    .learningRate(1.0)
                    .learningPolicyProvider(EpsilonGreedyPolicyProvider.builder()
                            .epsilon(0.0)
                            .build()
                    )
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
        return 100;
    }

    @Override
    public int getNumTrials() {
        return 50;
    }
}
