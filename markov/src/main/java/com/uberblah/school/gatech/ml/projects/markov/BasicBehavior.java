package com.uberblah.school.gatech.ml.projects.markov;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.VisualActionObserver;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.visualizer.Visualizer;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.learners.IMyLearnerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.util.MyExperimenter;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicBehavior {

    private ExperimentModule module;

    public BasicBehavior(){
        module = new MyExperimentModule();
    }

    public void addObserver(IMyEnvironment env) {
        VisualActionObserver observer = new VisualActionObserver(env.getDomain(),
        	GridWorldVisualizer.getVisualizer(env.getMap()));
        observer.initGUI();
        env.getEnv().addObservers(observer);
    }

    public void visualize(IMyEnvironment env, String outputPath){
        Path envPath = Paths.get(outputPath, env.getEnvironmentName());
        Visualizer v = GridWorldVisualizer.getVisualizer(env.getMap());
        new EpisodeSequenceVisualizer(v, env.getDomain(), envPath.toString());
    }

    public Path casePath(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath) {
        return Paths.get(outputPath, env.getEnvironmentName(), plannerFactory.getPlannerName());
    }

    public Path casePath(IMyEnvironment env, IMyLearnerFactory learnerFactory, String outputPath) {
        return Paths.get(outputPath, env.getEnvironmentName(), learnerFactory.getLearnerName());
    }

    public Path envPath(IMyEnvironment env, String outputPath) {
        return Paths.get(outputPath, env.getEnvironmentName());
    }

    public Policy evaluatePlanner(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath){
        Path casePath = casePath(env, plannerFactory, outputPath);
        System.out.println(String.format("EVALUATING CASE %s", casePath));

        Planner planner = plannerFactory.getPlanner(env.getDomain(), env.getHashingFactory());
        Long startTime = System.nanoTime();
        Policy policy = planner.planFromState(env.getInitialState());
        Long endTime = System.nanoTime();
        Long dt = endTime - startTime;
        System.out.println(String.format("TRAINING TIME = %d", dt));

        for (int i = 0; i < 10; i++) {
            PolicyUtils.rollout(policy, env.getInitialState(), env.getDomain().getModel())
                    .write(casePath + String.valueOf(i) + ".episode");
        }
        plannerFactory.saveToFile(planner, casePath + ".yaml");

        visualizePlanner(env, planner);

        return policy;
        //manualValueFunctionVis((ValueFunction)planner, p);
    }

    public Policy evaluateLearner(IMyEnvironment env, IMyLearnerFactory learnerFactory, String outputPath) {
        Path casePath = casePath(env, learnerFactory, outputPath);
        System.out.println(String.format("EVALUATING CASE %s", casePath));

        LearningAgent agent = learnerFactory.getLearnerFactory(
                env.getDomain(), env.getHashingFactory()
        ).generateAgent();

        SimulatedEnvironment senv = env.getEnv();
        Episode bestEpisode = null;
        Double bestScore = null;
        Long startTime = System.nanoTime();
        for(int i = 0; i < env.getNumEpisodes(); i++){
            Episode e = agent.runLearningEpisode(senv);

            double score = e.rewardSequence.stream().reduce(0.0, (x,y) -> x + y);
            if (bestScore == null || score > bestScore) {
                bestScore = score;
                bestEpisode = e;
            }

            //reset environment for next learning episode
            senv.resetEnvironment();
        }
        Long endTime = System.nanoTime();
        Long dt = endTime - startTime;
        System.out.println(String.format("TRAINING TIME = %d", dt));
        learnerFactory.saveToFile(agent, casePath + ".yaml");

        for(int i = 0; i < 10; i++) {
            Episode e = agent.runLearningEpisode(senv);
            senv.resetEnvironment();
            e.write(casePath + String.valueOf(i) + ".episode");
        }

        System.out.println(String.format("CASE %s GOT %f", casePath, bestScore));
        bestEpisode.write(casePath + ".bestEpisode");

        return learnerFactory.planFromState(agent, env.getInitialState());
    }

    public void savePlanner(IMyEnvironment env, IMyPlannerFactory factory, Planner planner, String outputPath) {
        Path casePath = casePath(env, factory, outputPath);
        factory.saveToFile(planner, casePath + ".yaml");
    }

    public Planner loadPlanner(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath){
        Path casePath = casePath(env, plannerFactory, outputPath);
        Planner planner = plannerFactory.getPlanner(env.getDomain(), env.getHashingFactory());
        plannerFactory.loadFromFile(planner, casePath + ".yaml");
        return planner;
    }

    public void visualizePlanner(IMyEnvironment env, Planner planner) {
        Policy p = planner.planFromState(env.getInitialState());
        List<State> allStates = StateReachability.getReachableStates(
                env.getInitialState(), env.getDomain(), env.getHashingFactory());
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
                allStates, env.getWidth(), env.getHeight(), (ValueFunction)planner, p);
        gui.initGUI();
    }

    public void experimentAndPlotter(IMyEnvironment env, String outputRoot, IMyLearnerFactory[] agents) throws Exception {

        Path experimentPath = envPath(env, outputRoot);

        LearningAgentFactory[] factories = new LearningAgentFactory[agents.length];
        for (int i = 0; i < agents.length; i++) {
            IMyLearnerFactory factoryFactory = agents[i];
            System.out.println(String.format("ADDING LEARNER FACTORY %s", agents[i].getLearnerName()));
            factories[i] = factoryFactory.getLearnerFactory(env.getDomain(), env.getHashingFactory());
        }
        System.out.println(String.format("NUMBER OF LEARNER FACTORIES: %d", factories.length));

        LearningAlgorithmExperimenter exp = MyExperimenter.class.getDeclaredConstructor(
                Environment.class,
                int.class,
                int.class,
                LearningAgentFactory[].class
        ).newInstance(
                env.getEnv(), env.getNumTrials(), env.getNumEpisodes(), factories
        );

        exp.setUpPlottingConfiguration(
                500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_REWARD_PER_EPISODE,
                PerformanceMetric.STEPS_PER_EPISODE
        );

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV(experimentPath + ".experiment");
    }

    public void experiment(String outputRoot) throws Exception {

        IMyEnvironment[] envs = module.getEnvironments();

        for (IMyEnvironment myEnv : envs) {
            System.out.println(String.format("EXPERIMENTING WITH %s", myEnv.getEnvironmentName()));

//            addObserver(myEnv);

            IMyPlannerFactory[] planners = myEnv.getPlanners();
            Map<String, Policy> policies = new HashMap<String, Policy>();
            for (IMyPlannerFactory myPlanner : planners) {
                /*
                TODO: TRAIN THE PLANNER
                iterations to convergence
                time to convergence
                what the planner converged to
                 */
                policies.put(myPlanner.getPlannerName(), evaluatePlanner(myEnv, myPlanner, outputRoot));
            }

            IMyLearnerFactory[] learners = myEnv.getLearners();
            System.out.println(String.format("NUMBER OF LEARNERS = %d", learners.length));
            experimentAndPlotter(myEnv, outputRoot, learners);

            for (IMyLearnerFactory factoryFactory : learners) {
                /*
                TODO: TRAIN THE LEARNER
                episodes to convergence
                time to convergence
                what the learner converged to
                 */
                policies.put(factoryFactory.getLearnerName(), evaluateLearner(myEnv, factoryFactory, outputRoot));
            }

            // write the policies all together so we can compare them by name
            Path policyFilePath = Paths.get(envPath(myEnv, outputRoot).toString(), "policies.yaml");
            BufferedWriter writer = new BufferedWriter(new FileWriter(policyFilePath.toString()));
            new Yaml().dump(policies, writer);
            writer.close();

            visualize(myEnv, outputRoot);
        }
    }

    public static void main(String[] args) throws Exception {

        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/";

        example.experiment(outputPath);

    }

