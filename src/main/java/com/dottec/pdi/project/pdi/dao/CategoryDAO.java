package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoryDAO {

    private static final String INSERT_SQL = "INSERT INTO tags (tag_name, tag_type) VALUES (?, ?)";

    public void insert(Category category) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getType().name());

            int rows = stmt.executeUpdate();
            System.out.println("Categoria inserida! Linhas: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
    }
}
