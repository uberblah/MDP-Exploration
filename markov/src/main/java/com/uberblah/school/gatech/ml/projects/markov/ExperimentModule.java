package com.uberblah.school.gatech.ml.projects.markov;

import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;

public interface ExperimentModule {
    IMyEnvironment[] getEnvironments();
    IMyPlannerFactory[] getPlanners();
    LearningAgentFactory[] getLearners(OOSADomain domain, HashableStateFactory hashingFactory);
}
