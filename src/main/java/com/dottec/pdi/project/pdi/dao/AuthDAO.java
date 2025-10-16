package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AuthDAO {

    private static final String FIND_BY_EMAIL_AND_HASH_SQL = "SELECT * FROM users WHERE use_email = ? AND use_password_hash = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM users WHERE use_email = ?";

    private AuthDAO() {}

    public static User findByEmailAndHash(String email, String passwordHash) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL_AND_HASH_SQL)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por credenciais: " + e.getMessage(), e);
        }
        return null;
    }

    public static User findUserByEmail(String email) {
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
            user.setDeletedAt(null);
        }

        int departmentId = rs.getInt("department_id");
        if (departmentId > 0 && !rs.wasNull()) {

            Department department = DepartmentDAO.findById(departmentId);
            user.setDepartment(department);
        } else {
            user.setDepartment(null);
        }

        return user;
    }
}