package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.model.Goal;

public class GoalViewModel {
    private Goal goal;

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void createGoal(){
        this.goal = new Goal();
    }
}
