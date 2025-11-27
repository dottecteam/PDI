package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.dao.AttachmentDAO;
import com.dottec.pdi.project.pdi.dao.GoalDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ActivityController {
    private ActivityController() {
    }

    public static boolean saveActivity(Activity activity) {
        if (activity.getName() != null && activity.getDeadline() != null) {
            ActivityDAO.insert(activity);
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
    }

    public static boolean saveAttachment(Activity activity, File file) {
        try {
            String fileName = file.getName();

            String uploadBasePath = "uploads/activities/" + activity.getId();

            // Cria a pasta se não existir
            File directory = new File(uploadBasePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destination = new File(directory, fileName);

            Files.copy(
                    file.toPath(),
                    destination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            String simulatedFilePath = "/uploads/activities/" + activity.getId() + "/" + fileName;

            User currentUser = AuthController.getInstance().getLoggedUser();
            if (currentUser == null) {
                System.err.println("Erro: Nenhum usuário logado para registrar o anexo.");
                return false;
            }

            Attachment attachment = new Attachment();
            attachment.setFilePath(simulatedFilePath);
            attachment.setActivity(activity);
            attachment.setGoal(activity.getGoal());
            attachment.setUploadedBy(currentUser);

            AttachmentDAO.insert(attachment);
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao salvar anexo: " + e.getMessage());
            return false;
        }
    }

}
