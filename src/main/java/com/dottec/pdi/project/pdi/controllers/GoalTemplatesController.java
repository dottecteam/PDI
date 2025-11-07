package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.GoalTemplatesDAO;
import com.dottec.pdi.project.pdi.model.GoalTemplates;
import java.util.List;

public final class GoalTemplatesController{

    private GoalTemplatesController(){}

    private static boolean isValid(GoalTemplates goalTemplate) {
        return goalTemplate != null 
            && goalTemplate.getGoa_tmp_name() != null && !goalTemplate.getGoa_tmp_name().isBlank()
            && goalTemplate.getGoa_tmp_description() != null && !goalTemplate.getGoa_tmp_description().isBlank();
    }

    public static boolean addGoalTemplate(GoalTemplates goalTemplate) {
        if (isValid(goalTemplate)) {
            GoalTemplatesDAO.insert(goalTemplate);
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

    public static GoalTemplates findGoalTemplateById(int id) {
        return GoalTemplatesDAO.findById(id);
    }

    public static List<GoalTemplates> findAllGoalTemplates() {
        return GoalTemplatesDAO.readAll();
    }

    public static void updateGoalTemplate(int id, String newName, String newDescription) {
        GoalTemplates existingGoal = GoalTemplatesDAO.findById(id);
        if (existingGoal != null) {
            existingGoal.setGoa_tmp_name(newName);
            existingGoal.setGoa_tmp_description(newDescription);
            GoalTemplatesDAO.update(existingGoal);
        } 
        else {
            System.err.println("Erro: GoalTemplate com ID " + id + " não encontrado para atualização.");
        }
    }
}