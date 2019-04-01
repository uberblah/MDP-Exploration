package com.uberblah.school.gatech.ml.projects.markov;

import com.uberblah.school.gatech.ml.projects.markov.envs.BoringEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.DelayedGratificationEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.learners.EpsilonQLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.learners.OptimisticQLearnerFactory;
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
    public IMyLearnerFactory[] getLearners() {
        IMyLearnerFactory[] learners = {
                new EpsilonQLearnerFactory(),
                new OptimisticQLearnerFactory()
        };
        return learners;
    }
}
