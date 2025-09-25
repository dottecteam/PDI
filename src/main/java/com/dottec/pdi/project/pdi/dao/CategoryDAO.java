package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Category;
import com.dottec.pdi.project.pdi.model.enums.CategoryType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO {

    // Comandos SQL
    private static final String INSERT_SQL = "INSERT INTO tags (tag_name, tag_type) VALUES (?, ?)";
    private static final String DELETE_SQL = "DELETE FROM tags WHERE tag_id = ?";
    private static final String UPDATE_SQL = "UPDATE tags SET tag_name = ?, tag_type = ? WHERE tag_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT tag_id, tag_name, tag_type FROM tags WHERE tag_id = ?";


    public void insert(Category category) {
        Database db = null;
        try {
            db = new Database();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria inserida! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
    }



    public void delete(int tag_id){
        Database db = null;
        try {
            db = new Database();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_SQL);

            stmt.setInt(1, tag_id);

            int rows = stmt.executeUpdate();
            System.out.println("Categoria deletada! Linhas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria: " + e.getMessage(), e);

        }
    }



    public void update(Category category) {
        Database db = null;
        try {
            db = new Database();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL);

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());
            stmt.setInt(3, category.getId());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria atualizada! Linhas afetadas: " + rows);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }



    public Category findById(int id) {
        Database db = null;
        try {
            db = new Database();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL);

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("tag_id"));
                    category.setName(rs.getString("tag_name"));
                    category.setType(CategoryType.valueOf(rs.getString("tag_type")));

                    return category;
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria: " + e.getMessage(), e);
        }

        return null;
    }
}
