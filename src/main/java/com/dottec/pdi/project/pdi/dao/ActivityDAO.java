package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.Goal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.dottec.pdi.project.pdi.dao.GoalDAO.mapResultSetToGoal;

public class ActivityDAO {
    private static final String INSERT_SQL = "INSERT INTO activities (act_name, act_description, act_status, act_deadline, goal_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM activities WHERE act_id = ?";
    private static final String UPDATE_SQL = "UPDATE activities SET act_name = ?, act_description = ?, act_status = ?, act_deadline = ?, goal_id = ?, goa_updated_at = CURRENT_TIMESTAMP WHERE act_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM activities";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM activities WHERE act_id = ?";

    private static final String FIND_BY_GOAL_ID_SQL = "SELECT * FROM activities WHERE goal_id = ?";

    public static void insert(Activity activity) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)){
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getStatus().name());
            stmt.setDate(4, Date.valueOf(activity.getDeadline()));
            stmt.setInt(5, activity.getGoal().getId());

            int rows = stmt.executeUpdate();
            System.out.println("Atividade inserida! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir Atividade: " + e.getMessage(), e);
        }
    }

    public static void delete(Activity activity){
        ActivityDAO.deleteById(activity.getId());
    }

    public static void deleteById(int id){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Atividade deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Atividade: " + e.getMessage(), e);
        }
    }

    public static void update(Activity activity) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)){
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getStatus().name());
            stmt.setDate(4, Date.valueOf(activity.getDeadline()));
            stmt.setInt(5, activity.getGoal().getId());
            stmt.setInt(6, activity.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Atividade atualizada! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Atividade: " + e.getMessage(), e);
        }
    }

    public static Activity findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Activity activity = mapResultSetToActivity(rs);
                    List<Attachment> attachments = AttachmentDAO.findByActivityId(activity.getId());
                    activity.setAttachments(attachments);
                    return activity;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atividade: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Activity> readAll() {
        List<Activity> activities = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as atividades: " + e.getMessage(), e);
        }
        return activities;
    }

    public static Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getInt("act_id"));
        activity.setName(rs.getString("act_name"));
        activity.setDescription(rs.getString("act_description"));
        activity.setStatus(ActivityStatus.valueOf(rs.getString("act_status")));
        java.sql.Date deadlineSql = rs.getDate("act_deadline");
        if (deadlineSql != null) {
            activity.setDeadline(deadlineSql.toLocalDate());
        }
        activity.setCreatedAt(rs.getTimestamp("act_created_at").toLocalDateTime());

        Timestamp updatedAtTimestamp = rs.getTimestamp("act_updated_at");
        if(updatedAtTimestamp != null){
            activity.setUpdateAt(updatedAtTimestamp.toLocalDateTime());
        }

        Goal goal = new Goal();
        goal.setId(rs.getInt("goal_id"));
        activity.setGoal(goal);

        return activity;
    }

    public static List<Activity> findByGoalId(int goalId) {
        List<Activity> activities = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_GOAL_ID_SQL)) {
            stmt.setInt(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atividades da meta: " + e.getMessage(), e);
        }
        return activities;
    }

}
