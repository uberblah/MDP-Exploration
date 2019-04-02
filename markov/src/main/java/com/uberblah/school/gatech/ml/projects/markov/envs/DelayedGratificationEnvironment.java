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
import com.uberblah.school.gatech.ml.projects.markov.util.MyGridWorldRewardFunction;
import lombok.Getter;

import java.util.function.Function;

@Getter
public class DelayedGratificationEnvironment implements IMyEnvironment {
    private int nOptions;
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

    public DelayedGratificationEnvironment(
            final int nOptions,
            final Function<Integer, Double> punishmentCurve,
            final Function<Integer, Double> rewardCurve
    ) {
        this.nOptions = nOptions;
        this.punishmentCurve = punishmentCurve;
        this.rewardCurve = rewardCurve;
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
        }
        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(gwrf);

        initialState = new GridWorldState(new GridAgent(0, 0));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    public DelayedGratificationEnvironment() {
        this(20, x -> 0.1, x -> 0.2 * (x + 1));
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
                    .learningRate(0.3)
                    .learningPolicy(new EpsilonGreedy(0.02))
                    .build(),
                QLearnerFactory.builder()
                    .learnerName("OptimistiQ")
                    .learningPolicy(new EpsilonGreedy(0.02))
                    .learningRate(0.3)
                    .qInit(10.0)
                    .build()
        };
        return factories;
    }

    @Override
    public int getNumEpisodes() {
        return 1000;
    }
}
