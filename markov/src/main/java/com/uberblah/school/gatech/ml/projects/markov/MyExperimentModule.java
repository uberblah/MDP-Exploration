package com.uberblah.school.gatech.ml.projects.markov;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import com.uberblah.school.gatech.ml.projects.markov.envs.BoringEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.DelayedGratificationEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;

public class MyExperimentModule implements ExperimentModule {

    @Override
    public IMyEnvironment[] getEnvironments() {
        IMyEnvironment[] envs = {
                new BoringEnvironment(),
                new DelayedGratificationEnvironment(),
//                new LavaBridgeEnvironment(),
//                new SecretPassageEnvironment()
        };
        return envs;
    }

    @Override
    public IMyPlannerFactory[] getPlanners() {
        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder().build(),
                PolicyIterationPlannerFactory.builder().build()
        };
        return planners;
    }

    @Override
    public LearningAgentFactory[] getLearners(OOSADomain domain, HashableStateFactory hashingFactory) {
        LearningAgentFactory[] learners = {
                new LearningAgentFactory() {
                    public String getAgentName() {
                        return "Epsilon Q";
                    }
                    public LearningAgent generateAgent() {
                        return new QLearning(
                                domain,
                                0.999,
                                hashingFactory,
                                0.0,
                                0.9,
                                1000
                        );
                    }
                },
                new LearningAgentFactory() {
                    public String getAgentName() {
                        return "Optimistic Q";
                    }
                    public LearningAgent generateAgent() {
                        QLearning agent = new QLearning(
                                domain,
                                0.999,
                                hashingFactory,
                                0.0,
                                0.9,
                                1000
                        );
                        agent.setLearningPolicy(new GreedyQPolicy(agent));
                        return agent;
                    }
                }
        };
        return learners;
    }
}
