package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.enums.GoalStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    // Comandos SQL
    private static final String INSERT_SQL = "INSERT INTO pdi_goals (goa_name, goa_description, goa_status, goa_deadline, collaborator_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM pdi_goals WHERE goa_id = ?";
    private static final String UPDATE_SQL = "UPDATE pdi_goals SET goa_name = ?, goa_description = ?, goa_status = ?, goa_deadline = ?, collaborator_id = ?, goa_updated_at = CURRENT_TIMESTAMP WHERE goa_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM pdi_goals";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM pdi_goals WHERE goa_id = ?";

    public void insert(Goal goal) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)){
            stmt.setString(1, goal.getName());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getStatus().name());
            stmt.setDate(4, Date.valueOf(goal.getDeadline()));
            stmt.setInt(5, goal.getCollaborator().getId());

            int rows = stmt.executeUpdate();
            System.out.println("Meta inserida! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir meta: " + e.getMessage(), e);
        }
    }

    public void delete(Goal goal){
        this.deleteById(goal.getId());
    }

    public void update(Goal goal) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)){
            stmt.setString(1, goal.getName());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getStatus().name());
            stmt.setDate(4, Date.valueOf(goal.getDeadline()));
            stmt.setInt(5, goal.getCollaborator().getId());
            stmt.setInt(6, goal.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Meta atualizada! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar meta: " + e.getMessage(), e);
        }
    }

    public Goal findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGoal(rs);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar meta: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Goal> readAll() {
        List<Goal> goals = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                goals.add(mapResultSetToGoal(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as metas: " + e.getMessage(), e);
        }
        return goals;
    }

    public void deleteById(int id){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Meta deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar meta: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para não repetir código de mapeamento
    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setId(rs.getInt("goa_id"));
        goal.setName(rs.getString("goa_name"));
        goal.setDescription(rs.getString("goa_description"));
        goal.setStatus(GoalStatus.valueOf(rs.getString("goa_status")));
        goal.setDeadline(rs.getDate("goa_deadline").toLocalDate());
        goal.setCreatedAt(rs.getTimestamp("goa_created_at").toLocalDateTime());
        goal.setUpdatedAt(rs.getTimestamp("goa_updated_at").toLocalDateTime());

        // Cria um objeto Collaborator - apenas com o ID por enquanto
        Collaborator collaborator = new Collaborator();
        collaborator.setId(rs.getInt("collaborator_id"));
        goal.setCollaborator(collaborator);

        // As listas de Activities e Tags não são preenchidas aqui

        return goal;
    }
}