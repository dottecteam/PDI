package com.dottec.pdi.project.pdi.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Attachment {
    private int id;
    private String filePath;
    private LocalDateTime uploadedAt;
    private User uploadedBy;
    private Goal goal;
    private Activity activity;

    public Attachment() {
    }

    public Attachment(int id, String filePath, LocalDateTime uploadedAt, User uploadedBy, Goal goal, Activity activity) {
        this.id = id;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
        this.goal = goal;
        this.activity = activity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}