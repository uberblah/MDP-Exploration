package com.uberblah.school.gatech.ml.projects.markov.envs;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
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
import lombok.Getter;

@Getter
public class RingMazeEnvironment implements IMyEnvironment {
    private double gamma;
    private int width;
    private int height;
    private int mid;
    private int botMid;
    private int topMid;
    private double passivePunishment;
    private double goalReward;
    private int n;

    private GridWorldDomain gwdg;
    private GridWorldTerminalFunction gwtf;
    private TFGoalCondition goalCondition;
    private GoalBasedRF rf;
    private OOSADomain domain;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

    public RingMazeEnvironment() {
        this.gamma = 0.99;
        this.n = 20;
        this.width = (2 * n) + 1;
        this.height = (2 * n) + 1;
        this.passivePunishment = 0.1;
        this.goalReward = 0.0;

        gwdg = new GridWorldDomain(width, height);
        gwtf = new GridWorldTerminalFunction();
        gwtf.unmarkAllTerminalPositions();

        gwdg.vertical1DEastWall(topMid, topMid, 0);
        gwdg.vertical1DEastWall(botMid, botMid, 0);
        gwdg.vertical1DEastWall(topMid, topMid, width-2);
        gwdg.vertical1DEastWall(botMid, botMid, width-2);
        gwdg.horizontal1DNorthWall(1, width-2, topMid);
        gwdg.horizontal1DNorthWall(1, width-2, botMid -1);

        for (int x = 1; x < width - 3; x += 2) {
            gwdg.vertical1DEastWall(mid, topMid, x);
            gwdg.vertical1DEastWall(botMid, mid, x+1);
        }

        gwtf.markAsTerminalPosition(width-1, mid);

        goalCondition = new TFGoalCondition(gwtf);
        rf = new GoalBasedRF(goalCondition, goalReward, -passivePunishment);
        gwdg.setTf(gwtf);
        domain = gwdg.generateDomain();
        ((FactoredModel)domain.getModel()).setRf(rf);

        initialState = new GridWorldState(new GridAgent(0, mid));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    @Override
    public String getEnvironmentName() {
        return "SecretPassage";
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
                        .learningRate(0.8)
                        .build(),
                QLearnerFactory.builder()
                        .learnerName("OptimistiQ")
                        .gamma(gamma)
                        .learningRate(0.8)
                        .qInit(10 * passivePunishment) // max reward
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
        return 2000;
    }

    @Override
    public int getNumTrials() {
        return 5;
    }
}
