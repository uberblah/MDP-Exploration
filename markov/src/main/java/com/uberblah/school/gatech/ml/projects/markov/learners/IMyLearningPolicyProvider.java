package com.uberblah.school.gatech.ml.projects.markov.learners;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.QProvider;

public interface IMyLearningPolicyProvider {
    Policy getLearningPolicy(QProvider q);
}
