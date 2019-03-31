package com.uberblah.school.gatech.ml.projects.markov.planners;

import burlap.behavior.singleagent.planning.Planner;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;

// the player
// input/output: how the user inputs actions, how we display the world
// mechanics: what actions are available, what the actions do in the game world

public interface IMyPlannerFactory {
    String getPlannerName();
    Planner getPlanner(SADomain domain, HashableStateFactory hashingFactory);
    void saveToFile(Planner planner, String fileName);
    Planner loadFromFile(Planner planner, String fileName);
}
