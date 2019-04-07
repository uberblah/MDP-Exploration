package com.uberblah.school.gatech.ml.projects.markov;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.mdp.core.state.State;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.util.ExperimentModule;
import com.uberblah.school.gatech.ml.projects.markov.util.MyExperimentModule;
import com.uberblah.school.gatech.ml.projects.markov.util.Pathy;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Examiner {

    private ExperimentModule module;
    private Pathy pathy;

    private Examiner() {
        module = new MyExperimentModule();
        pathy = new Pathy();
    }

    public void examine() throws Exception {
        for (IMyEnvironment myEnv : module.getEnvironments()) {
            Path policyFilePath = pathy.envFilePath(myEnv, "policies.yaml");
            System.out.println(policyFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(policyFilePath.toString()));

            Map<State, Policy> policies = (Map<State, Policy>)new Yaml().load(reader);
            GreedyQPolicy viPolicy = (GreedyQPolicy)policies.get("ValueIteration");
            GreedyQPolicy piPolicy = (GreedyQPolicy)policies.get("PolicyIteration");

            List<State> allStates = StateReachability.getReachableStates(
                    myEnv.getInitialState(), myEnv.getDomain(), myEnv.getHashingFactory());
            for (State s : allStates) {
                System.out.println(s);
//                    try {
                    System.out.println(viPolicy.definedFor(s));
                    System.out.println(piPolicy.definedFor(s));
                    List<ActionProb> via = viPolicy.policyDistribution(s);
                    List<ActionProb> pia = piPolicy.policyDistribution(s);
                    System.out.println(via == pia);
                    System.out.println(via);
                    System.out.println(pia);
//                    } catch (NullPointerException e) {
//                        System.out.println("ONLY ONE IS DEFINED FOR IT");
//                    }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Examiner ex = new Examiner();
        ex.examine();
    }

}
