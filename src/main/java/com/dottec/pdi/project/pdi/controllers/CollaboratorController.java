package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.dao.CollaboratorDAO;
import com.dottec.pdi.project.pdi.enums.CollaboratorStatus;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Log;
import com.dottec.pdi.project.pdi.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dottec.pdi.project.pdi.enums.CollaboratorStatus.active;

public class CollaboratorController {

    private CollaboratorController() {
    }

    public static void saveCollaborator(String name, String cpf, String email, Department department) {
        Collaborator collaborator = new Collaborator();
        collaborator.setName(name);
        collaborator.setCpf(cpf);
        collaborator.setEmail(email);
        collaborator.setDepartment(department);
        collaborator.setStatus(active);

        CollaboratorDAO.insert(collaborator);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("create_collaborator");
            String details = String.format("{\"col_name\": \"%s\", \"col_cpf\": \"%s\", \"dep_name\": \"%s\", \"log_message\": \"Collaborator created\"}",
                    name, cpf, department != null ? department.getName() : "N/A");
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }

    }

    public static List<Collaborator> findAllCollaborators() {
        return CollaboratorDAO.readAll();
    }

    public static List<Collaborator> searchCollaborators(String query) {

        String sql = "SELECT c.*, d.dep_id AS dep_id, d.dep_name AS dep_name FROM collaborators c " +
                "LEFT JOIN departments d ON c.department_id = d.dep_id WHERE LOWER(c.col_name) LIKE ?";
        List<Collaborator> results = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Collaborator c = toCollaborator(rs);
                    results.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private static Collaborator toCollaborator(ResultSet rs) throws SQLException {

        int id = rs.getInt("col_id");
        String nome = rs.getString("col_name");
        String statusString = rs.getString("col_status");
        int setor = rs.getInt("department_id");

        CollaboratorStatus status = CollaboratorStatus.valueOf(statusString.toLowerCase());

        Collaborator c = new Collaborator();
        c.setId(id);
        c.setName(nome);
        c.setStatus(status);

        if (!rs.wasNull()) {
            Department dept = new Department();
            dept.setId(setor);
            dept.setName(rs.getString("dep_name"));

            c.setDepartment(dept);

        } else {
            c.setDepartment(null);
        }

        return c;
    }

    public static void updateCollaborator(Collaborator collaborator) {
        CollaboratorDAO.update(collaborator);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("update_collaborator");
            String details = String.format("{\"col_id\": %d, \"col_name\": \"%s\", \"dep_id\": %d, \"log_message\": \"Collaborator data updated\"}",
                    collaborator.getId(), collaborator.getName(), collaborator.getDepartment() != null ? collaborator.getDepartment().getId() : 0);
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }

    public static void deleteCollaboratorById(int id) {
        CollaboratorDAO.deleteById(id);

        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser != null) {
            Log log = new Log();
            log.setLogAction("inactivate_collaborator");
            String details = String.format("{\"col_id\": %d, \"log_message\": \"Collaborator soft deleted\"}", id);
            log.setLogDetails(details);
            log.setLogUserId(loggedUser.getId());
            LogController.addLog(log);
        }
    }
}