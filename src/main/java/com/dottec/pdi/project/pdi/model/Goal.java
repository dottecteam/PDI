package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.GoalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Goal {
    private int id;
    private String name;
    private String description;

    private GoalStatus status;

    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Collaborator collaborator;
    private List<Activity> activities = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();

    public Goal() {
    }

    public Goal(int id, String name, String description, GoalStatus status, LocalDate deadline, LocalDateTime createdAt, LocalDateTime updatedAt, Collaborator collaborator, List<Activity> activities, List<Tag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.collaborator = collaborator;
        this.activities = activities;
        this.tags = tags;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GoalStatus getStatus() {
        return this.status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public List<Activity> getActivities() {
        return this.activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }


    public String getStatusMessage(){
        return this.status.name();
    }

    public int numberActivities() {
        return this.activities.size();
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public void updateActivity(Activity updatedActivity) {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getId() == updatedActivity.getId()) {
                activities.set(i, updatedActivity);
                return; // Encerra o método após encontrar e atualizar
            }
        }
    }

    public void removeActivity(int id) {
        this.activities.removeIf(activity -> activity.getId() == id);
    }

    public Activity getActivityById(int id) {
        for (Activity activity : activities) {
            if (activity.getId() == id) {
                return activity;
            }
        }
        return null;
    }

    public String getActivityStatusMessageById(int id) {
        Activity activity = getActivityById(id);
        return (activity != null) ? activity.getStatusMessage() : null;
    }

    public String getActivityNameById(int id) {
        Activity activity = getActivityById(id);
        return (activity != null) ? activity.getName() : null;
    }

    public String getActivityDescriptionById(int id) {
        Activity activity = getActivityById(id);
        return (activity != null) ? activity.getDescription() : null;
    }

    public LocalDate getDeadlineById(int id) {
        Activity activity = getActivityById(id);
        return (activity != null) ? activity.getDeadline() : null;
    }

    public LocalDateTime getCreatedById(int id) {
        Activity activity = getActivityById(id);
        return (activity != null) ? activity.getCreatedAt() : null;
    }

    public int numberTags() {
        return this.tags.size();
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTag(int id) {
        this.tags.removeIf(tag -> tag.getId() == id);
    }

    public Tag getTagById(int id) {
        for (Tag tag : tags) {
            if (tag.getId() == id) {
                return tag;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Goal other = (Goal) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", deadline=" + deadline +
                '}';
    }
}