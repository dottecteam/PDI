package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityTemplateDAO;
import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.dao.GoalTemplatesDAO;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.GoalTemplate;
import com.dottec.pdi.project.pdi.utils.GoalValidator;

import java.util.List;

public class ActivityTemplateController {
    private ActivityTemplateController() {}

    public static void saveActivityTemplate(ActivityTemplate activityTemplate){
        ActivityTemplateDAO.insert(activityTemplate);
    }

    public static List<ActivityTemplate> findActivityByGoalTemplateId(int goalTemplateId) {
        return ActivityTemplateDAO.findByGoalTemplateId(goalTemplateId);
    }

    public static List<ActivityTemplate> findAllActivityTemplates() {
        return ActivityTemplateDAO.readAll();
    }

    public static void updateActivityTemplate(ActivityTemplate activityTemplate){
        ActivityTemplateDAO.update(activityTemplate);
    }

    public static void deleteActivityTemplate(ActivityTemplate activityTemplate){
        ActivityTemplateDAO.delete(activityTemplate);
    }
}