//    public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){
//
//        List<State> allStates = StateReachability.getReachableStates(
//                initialState, domain, hashingFactory);
//
//        //define color function
//        LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
//        rb.addNextLandMark(0., Color.RED);
//        rb.addNextLandMark(1., Color.BLUE);
//
//        //define a 2D painter of state values,
//        //specifying which attributes correspond to the x and y coordinates of the canvas
//        StateValuePainter2D svp = new StateValuePainter2D(rb);
//        svp.setXYKeys("agent:x", "agent:y",
//                new VariableDomain(0, 11), new VariableDomain(0, 11),
//                1, 1);
//
//        //create our ValueFunctionVisualizer that paints for all states
//        //using the ValueFunction source and the state value painter we defined
//        ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(
//                allStates, svp, valueFunction);
//
//        //define a policy painter that uses arrow glyphs for each of the grid world actions
//        PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
//        spp.setXYKeys("agent:x", "agent:y", new VariableDomain(0, 11),
//                new VariableDomain(0, 11),
//                1, 1);
//
//        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
//        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
//        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
//        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
//        spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);
//
//        //add our policy renderer to it
//        gui.setSpp(spp);
//        gui.setPolicy(p);
//
//        //set the background color for places where states are not rendered to grey
//        gui.setBgColor(Color.GRAY);
//
//        //start it
//        gui.initGUI();
//    }

}