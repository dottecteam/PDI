package com.dottec.pdi.project.pdi.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.controllers.ActivityTemplateController;
import com.dottec.pdi.project.pdi.model.ActivityTemplate;
import com.dottec.pdi.project.pdi.model.GoalTemplate;

public class GoalTemplatesDAO {
    private static final String INSERT_SQL = "INSERT INTO goal_templates (goa_tmp_name, goa_tmp_description) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM goal_templates WHERE goa_tmp_id = ?";
    private static final String UPDATE_SQL = "UPDATE goal_templates SET goa_tmp_name = ?, goa_tmp_description = ? WHERE goa_tmp_id = ?;";
    private static final String FINDBYID_SQL = "SELECT goa_tmp_id, goa_tmp_name, goa_tmp_description FROM goal_templates WHERE goa_tmp_id = ?;";
    private static final String FINDBYTEMP_SQL = "SELECT g.goa_tmp_id, g.goa_tmp_name, g.goa_tmp_description, " +
            "a.act_tmp_id, a.act_tmp_name, a.act_tmp_description " +
            "FROM goal_templates g " +
            "LEFT JOIN activity_templates a ON a.goal_template_id = g.goa_tmp_id ORDER BY g.goa_tmp_id;";

    public static void insert(GoalTemplate goalTemplate) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, goalTemplate.getGoa_tmp_name());
            stmt.setString(2, goalTemplate.getGoa_tmp_description());

            int rows = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                goalTemplate.setGoa_tmp_id(rs.getInt(1));
            }

            System.out.println("GoalTemplate inserido! Linha adicionada: " + rows);

        } 
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir GoalTemplate: " + e.getMessage(), e);
        }
    }

    public static void deleteById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            System.out.println("GoalTemplate deletado!: " + rows);

        } 
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar GoalTemplate: " + e.getMessage(), e);
        }
    }

    public static void update(GoalTemplate goal) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, goal.getGoa_tmp_name());
            stmt.setString(2, goal.getGoa_tmp_description());
            stmt.setInt(3, goal.getGoa_tmp_id());

            int rows = stmt.executeUpdate();
            System.out.println("GoalTemplate atualizado! Linha atualizada: " + rows);

        } 
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar GoalTemplate: " + e.getMessage(), e);
        }
    }

    public static GoalTemplate findById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FINDBYID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GoalTemplate(
                            rs.getInt("goa_tmp_id"),
                            null, // created_at está null pq ele não será selecionado
                            rs.getString("goa_tmp_name"),
                            rs.getString("goa_tmp_description")
                    );
                }
            }

        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar GoalTemplate por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<GoalTemplate> readAll() {
        Map<Integer, GoalTemplate> map = new LinkedHashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FINDBYTEMP_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int goaTmpId = rs.getInt("goa_tmp_id");
                GoalTemplate goal = map.get(goaTmpId);

                if (goal == null) {
                    goal = new GoalTemplate();
                    goal.setGoa_tmp_id(goaTmpId);
                    goal.setGoa_tmp_created_at(null);
                    goal.setGoa_tmp_name(rs.getString("goa_tmp_name"));
                    goal.setGoa_tmp_description(rs.getString("goa_tmp_description"));
                    goal.setActivityTemplates(new ArrayList<>());
                    map.put(goaTmpId, goal);
                }

                int actTmpId = rs.getInt("act_tmp_id");
                if (!rs.wasNull()) {
                    ActivityTemplate at = new ActivityTemplate(
                            actTmpId,
                            rs.getString("act_tmp_name"),
                            rs.getString("act_tmp_description"),
                            goal
                    );
                    goal.getActivityTemplates().add(at);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar GoalTemplates: " + e.getMessage(), e);
        }

        return new ArrayList<>(map.values());
    }

}