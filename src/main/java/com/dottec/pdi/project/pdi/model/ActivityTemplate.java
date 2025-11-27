package com.dottec.pdi.project.pdi.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ActivityTemplate {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private GoalTemplate goalTemplate;

    public ActivityTemplate(){};

    public ActivityTemplate(int id, String name, String description, GoalTemplate goalTemplate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.goalTemplate = goalTemplate;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public GoalTemplate getGoalTemplate() {return goalTemplate;}
    public void setGoalTemplate(GoalTemplate goalTemplate) {this.goalTemplate = goalTemplate;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ActivityTemplate other = (ActivityTemplate) obj;
        return id == other.getId();
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + description +
                '}';
    }
}
