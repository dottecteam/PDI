package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Collaborator;
import com.dottec.pdi.project.pdi.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationCreatorDAO {

    private static final String FIND_EXPIRING_ACTIVITIES_SQL = """
        SELECT
            a.act_id, a.act_name, a.act_deadline,
            c.col_name, c.col_email,
            d.dep_id, d.dep_name
        FROM activities a
        JOIN goals g ON a.goal_id = g.goa_id
        JOIN collaborators c ON g.collaborator_id = c.col_id
        JOIN departments d ON c.department_id = d.dep_id
        WHERE a.act_status IN ('in_progress', 'pending')
        AND a.act_deadline BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
        AND d.dep_id = ?
    """;

    private static final String FIND_ALL_EXPIRING_ACTIVITIES_SQL = """
        SELECT
            a.act_id, a.act_name, a.act_deadline,
            c.col_name, c.col_email,
            d.dep_id, d.dep_name
        FROM activities a
        JOIN goals g ON a.goal_id = g.goa_id
        JOIN collaborators c ON g.collaborator_id = c.col_id
        JOIN departments d ON c.department_id = d.dep_id
        WHERE a.act_status IN ('in_progress', 'pending')
        AND a.act_deadline BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    """;

    private NotificationCreatorDAO() {}


    public static List<Activity> findExpiringActivitiesByDepartment(int departmentId) {
        return findActivities(FIND_EXPIRING_ACTIVITIES_SQL, departmentId);
    }

    public static List<Activity> findAllExpiringActivities() {
        return findActivities(FIND_ALL_EXPIRING_ACTIVITIES_SQL, -1);
    }

    private static List<Activity> findActivities(String sql, int departmentId) {
        List<Activity> activities = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (departmentId != -1) {
                stmt.setInt(1, departmentId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Activity activity = new Activity();
                    activity.setId(rs.getInt("act_id"));
                    activity.setName(rs.getString("act_name"));
                    activity.setDeadline(rs.getDate("act_deadline").toLocalDate());

                    Collaborator collaborator = new Collaborator();
                    collaborator.setName(rs.getString("col_name"));
                    collaborator.setEmail(rs.getString("col_email"));

                    Department department = new Department();
                    department.setId(rs.getInt("dep_id"));
                    department.setName(rs.getString("dep_name"));
                    collaborator.setDepartment(department);

                    activity.setGoal(new com.dottec.pdi.project.pdi.model.Goal() {{
                        setCollaborator(collaborator);
                    }});

                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar atividades expirando: " + e.getMessage(), e);
        }
        return activities;
    }
}