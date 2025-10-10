package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Collaborator {
    private int id;
    private String name;
    private String email;
    private String cpf;

    private CollaboratorStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Department department;


    public Collaborator() {}
    public Collaborator(int id, String name, String email, String cpf, Department department, CollaboratorStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;

        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
        this.deletedAt=deletedAt;

        this.department = department;
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

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    public CollaboratorStatus getStatus() {
        return this.status;
    }
    public void setStatus(CollaboratorStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        // Gera um código hash baseado apenas no ID.
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        // Verifica se é a mesma instância de objeto.
        if (this == obj) return true;

        // Verifica se o objeto é nulo ou de uma classe diferente.
        if (obj == null || getClass() != obj.getClass()) return false;

        // Faz o cast e compara os IDs para determinar a igualdade.
        Collaborator other = (Collaborator) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        // Cria uma representação em texto do objeto para facilitar a depuração.
        return "Collaborator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }

}
