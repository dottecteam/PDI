package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.dao.AttachmentDAO;
import com.dottec.pdi.project.pdi.model.Activity;

import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.User;

import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.Tag;

import com.dottec.pdi.project.pdi.model.Log;

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

    public static List<Activity> findAllActivities() {
        return ActivityDAO.readAll();
    }

    public static void updateActivity(Activity activity){
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

    public static boolean deleteAttachment(Attachment attachment) {
        if (attachment == null || attachment.getId() == 0) {
            return false;
        }

        try {
            String filePath = attachment.getFilePath().substring(1);
            File fileToDelete = new File(filePath);

            if (fileToDelete.exists()) {
                Files.delete(fileToDelete.toPath());
                System.out.println("Arquivo físico deletado: " + filePath);
            } else {
                System.err.println("Aviso: Arquivo físico não encontrado para exclusão: " + filePath);
            }

            AttachmentDAO.deleteById(attachment.getId());

            return true;

        } catch (Exception e) {
            System.err.println("Erro ao deletar anexo: " + e.getMessage());
            return false;
        }
    }

}
