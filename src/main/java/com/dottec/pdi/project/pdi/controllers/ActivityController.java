package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Goal;

import java.util.List;

public class ActivityController {
    private ActivityController() {}

    public static boolean saveActivity(Activity activity){
        if(activity.getName() != null && activity.getDeadline() != null) {
            ActivityDAO.insert(activity);
            return true;
        } else {
            return false;
        }
    }

    public static List<Activity> findActivitiesByGoalId(int id){
        return ActivityDAO.findByGoalId(id);
    }

    public static List<Activity> findAllGoals() {
        return ActivityDAO.readAll();
    }

    public static void updateGoal(Activity activity){
        ActivityDAO.update(activity);
    }
}
