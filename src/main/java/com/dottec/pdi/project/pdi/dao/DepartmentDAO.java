package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.enums.DepartmentStatus;

// import com.dottec.pdi.project.pdi.model.User;
// import com.dottec.pdi.project.pdi.model.Collaborator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Statement;

public class DepartmentDAO {
    private static final String INSERT_SQL = "INSERT INTO departments (dep_name, dep_status) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE departments SET dep_name = ?, dep_status = ?, dep_updated_at = CURRENT_TIMESTAMP WHERE dep_id = ?";
    private static final String SOFT_DELETE_SQL = "UPDATE departments SET dep_status = 'INACTIVE', dep_deleted_at = CURRENT_TIMESTAMP WHERE dep_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM departments WHERE dep_deleted_at IS NULL";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM departments WHERE dep_id = ? AND dep_deleted_at IS NULL";
    private static final String FIND_BY_ID_INCLUDE_DELETED_SQL = "SELECT * FROM departments WHERE dep_id = ?";

    public static Department findByIdIncludeDeleted(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_INCLUDE_DELETED_SQL)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar departamento (incluindo deletados): " + e.getMessage(), e);
        }
        return null;
    }

    public static void insert(Department department) {
        // Adicionando Statement.RETURN_GENERATED_KEYS para obter o ID
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getStatus().name());

            int rows = stmt.executeUpdate();
            System.out.println("Departamento inserido! Linhas: " + rows);

            // NOVO: Recupera o ID gerado e define no objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    department.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir departamento: " + e.getMessage(), e);
        }
    }

    public static void delete(Department department) {
        DepartmentDAO.deleteById(department.getId());
    }

    public static void update(Department department) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, department.getName());
            stmt.setString(2, department.getStatus().name());
            stmt.setInt(3, department.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Departamento atualizado! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar departamento: " + e.getMessage(), e);
        }
    }

    public static Department findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDepartment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar departamento: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Department> readAll() {
        List<Department> departments = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os departamentos: " + e.getMessage(), e);
        }
        return departments;
    }

    public static void deleteById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(SOFT_DELETE_SQL)) {
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Departamento desativado (soft delete)! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar departamento: " + e.getMessage(), e);
        }
    }

    public static Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("dep_id"));
        department.setName(rs.getString("dep_name"));
        department.setStatus(DepartmentStatus.valueOf(rs.getString("dep_status")));
        department.setCreatedAt(rs.getTimestamp("dep_created_at").toLocalDateTime());
        department.setUpdatedAt(rs.getTimestamp("dep_updated_at").toLocalDateTime());

        Timestamp deletedAtTimestamp = rs.getTimestamp("dep_deleted_at");
        if (deletedAtTimestamp != null) {
            department.setDeletedAt(deletedAtTimestamp.toLocalDateTime());
        }

        // As listas de Collaborators e Users não são preenchidas aqui.
        // Isso deve ser feito por seus respectivos DAOs em uma camada de serviço.
        return department;
    }

    // --- MÉTODOS FUTUROS (a serem implementados com UserDAO e CollaboratorDAO) ---

    /*
    private static final String FIND_USERS_BY_DEP_ID_SQL = "SELECT * FROM pdi_users WHERE department_id = ?";

    public List<User> findUsersByDepartmentId(int departmentId) {
        List<User> users = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_USERS_BY_DEP_ID_SQL)) {

            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // TODO: Mapear o ResultSet para um objeto User quando o modelo e o DAO existirem.
                    // Exemplo: users.add(UserDAO.mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários do departamento: " + e.getMessage(), e);
        }
        return users;
    }
    */

    /*
    private static final String FIND_COLLABORATORS_BY_DEP_ID_SQL = "SELECT * FROM pdi_collaborators WHERE department_id = ?";

    public List<Collaborator> findCollaboratorsByDepartmentId(int departmentId) {
        List<Collaborator> collaborators = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_COLLABORATORS_BY_DEP_ID_SQL)) {

            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // TODO: Mapear o ResultSet para um objeto Collaborator quando o DAO existir.
                    // Exemplo: collaborators.add(CollaboratorDAO.mapResultSetToCollaborator(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colaboradores do departamento: " + e.getMessage(), e);
        }
        return collaborators;
    }
    */
}