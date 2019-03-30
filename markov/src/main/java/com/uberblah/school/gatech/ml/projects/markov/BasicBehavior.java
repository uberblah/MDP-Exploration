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
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
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
import com.uberblah.school.gatech.ml.projects.markov.envs.BoringEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.DelayedGratificationEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.IMyEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.LavaBridgeEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.envs.SecretPassageEnvironment;
import com.uberblah.school.gatech.ml.projects.markov.planners.IMyPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.PolicyIterationPlannerFactory;
import com.uberblah.school.gatech.ml.projects.markov.planners.ValueIterationPlannerFactory;

import java.awt.Color;
import java.util.List;

public class BasicBehavior {

    private static final String caseDelimiter = "-";
    private String buildCaseName(String... parts) {
        return String.join(caseDelimiter, parts);
    }

    private GridWorldDomain gwdg;
    private OOSADomain domain;
    private TerminalFunction tf;
    private StateConditionTest goalCondition;
    private State initialState;
    private HashableStateFactory hashingFactory;
    private SimulatedEnvironment env;

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


        //VisualActionObserver observer = new VisualActionObserver(domain,
        //	GridWorldVisualizer.getVisualizer(gwdg.getMap()));
        //observer.initGUI();
        //env.addObservers(observer);
    }

    public void visualize(String outputpath){
        Visualizer v = GridWorldVisualizer.getVisualizer(gwdg.getMap());
        new EpisodeSequenceVisualizer(v, domain, outputpath);
    }

    public void evaluatePlanner(IMyEnvironment env, IMyPlannerFactory plannerFactory, String outputPath){

        String caseName = buildCaseName(env.getEnvironmentName(), plannerFactory.getPlannerName());
        System.out.println(caseName);

        Planner planner = plannerFactory.getPlanner(domain, hashingFactory);
        Policy p = planner.planFromState(initialState);

        PolicyUtils.rollout(p, initialState, domain.getModel())
                .write(outputPath + caseName);

        simpleValueFunctionVis((ValueFunction)planner, p);
        //manualValueFunctionVis((ValueFunction)planner, p);
    }

    public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p){

        List<State> allStates = StateReachability.getReachableStates(
                initialState, domain, hashingFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
                allStates, 11, 11, valueFunction, p);
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
        ((FactoredModel)domain.getModel()).setRf(new GoalBasedRF(this.goalCondition, 5.0, -0.1));

        /*
         * Create factories for Q-learning agents
         */
        LearningAgentFactory epsilonQFactory = new LearningAgentFactory() {
            public String getAgentName() {
                return "Epsilon Q";
            }
            public LearningAgent generateAgent() {
                return new QLearning(domain, 0.999, hashingFactory, 0.0, 0.9, 1000);
            }
        };

        LearningAgentFactory optimisticQFactory = new LearningAgentFactory() {
            public String getAgentName() {
                return "Optimistic Q";
            }
            public LearningAgent generateAgent() {
                QLearning agent = new QLearning(domain, 0.999, hashingFactory, 0.0, 0.9, 1000);
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

    public void experiment(String outputPath) {

        IMyEnvironment[] envs = {
                new DelayedGratificationEnvironment(),
                new LavaBridgeEnvironment(),
                new SecretPassageEnvironment()
        };

        IMyPlannerFactory[] planners = {
                ValueIterationPlannerFactory.builder().build(),
                PolicyIterationPlannerFactory.builder().build()
        };

        LearningAgentFactory[] learners = {
                new LearningAgentFactory() {
                    public String getAgentName() {
                        return "Epsilon Q";
                    }
                    public LearningAgent generateAgent() {
                        return new QLearning(domain, 0.999, hashingFactory, 0.0, 0.9, 1000);
                    }
                },
                new LearningAgentFactory() {
                    public String getAgentName() {
                        return "Optimistic Q";
                    }
                    public LearningAgent generateAgent() {
                        QLearning agent = new QLearning(domain, 0.999, hashingFactory, 0.0, 0.9, 1000);
                        agent.setLearningPolicy(new GreedyQPolicy(agent));
                        return agent;
                    }
                }
        };

        for (IMyEnvironment myEnv : envs) {
            for (IMyPlannerFactory myPlanner : planners) {
                /*
                TODO: TRAIN THE PLANNER
                iterations to convergence
                time to convergence
                what the planner converged to
                 */
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
        }
    }

    public static void main(String[] args) {

        BasicBehavior example = new BasicBehavior();
        String outputPath = "output/";

        example.evaluatePlanner(
                new BoringEnvironment(),
                PolicyIterationPlannerFactory.builder().build(),
                outputPath
        );
        example.evaluatePlanner(
                new BoringEnvironment(),
                ValueIterationPlannerFactory.builder().build(),
                outputPath
        );

//        example.experiment(outputPath);

//        example.experimentAndPlotter(outputPath);

//        example.visualize(outputPath);

    }

}