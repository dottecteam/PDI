package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;

import java.util.List;

import static com.dottec.pdi.project.pdi.enums.CollaboratorStatus.active;

public class CollaboratorController {

    private CollaboratorController() {
    }

    public static void saveCollaborator(String name, String cpf, String email, Department department) {
        Collaborator collaborator = new Collaborator();
        collaborator.setName(name);
        collaborator.setCpf(cpf);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(active);

        CollaboratorDAO.insert(collaborator);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("create_collaborator");
            String details = String.format("{\"col_name\": \"%s\", \"col_cpf\": \"%s\", \"dep_name\": \"%s\", \"log_message\": \"Collaborator created\"}",
                    name, cpf, department != null ? department.getName() : "N/A");
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }

    }

    public static List<Collaborator> findAllCollaborators() {
        return CollaboratorDAO.readAll();
    }

    public static void updateCollaborator(Collaborator collaborator) {
        CollaboratorDAO.update(collaborator);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("update_collaborator");
            String details = String.format("{\"col_id\": %d, \"col_name\": \"%s\", \"dep_id\": %d, \"log_message\": \"Collaborator data updated\"}",
                    collaborator.getId(), collaborator.getName(), collaborator.getDepartment() != null ? collaborator.getDepartment().getId() : 0);
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }

    public static void deleteCollaboratorById(int id) {
        CollaboratorDAO.deleteById(id);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("inactivate_collaborator");
            String details = String.format("{\"col_id\": %d, \"log_message\": \"Collaborator soft deleted\"}", id);
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }
}