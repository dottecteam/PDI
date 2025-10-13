package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;

import java.util.List;

import static com.dottec.pdi.project.pdi.enums.CollaboratorStatus.active;

public class CollaboratorController {
    public void saveCollaborator(String name, String cpf, String email, Department department){

        Collaborator collaborator = new Collaborator();
        collaborator.setCpf(cpf);
        collaborator.setName(name);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(active);

        CollaboratorDAO.insert(collaborator);
    }

    public List<Collaborator> findAllCollaborators() {
        List<Collaborator> collaborators = CollaboratorDAO.readAll();
        return collaborators;
    }

    public void updateCollaborator(String name, String cpf, String email, Department department, CollaboratorStatus status){

        Collaborator collaborator = new Collaborator();
        collaborator.setCpf(cpf);
        collaborator.setName(name);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(status);

        CollaboratorDAO.update(collaborator);
    }
}
