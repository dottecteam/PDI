package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.enums.TagType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {
    // Comandos SQL
    private static final String INSERT_SQL = "INSERT INTO tags (tag_name, tag_type) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM tags WHERE tag_id = ?";
    private static final String UPDATE_SQL = "UPDATE tags SET tag_name = ?, tag_type = ? WHERE tag_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM tags";
    private static final String FIND_BY_ID_SQL = "SELECT tag_id, tag_name, tag_type FROM tags WHERE tag_id = ?";

    public void insert(Tag tag) {
        try(Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)){
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getType().name());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria inserida! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
    }

    public void delete(Tag tag){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, tag.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria: " + e.getMessage(), e);
        }
    }

    public void update(Tag tag) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)){
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getType().name());
            stmt.setInt(3, tag.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria atualizada! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    public Tag findById(int id) {
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tag tag = new Tag(rs.getInt("tag_id"),rs.getString("tag_name"),TagType.valueOf(rs.getString("tag_type")));
                    return tag;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Tag> readAll() {
        List<Tag> tags = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("tag_id"),
                        rs.getString("tag_name"),
                        TagType.valueOf(rs.getString("tag_type"))
                );
                tags.add(tag);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as tags: " + e.getMessage(), e);
        }
        return tags;
    }

    public void deleteById(int id){
        try (Connection conn = Database.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)){
            stmt.setInt(1, id);

            int rows = stmt.executeUpdate();
            System.out.println("Categoria deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria: " + e.getMessage(), e);
        }
    }
}
