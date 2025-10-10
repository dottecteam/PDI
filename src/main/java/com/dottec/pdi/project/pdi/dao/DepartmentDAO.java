package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DepartmentDAO {

    private static final String INSERT_SQL =
            "INSERT INTO departments (dep_name, dep_status, dep_created_at) VALUES (?, ?, NOW())";

    private static final String DELETE_SQL =
            "UPDATE departments SET dep_status = ?, dep_deleted_at = NOW() WHERE dep_id = ?";

    private static final String UPDATE_SQL =
            "UPDATE departments SET dep_name = ?, dep_status = ?, dep_updated_at = NOW() WHERE dep_id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT dep_id, dep_name, dep_status, dep_created_at, dep_updated_at, dep_deleted_at FROM departments WHERE dep_id = ?";


    public void insert(Department department) {
        try (Connection conn = new Database().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getStatus().name());

            int rows = stmt.executeUpdate();
            System.out.println("Departamento inserido! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir departamento: " + e.getMessage(), e);
        }
    }

    // lembrar que não deletar oficialmente, apenas marca o tempo e muda o status para inativo
    public void delete(int depId) {
        try (Connection conn = new Database().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setString(1, DepartmentStatus.Inactive.name());
            stmt.setInt(2, depId);

            int rows = stmt.executeUpdate();
            System.out.println("Departamento marcado como inativo! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar departamento como inativo: " + e.getMessage(), e);
        }
    }


    // Atualizar nome e status + data de atualização
    public void update(Department department) {
        try (Connection conn = new Database().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, department.getName());
            stmt.setString(2, department.getStatus().name());
            stmt.setInt(3, department.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Departamento atualizado! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar departamento: " + e.getMessage(), e);
        }
    }


    public Department findById(int id) {
        try (Connection conn = new Database().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department department = new Department();
                    department.setId(rs.getInt("dep_id"));
                    department.setName(rs.getString("dep_name"));
                    department.setStatus(DepartmentStatus.valueOf(rs.getString("dep_status")));
                    return department;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar departamento: " + e.getMessage(), e);
        }

        return null;
    }
}
