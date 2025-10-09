package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.enums.UserStatus;

import java.util.Objects;

public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private int departmentId;
    private Role role;
    private UserStatus status;


    public User(int id, String name, String email, String passwordHash, int departmentId, Role role, UserStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.departmentId = departmentId;
        this.role = role;
        this.status = status;
    }

    public User(){

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return getId() == user.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString(){
        return  "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash=" + passwordHash +
                ", department='" + departmentId + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                '}';
    }
}
