package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.enums.TagType;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.Tag;
import com.mysql.cj.exceptions.ConnectionIsClosedException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dottec.pdi.project.pdi.dao.GoalDAO.mapResultSetToGoal;

public class ActivityDAO {
    private static final String INSERT_SQL = "INSERT INTO activities (act_name, act_description, act_status, act_deadline, goal_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "UPDATE activities SET act_status = 'canceled' WHERE act_id = ?";
    private static final String UPDATE_SQL = "UPDATE activities SET act_name = ?, act_description = ?, act_status = ?, act_deadline = ?, goal_id = ?, act_updated_at = CURRENT_TIMESTAMP WHERE act_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM activities";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM activities WHERE act_id = ?";

    private static final String INSERT_TAG_TO_ACTIVITY = "INSERT INTO activity_tags (activity_id, tag_id) VALUES (?, ?)";
    private static final String FIND_BY_ACTIVITY_ID_SQL = "SELECT t.tag_id, t.tag_name, t.tag_type FROM tags t JOIN activity_tags at ON t.tag_id = at.tag_id WHERE at.activity_id = ?";
    private static final String DELETE_ACTIVITY_TAG = "DELETE FROM ACTIVITY_TAGS WHERE activity_id = ? AND tag_id = ?";

    private static final String FIND_BY_GOAL_ID_SQL = "SELECT * FROM activities WHERE goal_id = ?";

    public static void insert(Activity activity) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getStatus().name());
            stmt.setDate(4, Date.valueOf(activity.getDeadline()));
            stmt.setInt(5, activity.getGoal().getId());

            int rows = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()) {
                    int activityId = rs.getInt(1);
                    activity.setId(activityId);
                }
            }

            addTagsToActivity(activity.getId(), activity.getTags(), conn);

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

    public static boolean update(Activity activity) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                stmt.setString(1, activity.getName());
                stmt.setString(2, activity.getDescription());
                stmt.setString(3, activity.getStatus().name());

                if (activity.getDeadline() != null) {
                    stmt.setDate(4, java.sql.Date.valueOf(activity.getDeadline()));
                } else {
                    stmt.setNull(4, java.sql.Types.DATE);
                }

                if (activity.getGoal() != null) {
                    stmt.setInt(5, activity.getGoal().getId());
                } else {
                    stmt.setNull(5, java.sql.Types.INTEGER);
                }

                stmt.setInt(6, activity.getId());

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    conn.setAutoCommit(originalAutoCommit);
                    return false;
                }

                updateActivityTags(conn, activity.getId(), activity.getTags());

                conn.commit();
                conn.setAutoCommit(originalAutoCommit);
                System.out.println("Atividade atualizada! Linhas afetadas: " + rows);
                return true;

            } catch (SQLException | RuntimeException ex) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { /* log rollbackEx */ }
                throw ex;
            } finally {
                try { conn.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) {}
            }

        } catch (SQLException e) {
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
        activity.setTags(findTagsByActivityId(activity.getId()));
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

    public static void addTagsToActivity(int activityId, List<Tag> tags, Connection conn){
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_TAG_TO_ACTIVITY)) {

            conn.setAutoCommit(false);

            for (Tag tag : tags) {
                stmt.setInt(1, activityId);
                stmt.setInt(2, tag.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir tags na atividade: " + e.getMessage(), e);
        }
    }

    public static List<Tag> findTagsByActivityId(int activityId){
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ACTIVITY_ID_SQL)){
            stmt.setInt(1, activityId);

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    Tag tag = new Tag(
                            rs.getInt("tag_id"),
                            rs.getString("tag_name"),
                            TagType.valueOf(rs.getString("tag_type")));
                    tags.add(tag);
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Erro ao encontrar categoria: " + e.getMessage(), e);
        }
        return tags;
    }

    public static void updateActivityTags(Connection conn, int activity_id, List<Tag> newTags){
        try (
            PreparedStatement selectStmt = conn.prepareStatement(FIND_BY_ACTIVITY_ID_SQL);
            PreparedStatement deleteStmt = conn.prepareStatement(DELETE_ACTIVITY_TAG);
            PreparedStatement insertStmt = conn.prepareStatement(INSERT_TAG_TO_ACTIVITY);
        ){
            conn.setAutoCommit(false);

            selectStmt.setInt(1, activity_id);
            ResultSet rs = selectStmt.executeQuery();

            Set<Integer> currentTags = new HashSet<>();
            while(rs.next()){
                currentTags.add(rs.getInt("tag_id"));
            }

            Set<Integer> newTagsIds = newTags.stream().map(Tag::getId).collect(Collectors.toSet());

            Set<Integer> removedTags = new HashSet<>(currentTags);
            removedTags.removeAll(newTagsIds);

            Set<Integer> addedTags = new HashSet<>(newTagsIds);
            addedTags.removeAll(currentTags);

            for (int tag_id : removedTags){
                deleteStmt.setInt(1, activity_id);
                deleteStmt.setInt(2, tag_id);
                deleteStmt.addBatch();
            }
            deleteStmt.executeBatch();

            for (int tag_id : addedTags){
                insertStmt.setInt(1, activity_id);
                insertStmt.setInt(2, tag_id);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            conn.commit();
        } catch (SQLException e){
            throw new RuntimeException("Erro ao atualizar categorias: " + e.getMessage(), e);
        }
    }
}
