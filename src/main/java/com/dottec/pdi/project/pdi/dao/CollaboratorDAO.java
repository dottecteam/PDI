package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.CollaboratorStatusData;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollaboratorDAO {
    private static final String INSERT_SQL = "INSERT INTO collaborators (col_name, col_email, col_cpf, col_status, department_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE collaborators SET col_name = ?, col_email = ?, col_cpf = ?, col_status = ?, department_id = ?, col_updated_at = CURRENT_TIMESTAMP WHERE col_id = ?";
    private static final String SOFT_DELETE_SQL = "UPDATE collaborators SET col_status = 'INACTIVE', col_deleted_at = CURRENT_TIMESTAMP WHERE col_id = ?";

    private static final String BASE_SELECT_SQL = """
        SELECT c.*, d.dep_name
        FROM collaborators c
        LEFT JOIN departments d ON c.department_id = d.dep_id
        """;

    private static final String SELECT_ALL_SQL = BASE_SELECT_SQL + " WHERE c.col_deleted_at IS NULL";
    private static final String FIND_BY_ID_SQL = BASE_SELECT_SQL + " WHERE c.col_id = ? AND c.col_deleted_at IS NULL";
    private static final String FIND_BY_DEPARTMENT_ID_SQL = BASE_SELECT_SQL + " WHERE c.department_id = ? AND c.col_deleted_at IS NULL";


    public static void insert(Collaborator collaborator) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, collaborator.getName());
            stmt.setString(2, collaborator.getEmail());
            stmt.setString(3, collaborator.getCpf());
            stmt.setString(4, collaborator.getStatus().name());
            stmt.setInt(5, collaborator.getDepartment().getId());

            int rows = stmt.executeUpdate();
            System.out.println("Colaborador inserido! Linhas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir colaborador: " + e.getMessage(), e);
        }
    }

    public static void update(Collaborator collaborator) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, collaborator.getName());
            stmt.setString(2, collaborator.getEmail());
            stmt.setString(3, collaborator.getCpf());
            stmt.setString(4, collaborator.getStatus().name());
            stmt.setInt(5, collaborator.getDepartment().getId());
            stmt.setInt(6, collaborator.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Colaborador atualizado! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar colaborador: " + e.getMessage(), e);
        }
    }

    public static void delete(Collaborator collaborator) {
        CollaboratorDAO.deleteById(collaborator.getId());
    }

    public static void deleteById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(SOFT_DELETE_SQL)) {
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Colaborador desativado (soft delete)! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar colaborador: " + e.getMessage(), e);
        }
    }

    public static Collaborator findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCollaborator(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colaborador por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Collaborator> readAll() {
        List<Collaborator> collaborators = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                collaborators.add(mapResultSetToCollaborator(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os colaboradores: " + e.getMessage(), e);
        }
        return collaborators;
    }

    public static List<Collaborator> findByDepartmentId(int departmentId) {
        List<Collaborator> collaborators = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_DEPARTMENT_ID_SQL)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    collaborators.add(mapResultSetToCollaborator(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colaboradores por departamento: " + e.getMessage(), e);
        }
        return collaborators;
    }

    public static List<CollaboratorStatusData> getTaskStatusCountsForCollaborator(int collaboratorId) {
        //A Lista de dados que será retornada
        List<CollaboratorStatusData> statusCounts = new ArrayList<>();


        String sql = "select goa_status, COUNT(*) as quantidade from goals where collaborator_id = ? group by goa_status;";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //Define o ID do colaborador no placeholder '?' da query
            stmt.setInt(1, collaboratorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("goa_status");
                    int quantidade = rs.getInt("quantidade");


                    statusCounts.add(new CollaboratorStatusData(status, quantidade));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contagem de status de tarefas: " + e.getMessage(), e);
        }

        return statusCounts;
    }

    public static Collaborator mapResultSetToCollaborator(ResultSet rs) throws SQLException {
        Collaborator collaborator = new Collaborator();
        collaborator.setId(rs.getInt("col_id"));
        collaborator.setName(rs.getString("col_name"));
        collaborator.setEmail(rs.getString("col_email"));
        collaborator.setCpf(rs.getString("col_cpf"));
        collaborator.setStatus(CollaboratorStatus.valueOf(rs.getString("col_status")));
        collaborator.setCreatedAt(rs.getTimestamp("col_created_at").toLocalDateTime());

        Timestamp updatedAtTimestamp = rs.getTimestamp("col_updated_at");
        if (updatedAtTimestamp != null) {
            collaborator.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
        }

        Timestamp deletedAtTimestamp = rs.getTimestamp("col_deleted_at");
        if (deletedAtTimestamp != null) {
            collaborator.setDeletedAt(deletedAtTimestamp.toLocalDateTime());
        }

        Department department = new Department();
        department.setId(rs.getInt("department_id"));
        department.setName(rs.getString("dep_name"));
        collaborator.setDepartment(department);

        return collaborator;
    }
}