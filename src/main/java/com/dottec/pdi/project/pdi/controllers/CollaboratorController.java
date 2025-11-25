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

    private CollaboratorController(){}

    public static void saveCollaborator(String name, String cpf, String email, Department department) {
        Collaborator collaborator = new Collaborator();
        collaborator.setName(name);
        collaborator.setCpf(cpf);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(active); // Define o status padrão como ativo

        CollaboratorDAO.insert(collaborator);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("CREATE_COLLABORATOR");
            log.setLogDetails("Collaborator created: " + name + " (CPF: " + cpf + ")");
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
            log.setLogAction("UPDATE_COLLABORATOR");
            log.setLogDetails("Collaborator updated: " + collaborator.getName() + " (ID: " + collaborator.getId() + ")");
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }

    public static void deleteCollaboratorById(int id) {
        CollaboratorDAO.deleteById(id);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("INACTIVATE_COLLABORATOR");
            log.setLogDetails("Collaborator inactivated (soft delete): ID " + id);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }
}