package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    // Adding the attributes for the LogDAO to connect the methods later on

    private static final String INSERT_SQL =
            "INSERT INTO logs (log_action, log_details, user_id) VALUES (?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE logs SET log_action = ?, log_details = ?, user_id = ? WHERE log_id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM logs WHERE log_id = ?";

    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM logs WHERE log_id = ?";

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM logs";


    // --------- Adding helper method to call back in the getAllLogs and findLogbyId methods later on ------ //

    private static Log mapResultSetToLog(ResultSet rs) throws SQLException {
        Log log = new Log();
        log.setLogId(rs.getInt("log_id"));
        log.setLogAction(rs.getString("log_action"));
        log.setLogDetails(rs.getString("log_details"));
        log.setLogCreatedAt(rs.getTimestamp("log_created_at"));
        log.setLogUserId(rs.getInt("user_id"));
        return log;
    }

    public static void insert(Log log) { // Adding method in the DAO
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, log.getLogAction());
            stmt.setString(2, log.getLogDetails());
            stmt.setInt(3, log.getLogUserId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while inserting log: " + e.getMessage());
        }
    }


    public static void update(Log log) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, log.getLogAction());
            stmt.setString(2, log.getLogDetails());
            stmt.setInt(3, log.getLogUserId());
            stmt.setInt(4, log.getLogId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error while updating log: " + e.getMessage());
        }
    }

    public static void delete(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while deleting log: " + e.getMessage());
        }
    }

    public static Log getById(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLog(rs);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Log> getAll() {
        List<Log> logs = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return logs;
    }

}
