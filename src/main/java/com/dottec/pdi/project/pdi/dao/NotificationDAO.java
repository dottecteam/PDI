package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Notification;
import com.dottec.pdi.project.pdi.enums.NotificationType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private static final String INSERT_SQL = "INSERT INTO notifications (not_message, not_type, not_is_read, user_id) VALUES (?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM notifications WHERE not_id = ?";
    private static final String UPDATE_SQL = "UPDATE notifications SET not_message = ?, not_type = ?, not_is_read = ?, user_id = ? WHERE not_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM notifications WHERE not_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM notifications";

    public static void insert(Notification notification) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, notification.getNotMessage());
            stmt.setString(2, notification.getNotType().name());
            stmt.setBoolean(3, notification.isNotIsRead());
            stmt.setInt(4, notification.getUserId());

            int rows = stmt.executeUpdate();
            System.out.println("Notificação inserida! Linhas afetadas: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir notificação: " + e.getMessage(), e);
        }
    }

    public static void delete(Notification notification) {
        deleteById(notification.getNotId());
    }

    public static void update(Notification notification) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, notification.getNotMessage());
            stmt.setString(2, notification.getNotType().name());
            stmt.setBoolean(3, notification.isNotIsRead());
            stmt.setInt(4, notification.getUserId());
            stmt.setInt(5, notification.getNotId());

            int rows = stmt.executeUpdate();
            System.out.println("Notificação atualizada! Linhas afetadas: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar notificação: " + e.getMessage(), e);
        }
    }

    public static Notification findById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar notificação por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Notification> readAll() {
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as notificações: " + e.getMessage(), e);
        }

        return notifications;
    }

    public static void deleteById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Notificação deletada! Linhas afetadas: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar notificação: " + e.getMessage(), e);
        }
    }

    private static Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        return new Notification(
            rs.getInt("not_id"),
            rs.getString("not_message"),
            NotificationType.valueOf(rs.getString("not_type")),
            rs.getBoolean("not_is_read"),
            rs.getTimestamp("not_created_at"),
            rs.getInt("user_id")
        );
    }
}
