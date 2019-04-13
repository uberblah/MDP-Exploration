package com.uberblah.school.gatech.ml.projects.markov.util;

import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IceLakeEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.util.ExperimentModule;

public class MyExperimentModule implements ExperimentModule {

    @Override
    public int getNumTests() {
        return 1000;
    }

    @Override
    public IMyEnvironment[] getEnvironments() {
        IMyEnvironment[] envs = {
//                new BoringEnvironment(),
//                new DelayedGratificationEnvironment(),
//                new HeavenStaircaseEnvironment(),
//                new SecretPassageEnvironment(),
                new IceLakeEnvironment(IceLakeEnvironment.Size.SMALL),
                new IceLakeEnvironment(IceLakeEnvironment.Size.LARGE)
        };
        return envs;
    }
}
