package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;

import java.util.List;

import static com.dottec.pdi.project.pdi.enums.CollaboratorStatus.active;

public class CollaboratorController {

    public static void saveCollaborator(String name, String cpf, String email, Department department) {
        Collaborator collaborator = new Collaborator();
        collaborator.setName(name);
        collaborator.setCpf(cpf);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(active); // Define o status padrão como ativo

        CollaboratorDAO.insert(collaborator);
    }

    public static List<Collaborator> findAllCollaborators() {
        return CollaboratorDAO.readAll();
    }

    public static void updateCollaborator(Collaborator collaborator) {
        // Este método recebe o objeto inteiro para ser mais flexível
        CollaboratorDAO.update(collaborator);
    }

    public static void deleteCollaboratorById(int id) {
        CollaboratorDAO.deleteById(id);
    }
}