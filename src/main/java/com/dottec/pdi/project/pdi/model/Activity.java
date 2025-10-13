package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.ActivityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity{
    private int id;
    private String name;
    private String description;

    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    private ActivityStatus status;
    private Goal goal;
    private List<Tag> tags = new ArrayList<>();

    public Activity(){}
    public Activity( int id , String name, String description, LocalDate deadline, LocalDateTime createdAt, LocalDateTime updateAt, ActivityStatus status, Goal goal, List<Tag> tags)
    {
        this.id = id;
        this.name = name;
        this.description = description;

        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updateAt = updateAt;

        this.status = status;
        this.goal = goal;
        this.tags = tags;
    }

    public int getId(){
        return this.id;
    }
    public void setId(int id) {this.id = id;}

    public String getName(){
        return this.name;
    }
    public void setName( String name ){
        this.name = name;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription( String description ){
        this.description = description;
    }

    public LocalDate getDeadline(){
        return this.deadline;
    }
    public void setDeadline( LocalDate deadline ){
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdateAt() {return updateAt;}
    public void setUpdateAt(LocalDateTime updateAt) {this.updateAt = updateAt;}

    public ActivityStatus getStatus(){
        return this.status;
    }
    public void setStatus( ActivityStatus status ){
        this.status = status;
    }

    public Goal getGoal() {return goal;}
    public void setGoal(Goal goal) {this.goal = goal;}

    public List<Tag> getTags() {return tags;}
    public void setTags(List<Tag> tags) {this.tags = tags;}


    public String getStatusMessage(){
        return this.status.name();
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

    public int numberTags() {
        return this.tags.size();
    }

    public void clearTags() {
        this.tags.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Activity other = (Activity) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deadline=" + deadline +
                ", status=" + status +
                '}';
    }
}