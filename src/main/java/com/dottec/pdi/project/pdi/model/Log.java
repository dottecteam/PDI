package com.dottec.pdi.project.pdi.model;

import java.sql.Timestamp;

public class Log {

    private int logId;
    private String logAction;
    private String details;
    private Timestamp logCreatedAt;
    private int userId;

    public Log(int logId, String logAction, String details, Timestamp logCreatedAt, int userId) {
        this.logId = logId;
        this.logAction = logAction;
        this.details = details;
        this.logCreatedAt = logCreatedAt;
        this.userId = userId;
    }

    public int getLogId() {
        return this.logId; // Setting the get and set methods for the id Parameters
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getLogAction() {
        return this.logAction;
    }

    public void setLogAction(String logAction) {
        this.logAction = logAction;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getLogCreatedAt() {
        return this.logCreatedAt;
    }

    public void setLogCreatedAt(Timestamp logCreatedAt) {
        this.logCreatedAt = logCreatedAt;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
