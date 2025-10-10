package com.dottec.pdi.project.pdi.model;

import com.dottec.pdi.project.pdi.enums.TagType;
import java.util.Objects;

public class Tag {
    private int id;
    private String name;
    private TagType type;

    public Tag() {}

    public Tag(int id, String name, TagType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public TagType getType() {
        return type;
    }
    public void setType(TagType type) {
        this.type = type;
    }

    public String getTypeMessage(){
        return this.type.name();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag other = (Tag) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
