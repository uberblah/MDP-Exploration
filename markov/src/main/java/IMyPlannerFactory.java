import burlap.behavior.singleagent.planning.Planner;

public interface IMyPlannerFactory {
    String getPlannerName();
    Planner getPlanner();
}
