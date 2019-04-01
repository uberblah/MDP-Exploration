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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    public void evaluatePlanner(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath){
        Path casePath = casePath(env, plannerFactory, outputPath);
        System.out.println(String.format("EVALUATING CASE %s", casePath));

        Planner planner = plannerFactory.getPlanner(env.getDomain(), env.getHashingFactory());
        Policy p = planner.planFromState(env.getInitialState());

        PolicyUtils.rollout(p, env.getInitialState(), env.getDomain().getModel())
                .write(casePath + ".episode");
        plannerFactory.saveToFile(planner, casePath + ".planner");

        visualizePlanner(env, planner);
        //manualValueFunctionVis((ValueFunction)planner, p);
    }

    public void evaluateLearner(IMyEnvironment env, IMyLearnerFactory learnerFactory, String outputPath) {
        Path casePath = casePath(env, learnerFactory, outputPath);
        System.out.println(String.format("EVALUATING CASE %s", casePath));

        LearningAgent agent = learnerFactory.getLearnerFactory(
                env.getDomain(), env.getHashingFactory()
        ).generateAgent();

        //run learning for 50 episodes
        SimulatedEnvironment senv = env.getEnv();
        Episode bestEpisode = null;
        for(int i = 0; i < 50; i++){
            Episode e = agent.runLearningEpisode(senv);
            bestEpisode = e;

            System.out.println(i + ": " + e.maxTimeStep());

            //reset environment for next learning episode
            senv.resetEnvironment();
        }

        bestEpisode.write(casePath + ".episode");
    }

    public void savePlanner(IMyEnvironment env, IMyPlannerFactory factory, Planner planner, String outputPath) {
        Path casePath = casePath(env, factory, outputPath);
        factory.saveToFile(planner, casePath + ".planner");
    }

    public Planner loadPlanner(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath){
        Path casePath = casePath(env, plannerFactory, outputPath);
        Planner planner = plannerFactory.getPlanner(env.getDomain(), env.getHashingFactory());
        plannerFactory.loadFromFile(planner, casePath + ".planner");
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

        Path experimentPath = Paths.get(outputRoot, env.getEnvironmentName());

        LearningAgentFactory[] factories = new LearningAgentFactory[agents.length];
        for (int i = 0; i < agents.length; i++) {
            IMyLearnerFactory factoryFactory = agents[i];
            factories[i] = factoryFactory.getLearnerFactory(env.getDomain(), env.getHashingFactory());
        }

        LearningAlgorithmExperimenter exp = LearningAlgorithmExperimenter.class.getDeclaredConstructor(
                Environment.class,
                int.class,
                int.class,
                LearningAgentFactory[].class
        ).newInstance(
                env.getEnv(), 50, 100, factories
        );

        exp.setUpPlottingConfiguration(
                500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD
        );

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV(experimentPath + ".experiment");
    }

    public void experiment(String outputRoot) throws Exception {

        IMyEnvironment[] envs = module.getEnvironments();
        IMyPlannerFactory[] planners = module.getPlanners();
        IMyLearnerFactory[] learners = module.getLearners();

        for (IMyEnvironment myEnv : envs) {

            for (IMyPlannerFactory myPlanner : planners) {
                /*
                TODO: TRAIN THE PLANNER
                iterations to convergence
                time to convergence
                what the planner converged to
                 */
                evaluatePlanner(myEnv, myPlanner, outputRoot);
            }
            // TODO: DO THE EXPERIMENT WITH THE LEARNERS
            experimentAndPlotter(myEnv, outputRoot, learners);
            for (IMyLearnerFactory factoryFactory : learners) {
                /*
                TODO: TRAIN THE LEARNER
                episodes to convergence
                time to convergence
                what the learner converged to
                 */
            }

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