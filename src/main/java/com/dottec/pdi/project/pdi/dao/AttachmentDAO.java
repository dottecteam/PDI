package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

    private static final String INSERT_SQL = "INSERT INTO attachments (att_file_path, uploaded_by, goal_id, activity_id) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ACTIVITY_ID_SQL = "SELECT * FROM attachments WHERE activity_id = ?";

    public static void insert(Attachment attachment) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, attachment.getFilePath());
            stmt.setInt(2, attachment.getUploadedBy().getId());

            if (attachment.getGoal() != null) {
                stmt.setInt(3, attachment.getGoal().getId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (attachment.getActivity() != null) {
                stmt.setInt(4, attachment.getActivity().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            int rows = stmt.executeUpdate();
            System.out.println("Anexo inserido! Linhas: " + rows);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir anexo: " + e.getMessage(), e);
        }
    }

    public static List<Attachment> findByActivityId(int activityId) {
        List<Attachment> attachments = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ACTIVITY_ID_SQL)) {
            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attachments.add(mapResultSetToAttachment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar anexos por atividade: " + e.getMessage(), e);
        }
        return attachments;
    }

    public static Attachment mapResultSetToAttachment(ResultSet rs) throws SQLException {
        Attachment attachment = new Attachment();
        attachment.setId(rs.getInt("att_id"));
        attachment.setFilePath(rs.getString("att_file_path"));

        Timestamp uploadedAtTimestamp = rs.getTimestamp("att_uploaded_at");
        if (uploadedAtTimestamp != null) {
            attachment.setUploadedAt(uploadedAtTimestamp.toLocalDateTime());
        }

        User uploadedBy = new User();
        uploadedBy.setId(rs.getInt("uploaded_by"));
        attachment.setUploadedBy(uploadedBy);

        int goalId = rs.getInt("goal_id");
        if (!rs.wasNull()) {
            Goal goal = new Goal();
            goal.setId(goalId);
            attachment.setGoal(goal);
        }

        int activityId = rs.getInt("activity_id");
        if (!rs.wasNull()) {
            Activity activity = new Activity();
            activity.setId(activityId);
            attachment.setActivity(activity);
        }

        return attachment;
    }
}