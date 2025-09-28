package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.model.Collaborator;

import java.util.List;

public class CollaboratorController {
    private CollaboratorDAO collaboratorDAO = new CollaboratorDAO();

    public void saveCollaborator(Collaborator collaborator){
        collaboratorDAO.save(collaborator);
    }

    public List<Collaborator> findAllCollaborators() {
        List<Collaborator> collaborators = collaboratorDAO.findAll();
        return collaborators;
    }

    public void updateCollaborator(Collaborator collaborator){collaboratorDAO.update(collaborator);}
}
