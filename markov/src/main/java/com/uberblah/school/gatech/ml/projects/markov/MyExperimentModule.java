package com.uberblah.school.gatech.ml.projects.markov;

import com.uberblah.school.gatech.ml.projects.markov.envs.DelayedGratificationEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.HeavenStaircaseEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;

public class MyExperimentModule implements ExperimentModule {

    @Override
    public IMyEnvironment[] getEnvironments() {
        IMyEnvironment[] envs = {
//                new BoringEnvironment(),
//                new DelayedGratificationEnvironment(),
                new HeavenStaircaseEnvironment(),
//                new SecretPassageEnvironment()
        };
        return envs;
    }
}
