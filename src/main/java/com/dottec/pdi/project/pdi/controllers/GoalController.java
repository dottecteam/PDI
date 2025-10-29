package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.utils.GoalValidator;
import com.dottec.pdi.project.pdi.viewmodel.GoalViewModel;

import java.util.List;

public final class GoalController {

    private GoalController() {}

    public static boolean saveGoal(Goal goal){
        if(GoalValidator.isValid(goal)){
            GoalDAO.insert(goal);
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

    public static void updateGoal(Goal goal){
        GoalDAO.update(goal);
    }

    public static void assignGoalToCollaborator(Goal goal, Collaborator collaborator) {
        System.out.println("Associando meta '" + goal.getName() + "' ao colaborador '" + collaborator.getName() + "'");
        // Exemplo:
        // goal.setCollaborator(collaborator);
        // goal.setStatus(GoalStatus.IN_PROGRESS); // Define como "em andamento"
        // GoalDAO.update(goal); // ou um método específico para associação
    }
}