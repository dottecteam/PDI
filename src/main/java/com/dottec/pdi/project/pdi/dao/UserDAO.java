package com.dottec.pdi.project.pdi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	
    private static Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public static void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (use_name, use_email, use_password_hash, use_role, use_status, department_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUseName());
            stmt.setString(2, user.getUseEmail());
            stmt.setString(3, user.getUsePasswordHash());
            stmt.setString(4, user.getUseRole());
            stmt.setString(5, user.getUseStatus());
            stmt.setInt(6, user.getDepartmentId());
            stmt.executeUpdate();
        }
    }

    public static User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE use_id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }
    
    public static User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE use_email = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    public static List<User> listAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public static void update(User user) throws SQLException {
        String sql = "UPDATE users SET use_name=?, use_email=?, use_password_hash=?, use_role=?, use_status=?, department_id=?, use_updated_at=NOW() WHERE use_id=?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUseName());
            stmt.setString(2, user.getUseEmail());
            stmt.setString(3, user.getUsePasswordHash());
            stmt.setString(4, user.getUseRole());
            stmt.setString(5, user.getUseStatus());
            stmt.setInt(6, user.getDepartmentId());
            stmt.setInt(7, user.getUseId());
            
            stmt.executeUpdate();
        }
    }
    
    public static void delete(int id) throws SQLException {
    	String sql = "DELETE FROM users WHERE use_id = ?";
    	try (Connection connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
               
               stmt.setInt(1, id);
               stmt.executeUpdate();
           }
    }
    
    public static void softDelete(int id) throws SQLException {
        String sql = "UPDATE users SET use_deleted_at = NOW(), use_status = 'inactive' WHERE use_id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    public static void updatePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET use_password_hash = ?, use_updated_at = NOW() WHERE use_id = ?";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    private static User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUseId(rs.getInt("use_id"));
        user.setUseName(rs.getString("use_name"));
        user.setUseEmail(rs.getString("use_email"));
        user.setUsePasswordHash(rs.getString("use_password_hash"));
        user.setUseRole(rs.getString("use_role"));
        user.setUseStatus(rs.getString("use_status"));
        user.setUseCreatedAt(rs.getTimestamp("use_created_at"));
        user.setUseUpdatedAt(rs.getTimestamp("use_updated_at"));
        user.setUseDeletedAt(rs.getTimestamp("use_deleted_at"));
        user.setDepartmentId(rs.getInt("department_id"));
        return user;
    }
}