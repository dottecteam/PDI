package com.dottec.pdi.project.pdi.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.GoalTemplates;

public class GoalTemplatesDAO {
    private static final String INSERT_SQL = "INSERT INTO goal_templates (goa_tmp_name, goa_tmp_description) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM goal_templates WHERE goa_tmp_id = ?";
    private static final String UPDATE_SQL = "UPDATE goal_templates SET goa_tmp_name = ?, goa_tmp_description = ? WHERE goa_tmp_id = ?;";
    private static final String FINDBYID_SQL = "SELECT goa_tmp_name, goa_tmp_description FROM goal_templates WHERE goa_tmp_id = ?;";
    private static final String FINDBYTEMP_SQL = "SELECT goa_tmp_id, goa_tmp_name, goa_tmp_description FROM goal_templates ORDER BY goa_tmp_created_at DESC;";

    public static void insert(GoalTemplates goal) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, goal.getGoa_tmp_name());
            stmt.setString(2, goal.getGoa_tmp_description());

            int rows = stmt.executeUpdate();
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

    public static void update(GoalTemplates goal) {
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

    public static GoalTemplates findById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FINDBYID_SQL)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GoalTemplates(
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

    public static List<GoalTemplates> readAll() {
        List<GoalTemplates> goals = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FINDBYTEMP_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                GoalTemplates goal = new GoalTemplates(
                        rs.getInt("goa_tmp_id"),
                        null, // created_at está null pq ele não será selecionado
                        rs.getString("goa_tmp_name"),
                        rs.getString("goa_tmp_description")
                );
                goals.add(goal);
            }

        } 
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar GoalTemplates: " + e.getMessage(), e);
        }

        return goals;
    }
}