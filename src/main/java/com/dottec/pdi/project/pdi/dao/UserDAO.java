package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String INSERT_SQL = "INSERT INTO users (use_name, use_email, use_password_hash, use_role, use_status, department_id) VALUES (?, ?, ?, ?, 'active', ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE use_id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM users WHERE use_email = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM users";
    private static final String UPDATE_SQL = "UPDATE users SET use_name=?, use_email=?, use_password_hash=?, use_role=?, use_status=?, department_id=?, use_updated_at=NOW() WHERE use_id=?";
    private static final String DELETE_SQL = "DELETE FROM users WHERE use_id = ?";
    private static final String SOFT_DELETE_SQL = "UPDATE users SET use_deleted_at = NOW(), use_status = 'inactive' WHERE use_id = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE users SET use_password_hash = ?, use_updated_at = NOW() WHERE use_id = ?";
    private static final String LOGIN_SQL = "SELECT * FROM users WHERE use_email = ? AND use_password_hash = ? AND use_status = 'active' AND use_deleted_at IS NULL";
    private static final String FIND_BY_ROLE_SQL = "SELECT * FROM users WHERE use_role = ? AND use_status = 'active' AND use_deleted_at IS NULL";
    //    private static final String LOGIN_SQL = "SELECT * FROM users WHERE use_email = ? AND use_password_hash = ? AND use_status = 'active' AND use_deleted_at IS NULL";
    private static final String FIND_BY_EMAIL_FOR_LOGIN_SQL = "SELECT * FROM users WHERE use_email = ? AND use_status = 'active' AND use_deleted_at IS NULL";

    public static void insert(User user) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setObject(4, user.getRole().name());


            if (user.getDepartment() != null) {
                stmt.setInt(5, user.getDepartment().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            int rows = stmt.executeUpdate();
            System.out.println("Usuário inserido! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    public static User findById(int id) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public static User findByEmail(String email) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por Email: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<User> listAll() {
        List<User> users = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar todos os usuários: " + e.getMessage(), e);
        }
        return users;
    }

    public static void update(User user) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getStatus().name());

            if (user.getDepartment() != null) {
                stmt.setInt(6, user.getDepartment().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setInt(7, user.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Usuário atualizado! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public static void delete(User user) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, user.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Usuário excluído (hard delete)! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    public static void softDelete(User user) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SOFT_DELETE_SQL)) {

            stmt.setInt(1, user.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Usuário inativado (soft delete)! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inativar usuário: " + e.getMessage(), e);
        }
    }

    public static void updatePassword(int userId, String newPasswordHash) {
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_PASSWORD_SQL)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();
            System.out.println("Senha de usuário atualizada! Linhas afetadas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar senha do usuário: " + e.getMessage(), e);
        }
    }


    public static User login(String email, String password) {
        User user = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL_FOR_LOGIN_SQL)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                // Se encontrou um usuário, reutiliza o método mapUser para criar o objeto
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            // Mantém o padrão de tratamento de erro da classe DAO
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        if (user != null) {
            if (password.hashCode() == user.getPasswordHash().hashCode()) {
                return user;
            } else {
                return null; // Senha incorreta
            }
        }

        // Retorna null se o login falhar (credenciais incorretas ou usuário inativo)
        return null;
    }

    public static List<User> findByRole(Role role) {
        List<User> users = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_ROLE_SQL)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por role: " + e.getMessage(), e);
        }
        return users;
    }


    private static User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("use_id"));
        user.setName(rs.getString("use_name"));
        user.setEmail(rs.getString("use_email"));
        user.setPasswordHash(rs.getString("use_password_hash"));

        String roleString = rs.getString("use_role");
        user.setRole(Role.valueOf(roleString));

        String statusString = rs.getString("use_status");
        user.setStatus(UserStatus.valueOf(statusString));

        user.setCreatedAt(rs.getTimestamp("use_created_at").toLocalDateTime());
        Timestamp updatedAtTimestamp = rs.getTimestamp("use_updated_at");
        if (updatedAtTimestamp != null) {
            user.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
        }

        Timestamp deletedAtTimestamp = rs.getTimestamp("use_deleted_at");
        if (deletedAtTimestamp != null) {
            user.setDeletedAt(deletedAtTimestamp.toLocalDateTime());
        } else {
            user.setDeletedAt(null); // Garante que é nulo se não houver valor no banco
        }

        int departmentId = rs.getInt("department_id");
        if (departmentId > 0 && !rs.wasNull()) {
            // CORREÇÃO: Usar findByIdIncludeDeleted para garantir que o setor apareça,
            // mesmo que esteja inativo/soft-deleted, na lista de gerenciamento de usuários.
            Department department = DepartmentDAO.findByIdIncludeDeleted(departmentId);
            user.setDepartment(department);
        } else {
            user.setDepartment(null);
        }

        return user;
    }
}