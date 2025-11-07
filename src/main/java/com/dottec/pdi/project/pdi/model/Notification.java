// ---- Creating the Notification class ----- //

// ---- Imports ---- //
package com.dottec.pdi.project.pdi.model;
import com.dottec.pdi.project.pdi.enums.NotificationType;
import java.sql.Timestamp;


public class Notification {

    // -- Initializing class attributes --- //
    private int notId;
    private String notMessage;
    private NotificationType notType;
    private boolean notIsRead;
    private Timestamp notCreatedAt;
    private int userId;

    // Default constructor (required for frameworks)
    public Notification() {}

    // Parameterized constructor (for convenience)
    public Notification(int notId, String notMessage, NotificationType notType, boolean notIsRead, Timestamp notCreatedAt, int userId) {
        this.notId = notId;
        this.notMessage = notMessage;
        this.notType = notType;
        this.notIsRead = notIsRead;
        this.notCreatedAt = notCreatedAt;
        this.userId = userId;
    }

    // Getters and Setters
    public int getNotId() {
        return notId;
    }

    public void setNotId(int notId) {
        this.notId = notId;
    }

    public String getNotMessage() {
        return notMessage;
    }

    public void setNotMessage(String notMessage) {
        this.notMessage = notMessage;
    }

    public NotificationType getNotType() {
        return notType;
    }

    public void setNotType(NotificationType notType) {
        this.notType = notType;
    }

    public boolean isNotIsRead() {
        return notIsRead;
    }

    public void setNotIsRead(boolean notIsRead) {
        this.notIsRead = notIsRead;
    }

    public Timestamp getNotCreatedAt() {
        return notCreatedAt;
    }

    public void setNotCreatedAt(Timestamp notCreatedAt) {
        this.notCreatedAt = notCreatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
