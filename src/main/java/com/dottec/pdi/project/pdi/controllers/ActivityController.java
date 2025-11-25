package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Goal;

import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;

import java.util.List;

public class ActivityController {
    private ActivityController() {}

    public static boolean saveActivity(Activity activity){
        if(activity.getName() != null && activity.getDeadline() != null) {

            ActivityDAO.insert(activity);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("CREATE_ACTIVITY");
                log.setLogDetails("Activity created: " + activity.getName() + " for goal ID: " + activity.getGoal().getId());
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }
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

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("UPDATE_ACTIVITY");
            log.setLogDetails("Activity updated: " + activity.getName() + " (ID: " + activity.getId() + ")");
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }
}
