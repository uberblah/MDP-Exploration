package com.uberblah.school.gatech.ml.projects.markov;

import com.uberblah.school.gatech.ml.projects.markov.envs.DelayedGratificationEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.LavaBridgeEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.SecretPassageEnvironment;

public class MyExperimentModule implements ExperimentModule {

    @Override
    public IMyEnvironment[] getEnvironments() {
        IMyEnvironment[] envs = {
//                new BoringEnvironment(),
//                new DelayedGratificationEnvironment(),
//                new LavaBridgeEnvironment(),
                new SecretPassageEnvironment()
        };
        return envs;
    }
}
