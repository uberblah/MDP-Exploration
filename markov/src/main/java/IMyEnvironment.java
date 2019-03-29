import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;

public interface IMyEnvironment {
    String getEnvironmentName();
    StateConditionTest getStateConditionTest();
    SimulatedEnvironment getSimulatedEnvironment();
    HashableStateFactory getHashableStateFactory();
}
