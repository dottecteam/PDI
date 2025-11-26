package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.NotificationCreatorDAO;
import com.dottec.pdi.project.pdi.dao.NotificationDAO;
import com.dottec.pdi.project.pdi.enums.NotificationType;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Notification;
import com.dottec.pdi.project.pdi.model.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class NotificationCreationController {

    private NotificationCreationController() {}
    
    public static void createExpirationNotifications(User user) {
        if (user == null) {
            System.err.println("Usuário nulo ao criar notificações.");
            return;
        }

        List<Activity> expiringActivities;

        if (user.getRole() == Role.department_manager) {
            if (user.getDepartment() == null) return;
            expiringActivities = NotificationCreatorDAO.findExpiringActivitiesByDepartment(user.getDepartment().getId());
        } else if (user.getRole() == Role.hr_manager || user.getRole() == Role.general_manager) {
            expiringActivities = NotificationCreatorDAO.findAllExpiringActivities();
        } else {
            return;
        }

        for (Activity activity : expiringActivities) {
            long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), activity.getDeadline());

            String message = String.format(
                    "A atividade '%s' (Colaborador: %s, Setor: %s) está próxima do prazo final (%d dias).",
                    activity.getName(),
                    activity.getGoal().getCollaborator().getName(),
                    activity.getGoal().getCollaborator().getDepartment().getName(),
                    daysUntilDeadline
            );

            Notification notification = new Notification();
            notification.setNotMessage(message);
            notification.setNotType(NotificationType.deadline);
            notification.setNotIsRead(false);
            notification.setUserId(user.getId());

            NotificationDAO.insert(notification);
        }
    }
}