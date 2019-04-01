package com.uberblah.school.gatech.ml.projects.markov;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BasicBehavior {

    private GridWorldDomain gwdg;
    private OOSADomain domain;
    private TerminalFunction tf;
    private StateConditionTest goalCondition;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

    private ExperimentModule module;

    public BasicBehavior(){
        gwdg = new GridWorldDomain(11, 11);
        gwdg.setMapToFourRooms();
        tf = new GridWorldTerminalFunction(10, 10);
        gwdg.setTf(tf);
        goalCondition = new TFGoalCondition(tf);
        domain = gwdg.generateDomain();

        initialState = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);

        module = new MyExperimentModule();
        //VisualActionObserver observer = new VisualActionObserver(domain,
        //	GridWorldVisualizer.getVisualizer(gwdg.getMap()));
        //observer.initGUI();
        //env.addObservers(observer);
    }

    public void visualize(IMyEnvironment env, String outputPath){
        Path envPath = Paths.get(outputPath, env.getEnvironmentName());
        Visualizer v = GridWorldVisualizer.getVisualizer(env.getMap());
        new EpisodeSequenceVisualizer(v, env.getDomain(), envPath.toString());
    }

    public Path casePath(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath) {
        return Paths.get(outputPath, env.getEnvironmentName(), plannerFactory.getPlannerName());
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

    public void manualValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(
                initialState, domain, hashingFactory);

        //define color function
        LandmarkColorBlendInterpolation rb = new LandmarkColorBlendInterpolation();
        rb.addNextLandMark(0., Color.RED);
        rb.addNextLandMark(1., Color.BLUE);

        //define a 2D painter of state values,
        //specifying which attributes correspond to the x and y coordinates of the canvas
        StateValuePainter2D svp = new StateValuePainter2D(rb);
        svp.setXYKeys("agent:x", "agent:y",
                new VariableDomain(0, 11), new VariableDomain(0, 11),
                1, 1);

        //create our ValueFunctionVisualizer that paints for all states
        //using the ValueFunction source and the state value painter we defined
        ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(
                allStates, svp, valueFunction);

        //define a policy painter that uses arrow glyphs for each of the grid world actions
        PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
        spp.setXYKeys("agent:x", "agent:y", new VariableDomain(0, 11),
                new VariableDomain(0, 11),
                1, 1);

        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_NORTH, new ArrowActionGlyph(0));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_SOUTH, new ArrowActionGlyph(1));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_EAST, new ArrowActionGlyph(2));
        spp.setActionNameGlyphPainter(GridWorldDomain.ACTION_WEST, new ArrowActionGlyph(3));
        spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);

        //add our policy renderer to it
        gui.setSpp(spp);
        gui.setPolicy(p);

        //set the background color for places where states are not rendered to grey
        gui.setBgColor(Color.GRAY);

        //start it
        gui.initGUI();
    }

    public void experimentAndPlotter(String outputPath){

        //different reward function for more structured performance plots
        ((FactoredModel)domain.getModel()).setRf(
                new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        /*
         * Create factories for Q-learning agents
         */
        LearningAgentFactory epsilonQFactory = new LearningAgentFactory() {
            public String getAgentName() {
                return "Epsilon Q";
            }
            public LearningAgent generateAgent() {
                return new QLearning(
                        domain,
                        0.999,
                        hashingFactory,
                        0.0,
                        0.9,
                        1000
                );
            }
        };

        LearningAgentFactory optimisticQFactory = new LearningAgentFactory() {
            public String getAgentName() {
                return "Optimistic Q";
            }
            public LearningAgent generateAgent() {
                QLearning agent = new QLearning(
                        domain,
                        0.999, hashingFactory,
                        0.0,
                        0.9,
                        1000
                );
                agent.setLearningPolicy(new GreedyQPolicy(agent));
                return agent;
            }
        };

        LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(
                env, 50, 100, epsilonQFactory, optimisticQFactory);
        exp.setUpPlottingConfiguration(
                500, 250, 2, 1000,
                TrialMode.MOST_RECENT_AND_AVERAGE,
                PerformanceMetric.CUMULATIVE_STEPS_PER_EPISODE,
                PerformanceMetric.AVERAGE_EPISODE_REWARD
        );

        exp.startExperiment();
        exp.writeStepAndEpisodeDataToCSV(outputPath + "expData");
    }

    public void experiment(String outputRoot) {

        IMyEnvironment[] envs = module.getEnvironments();
        IMyPlannerFactory[] planners = module.getPlanners();

        for (IMyEnvironment myEnv : envs) {
            LearningAgentFactory[] learners = module.getLearners(myEnv.getDomain(), myEnv.getHashingFactory());

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
            for (LearningAgentFactory agentFactory : learners) {
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

    public static void main(String[] args) {

        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/";

        example.experiment(outputPath);

//        example.experimentAndPlotter(outputPath);

    }

}