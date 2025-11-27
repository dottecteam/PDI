package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.GoalTemplatesController;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityTemplateDAO {
    private static final String INSERT_SQL = "INSERT INTO activity_templates (act_tmp_name, act_tmp_description, goal_template_id) VALUES (?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM activity_templates WHERE act_tmp_id = ?";
    private static final String UPDATE_SQL = "UPDATE activity_templates SET act_tmp_name = ?, act_tmp_description = ?, goal_template_id = ? WHERE act_tmp_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM activity_templates";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM activity_templates WHERE act_tmp_id = ?";

    private static final String FIND_BY_GOAL_TEMPLATE_ID_SQL = "SELECT * FROM activity_templates WHERE goal_template_id = ?";

    public static void insert(ActivityTemplate activityTemplate) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, activityTemplate.getName());
            stmt.setString(2, activityTemplate.getDescription());
            stmt.setInt(3, activityTemplate.getGoalTemplate().getGoa_tmp_id());

            int rows = stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                activityTemplate.setId(rs.getInt(1));
            }
            System.out.println("Atividade modelo inserida! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir Atividade modelo: " + e.getMessage(), e);
        }
    }

    public static void delete(ActivityTemplate activityTemplate){
        ActivityTemplateDAO.deleteById(activityTemplate.getId());
    }

    public static void deleteById(int id){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Atividade modelo deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Atividade modelo: " + e.getMessage(), e);
        }
    }

    public static void update(ActivityTemplate activityTemplate) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)){
            stmt.setString(1, activityTemplate.getName());
            stmt.setString(2, activityTemplate.getDescription());
            stmt.setInt(3, activityTemplate.getGoalTemplate().getGoa_tmp_id());
            stmt.setInt(4, activityTemplate.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Atividade modelo atualizada! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Atividade modelo: " + e.getMessage(), e);
        }
    }

    public static ActivityTemplate findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mapResultSetToActivityTemplate(rs);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atividade modelo: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<ActivityTemplate> readAll() {
        List<ActivityTemplate> activities = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                activities.add(mapResultSetToActivityTemplate(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as atividades modelo: " + e.getMessage(), e);
        }
        return activities;
    }

    public static ActivityTemplate mapResultSetToActivityTemplate(ResultSet rs) throws SQLException {
        ActivityTemplate activityTemplate = new ActivityTemplate();
        activityTemplate.setId(rs.getInt("act_tmp_id"));
        activityTemplate.setName(rs.getString("act_tmp_name"));
        activityTemplate.setDescription(rs.getString("act_tmp_description"));

        activityTemplate.setGoalTemplate(GoalTemplatesController.findGoalTemplateById(
                rs.getInt("goal_template_id")
        ));
        return activityTemplate;
    }

    public static List<ActivityTemplate> findByGoalTemplateId(int goalTemplateId) {
        try {
            Connection conn = Database.getConnection();
            return findByGoalTemplateId(conn, goalTemplateId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
    }

    public static List<ActivityTemplate> findByGoalTemplateId(Connection conn, int goalTemplateId) {
        List<ActivityTemplate> activities = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_GOAL_TEMPLATE_ID_SQL)) {
            stmt.setInt(1, goalTemplateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivityTemplate(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atividades modelo da meta modelo: " + e.getMessage(), e);
        }
        return activities;
    }

}
