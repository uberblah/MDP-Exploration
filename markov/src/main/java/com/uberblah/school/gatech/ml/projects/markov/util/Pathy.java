package com.uberblah.school.gatech.ml.projects.markov.util;

import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Pathy {

    private static final String outputRoot = "output";

    public Pathy() {}

    public Path casePath(IMyEnvironment env, IMyPlannerFactory plannerFactory) {
        return Paths.get(outputRoot, env.getEnvironmentName(), plannerFactory.getPlannerName());
    }

    public Path casePath(IMyEnvironment env, IMyLearnerFactory learnerFactory) {
        return Paths.get(outputRoot, env.getEnvironmentName(), learnerFactory.getLearnerName());
    }

    public Path envPath(IMyEnvironment env) {
        return Paths.get(outputRoot, env.getEnvironmentName());
    }

    public Path envFilePath(IMyEnvironment env, String filename) {
        return Paths.get(envPath(env).toString(), filename);
    }
}
