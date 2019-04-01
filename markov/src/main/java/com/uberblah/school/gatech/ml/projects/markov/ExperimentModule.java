package com.uberblah.school.gatech.ml.projects.markov;

import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;

public interface ExperimentModule {
    IMyEnvironment[] getEnvironments();
    IMyPlannerFactory[] getPlanners();
    IMyLearnerFactory[] getLearners();
}
