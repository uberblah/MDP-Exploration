package com.uberblah.school.gatech.ml.projects.markov.util;

import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.mdp.singleagent.environment.Environment;

public class MyExperimenter extends LearningAlgorithmExperimenter {
    public MyExperimenter(Environment testEnvironment, int nTrials, int trialLength, LearningAgentFactory...agentFactories){
        super(testEnvironment, nTrials, trialLength, agentFactories);
        System.out.println(String.format("EXPERIMENTER GOT %d FACTORIES", agentFactories.length));
        for (LearningAgentFactory factory : agentFactories) {
            System.out.println(String.format("  NAME: %s", factory.getAgentName()));
        }
    }
}
