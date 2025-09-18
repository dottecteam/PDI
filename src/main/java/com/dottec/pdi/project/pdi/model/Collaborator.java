package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.model.enums.Status;
import java.util.Objects;

public class Collaborator {
    private int id;
    private String name;
    private String email;
    private String cpf;
    private int department;
    // private String role;
    private String experience;
    private String observations;
    private Status status;

    public Collaborator() {
    }

    public Collaborator(int id, String name, String email, String cpf, int department, String experience, String observations, Status status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.department = department;
        this.experience = experience;
        this.observations = observations;
        this.status = status;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Collaborator other = (Collaborator) obj;
        return id == other.id && Objects.equals(cpf, other.cpf);
    }

    @Override
    public String toString() {
        return "Collaborator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", department=" + department +
                ", experience='" + experience + '\'' +
                ", observations='" + observations + '\'' +
                ", status=" + status +
                '}';
    }
}
