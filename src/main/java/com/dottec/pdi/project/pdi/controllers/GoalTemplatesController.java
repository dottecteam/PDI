package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.GoalTemplatesDAO;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.GoalTemplate;
import java.util.List;

public final class GoalTemplatesController{

    private GoalTemplatesController(){}

    private static boolean isValid(GoalTemplate goalTemplate) {
        return goalTemplate != null
            && goalTemplate.getGoa_tmp_name() != null && !goalTemplate.getGoa_tmp_name().isBlank()
            && goalTemplate.getGoa_tmp_description() != null && !goalTemplate.getGoa_tmp_description().isBlank();
    }

    public static boolean addGoalTemplate(GoalTemplate goalTemplate) {
        if (isValid(goalTemplate)) {
            GoalTemplatesDAO.insert(goalTemplate);
            if (goalTemplate.getActivityTemplates() != null) {
                for (ActivityTemplate activityTemplate : goalTemplate.getActivityTemplates()) {
                    ActivityTemplateController.saveActivityTemplate(activityTemplate);
                }
            }
            System.out.println("GoalTemplate '" + goalTemplate.getGoa_tmp_name() + "' inserido com sucesso!");
            return true;
        }
        else {
            System.err.println("Erro: GoalTemplate inválido.");
            return false;
        }
    }

    public static void deleteGoalTemplate(int id) {
        GoalTemplatesDAO.deleteById(id);
        System.out.println("GoalTemplate com ID " + id + " removido com sucesso!");
    }

    public static GoalTemplate findGoalTemplateById(int id) {
        GoalTemplate goalTemplate = GoalTemplatesDAO.findById(id);
        if(goalTemplate != null) {
            goalTemplate.setActivityTemplates(
                    ActivityTemplateController.findActivityByGoalTemplateId(goalTemplate.getGoa_tmp_id())
            );
        }
        return goalTemplate;
    }

    public static List<GoalTemplate> findAllGoalTemplates() {
        List<GoalTemplate> goalTemplates = GoalTemplatesDAO.readAll();
        return goalTemplates;
    }

    public static void updateGoalTemplate(GoalTemplate goalTemplate) {
        if (goalTemplate != null) {
            GoalTemplatesDAO.update(goalTemplate);
            if (goalTemplate.getActivityTemplates() != null) {
                for (ActivityTemplate activityTemplate : goalTemplate.getActivityTemplates()) {
                    ActivityTemplateController.updateActivityTemplate(activityTemplate);
                }
            }
        }
        else {
            System.err.println("Erro: GoalTemplate com ID " + goalTemplate.getGoa_tmp_id() + " não encontrado para atualização.");
        }
    }
}