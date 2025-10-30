package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.enums.GoalStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    // Comandos SQL (existentes)
    private static final String INSERT_SQL = "INSERT INTO goals (goa_name, goa_description, goa_status, goa_deadline, collaborator_id) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL = "DELETE FROM goals WHERE goa_id = ?";
    private static final String UPDATE_SQL = "UPDATE goals SET goa_name = ?, goa_description = ?, goa_status = ?, goa_deadline = ?, collaborator_id = ?, goa_updated_at = CURRENT_TIMESTAMP WHERE goa_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM goals";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM goals WHERE goa_id = ?";

    // NOVO COMANDO SQL: Buscar metas por ID do colaborador
    private static final String FIND_BY_COLLABORATOR_ID_SQL = "SELECT * FROM goals WHERE collaborator_id = ?";

    // ... (os métodos insert, delete, update, findById, readAll, deleteById continuam os mesmos) ...
    public static void insert(Goal goal) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, goal.getName());
            stmt.setString(2, goal.getDescription());
            stmt.setString(3, goal.getStatus().name());
            stmt.setDate(4, Date.valueOf(goal.getDeadline()));
            stmt.setInt(5, goal.getCollaborator().getId());

            int rows = stmt.executeUpdate();
            System.out.println("Meta inserida! Linhas: " + rows);

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                goal.setId(rs.getInt(1));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir meta: " + e.getMessage(), e);
        }
    }

    public static void delete(Goal goal){
        GoalDAO.deleteById(goal.getId());
    }

    public static void update(Goal goal) {
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

    public static Goal findById(int id) {
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

    public static List<Goal> readAll() {
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

    public static void deleteById(int id){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Meta deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar meta: " + e.getMessage(), e);
        }
    }

    // NOVO MÉTODO: Buscar metas por ID do colaborador
    public static List<Goal> findByCollaboratorId(int collaboratorId) {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_COLLABORATOR_ID_SQL)) {
            stmt.setInt(1, collaboratorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar metas do colaborador: " + e.getMessage(), e);
        }
        return goals;
    }

    // Método auxiliar para não repetir código de mapeamento
    public static Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setId(rs.getInt("goa_id"));
        goal.setName(rs.getString("goa_name"));
        goal.setDescription(rs.getString("goa_description"));
        goal.setStatus(GoalStatus.valueOf(rs.getString("goa_status")));
        java.sql.Date deadlineSql = rs.getDate("goa_deadline");
        if (deadlineSql != null) {
            goal.setDeadline(deadlineSql.toLocalDate());
        }
        goal.setCreatedAt(rs.getTimestamp("goa_created_at").toLocalDateTime());

        Timestamp updatedAtTimestamp = rs.getTimestamp("goa_updated_at");
        if(updatedAtTimestamp != null){
            goal.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
        }

        Collaborator collaborator = new Collaborator();
        collaborator.setId(rs.getInt("collaborator_id"));
        goal.setCollaborator(collaborator);

        return goal;
    }
}