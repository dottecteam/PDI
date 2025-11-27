package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.utils.GoalValidator;

import java.util.List;

public final class GoalController {

    private GoalController() {
    }

    public static boolean saveGoal(Goal goal) {
        if (GoalValidator.isValid(goal)) {
            GoalDAO.insert(goal);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("create_goal");
                String details = String.format("{\"goal_id\": %d, \"goal_name\": \"%s\", \"collaborator_id\": %d, \"log_message\": \"Goal created\"}",
                        goal.getId(), goal.getName(), goal.getCollaborator().getId());
                log.setLogDetails(details);
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }

            return true;
        } else {
            return false;
        }
    }

    public static List<Goal> findGoalsByCollaborator(int collaboratorId) {
        return GoalDAO.findByCollaboratorId(collaboratorId);
    }

    public static List<Goal> findAllGoals() {
        return GoalDAO.readAll();
    }

    public static void updateGoal(Goal goal) {
        GoalDAO.update(goal);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("update_goal");
            String details = String.format("{\"goal_id\": %d, \"goal_name\": \"%s\", \"goal_status\": \"%s\", \"log_message\": \"Goal updated\"}",
                    goal.getId(), goal.getName(), goal.getStatus().name());
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }

    public static void assignGoalToCollaborator(Goal goal, Collaborator collaborator) {
        System.out.println("Associando meta '" + goal.getName() + "' ao colaborador '" + collaborator.getName() + "'");
        // Exemplo:
        // goal.setCollaborator(collaborator);
        // goal.setStatus(GoalStatus.IN_PROGRESS); // Define como "em andamento"
        // GoalDAO.update(goal); // ou um método específico para associação
    }
}