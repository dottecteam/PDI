package com.dottec.pdi.project.pdi.model;
import java.time.LocalDateTime;
import java.util.Objects;

public class GoalTemplates {
    private int goa_tmp_id;
    private LocalDateTime goa_tmp_created_at; 
    private String goa_tmp_name;
    private String goa_tmp_description;

    public GoalTemplates(){}

    public GoalTemplates(int goa_tmp_id, LocalDateTime goa_tmp_created_at, String goa_tmp_name, String goa_tmp_description){
        this.goa_tmp_id = goa_tmp_id;
        this.goa_tmp_created_at = goa_tmp_created_at;
        this.goa_tmp_name = goa_tmp_name;
        this.goa_tmp_description = goa_tmp_description;
    }

    public int getGoa_tmp_id() {
        return goa_tmp_id;
    }

    public void setGoa_tmp_id(int goa_tmp_id) {
        this.goa_tmp_id = goa_tmp_id;
    }

    public LocalDateTime getGoa_tmp_created_at() {
        return goa_tmp_created_at;
    }

    public void setGoa_tmp_created_at(LocalDateTime goa_tmp_created_at) {
        this.goa_tmp_created_at = goa_tmp_created_at;
    }

    public String getGoa_tmp_name() {
        return goa_tmp_name;
    }

    public void setGoa_tmp_name(String goa_tmp_name) {
        this.goa_tmp_name = goa_tmp_name;
    }

    public String getGoa_tmp_description() {
        return goa_tmp_description;
    }

    public void setGoa_tmp_description(String goa_tmp_description) {
        this.goa_tmp_description = goa_tmp_description;
    }

    @Override
    public String toString() {
        return "GoalTemplates{" +
                "goa_tmp_id=" + goa_tmp_id +
                ", goa_tmp_created_at=" + goa_tmp_created_at +
                ", goa_tmp_name='" + goa_tmp_name + '\'' +
                ", goa_tmp_description='" + goa_tmp_description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoalTemplates that = (GoalTemplates) o;
        return goa_tmp_id == that.goa_tmp_id &&
                Objects.equals(goa_tmp_created_at, that.goa_tmp_created_at) &&
                Objects.equals(goa_tmp_name, that.goa_tmp_name) &&
                Objects.equals(goa_tmp_description, that.goa_tmp_description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goa_tmp_id, goa_tmp_created_at, goa_tmp_name, goa_tmp_description);
    }
}