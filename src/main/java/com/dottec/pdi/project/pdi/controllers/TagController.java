package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.TagDAO;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.enums.TagType;
import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;

import java.util.List;

public final class TagController {

    private TagController() {}

    public static void addTag(Tag tag) {
        TagDAO.insert(tag);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("CREATE_TAG");
            log.setLogDetails("Tag created: " + tag.getName() + " (Type: " + tag.getType().name() + ")");
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }

    }

    public static void deleteTag(int id) {
        Tag tag = TagDAO.findById(id);
        TagDAO.deleteById(id);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("DELETE_TAG");
            log.setLogDetails("Tag deleted: " + (tag != null ? tag.getName() : "ID " + id));
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }

    public static Tag findTagById(int id) {
        return TagDAO.findById(id);
    }

    public static List<Tag> findAllTags() {
        return TagDAO.readAll();
    }

    public static void updateTag(int id, String newName, TagType newType) {
        Tag existingTag = TagDAO.findById(id);
        if (existingTag != null) {
            existingTag.setName(newName);
            existingTag.setType(newType);
            TagDAO.update(existingTag);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("UPDATE_TAG");
                log.setLogDetails("Tag updated: " + existingTag.getName() + " (ID: " + id + ")");
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }

        } else {
            System.err.println("Erro: Tag com ID " + id + " não encontrada para atualização.");
        }
    }
}