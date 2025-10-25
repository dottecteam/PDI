package com.dottec.pdi.project.pdi.controllers;  // Path to the package

import com.dottec.pdi.project.pdi.dao.DashboardDAO;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardController {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    // -------- Introducing the controller methods to then call the methods from DashboardDAO

    // ✅ Activity Metrics
    public Map<String, Double> getPercentualStatus() throws SQLException {
        return dashboardDAO.getPercentualStatusMap();
    }

    // ----------- Getting the total status ------------ //
    public Map<String, Integer> getTotalStatus() throws SQLException {
        return dashboardDAO.getTotalStatusMap();
    }

    // ---------- Getting the status list --------------- //
    public Map<String, Object[]> getStatusList() throws SQLException {
        return dashboardDAO.getStatusList();
    }

    // ------------ Getting the status percentual ------------//
    public Double getStatusPercentual(String status) throws SQLException {
        return dashboardDAO.getPercentualStatus(ActivityStatus.valueOf(status.toUpperCase()));
    }

    // ---------------- Getting the averagePDI progress ---------//
    // ✅ PDI Metrics
    public Double getAveragePDIProgress() throws SQLException {
        return dashboardDAO.getAveragePDIProgress();
    }

    // ---------------- Getting the tag usage ---------------- //
    // ✅ Tags and Skills
    public Map<String, Object[]> getTagUsage() throws SQLException {
        return dashboardDAO.getTagNameUsagePercentualMap();
    }

    // ---------------- Getting the skills Distribution ---------- //
    public Map<String, Object[]> getSkillsDistribution() throws SQLException {
        return dashboardDAO.getSkillsDistribution();
    }

    // ✅ Department Analytics

    // -------------- Getting the Department Performance ------------- /
    public Map<String, Object[]> getDepartmentPerformance() throws SQLException {
        return dashboardDAO.getDepartmentPerformance();
    }

    // -------------- Getting the top departments which have indeed completed the task
    public Map<String, Integer> getTopDepartmentsCompleted() throws SQLException {
        return dashboardDAO.getTopDepartmentsCompletedPDIs();
    }

    // -------------- Getting top tags ----------------- //
    public Map<String, Integer> getTopTags(int id) throws SQLException {
        return dashboardDAO.getTopDepartmentTags(id);
    }

    // -------------- Getting lowest goals -------------------//
    public List<Object[]> getLowestGoals(int id) throws SQLException {
        return dashboardDAO.getLowestProgressGoals(id);
    }

    // --------------- Getting department history --------------//
    public Map<String, Double> getDepartmentHistory(int id) throws SQLException {
        return dashboardDAO.getDepartmentProgressHistory(id);
    }

    // --------------- Getting completion rate --------------------- //
    public Map<String, Object> getDepartmentCompletionRate(int id) throws SQLException {
        return dashboardDAO.getDepartmentCompletionRate(id);
    }

    // --------------- Getting collaborator progress ------------------- //
    // ✅ Collaborator Analytics
    public Map<String, Object[]> getCollaboratorProgress(int id) throws SQLException {
        return dashboardDAO.getCollaboratorProgress(id);
    }

}