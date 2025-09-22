package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.model.Collaborator;

public class CollaboratorController {
    private CollaboratorDAO collaboratorDAO = new CollaboratorDAO();

    public void saveCollaborator(Collaborator collaborator){
        collaboratorDAO.save(collaborator);
    }
}
