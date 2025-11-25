package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Goal;

import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;

import java.util.List;

public class ActivityController {
    private ActivityController() {
    }

    public static boolean saveActivity(Activity activity) {
        if (activity.getName() != null && activity.getDeadline() != null) {

            ActivityDAO.insert(activity);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("create_activity");
                String details = String.format("{\"activity_id\": %d, \"activity_name\": \"%s\", \"goal_id\": %d, \"log_message\": \"Activity created\"}",
                        activity.getId(), activity.getName(), activity.getGoal().getId());
                log.setLogDetails(details);
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }
            return true;
        } else {
            return false;
        }
    }

    public static List<Activity> findActivitiesByGoalId(int id) {
        return ActivityDAO.findByGoalId(id);
    }

    public static List<Activity> findAllGoals() {
        return ActivityDAO.readAll();
    }

    public static void updateGoal(Activity activity) {
        ActivityDAO.update(activity);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("update_activity");
            String details = String.format("{\"activity_id\": %d, \"activity_name\": \"%s\", \"activity_status\": \"%s\", \"log_message\": \"Activity updated\"}",
                    activity.getId(), activity.getName(), activity.getStatus().name());
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }
}
