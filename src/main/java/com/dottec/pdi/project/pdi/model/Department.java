package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.DepartmentStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Department {

    private int id;
    private String name;
    private DepartmentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private List<Collaborator> collaborators = new ArrayList<>();
    // private List<User> users = new ArrayList<>();

    public Department() {}

    public Department(
            int id,
            String name,
            DepartmentStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt,
            List<Collaborator> collaborators
            // List<User> users
    ) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.collaborators = collaborators;
        // this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DepartmentStatus getStatus() {
        return status;
    }

    public void setStatus(DepartmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    // public List<User> getUsers() {
    //     return users;
    // }

    // public void setUsers(List<User> users) {
    //     this.users = users;
    // }


    public String getStatusMessage(){
        return this.status.name();
    }

    public void addCollaborator(Collaborator collaborator) {
        if (collaborator != null && !collaborators.contains(collaborator)) {
            collaborators.add(collaborator);
        }
    }

    public void removeCollaborator(Collaborator collaborator) {
        collaborators.remove(collaborator);
    }

    public void clearCollaborators() {
        collaborators.clear();
    }

    // public void addUser(User user) {
    //     if (user != null && !users.contains(user)) {
    //         users.add(user);
    //     }
    // }

    // public void removeUser(User user) {
    //     users.remove(user);
    // }

    // public void clearUsers() {
    //     users.clear();
    // }
}
