package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Category;
import com.dottec.pdi.project.pdi.enums.CategoryType;
import com.dottec.pdi.project.pdi.controllers.DashboardTagFrequencyController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DashboardDAO {

    public List<DashboardTagFrequencyController> getTopTags() {
        //Lista de dados
        List<DashboardTagFrequencyController> frequencies = new ArrayList<>();

        //Select na tabela de goal_tags para ver quais tags são mais utilizadas
        String sql = "SELECT T.tag_name, COUNT(GT.tag_id) AS frequencia FROM goal_tags AS GT JOIN tags AS T ON GT.tag_id = T.tag_id GROUP BY\n" +
                "T.tag_id, T.tag_name ORDER BY frequencia DESC LIMIT 15;";


        try (
             Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // 4. "Tradução" do SQL para Objetos Java
            while (rs.next()) {
                String nomeTag = rs.getString("tag_name");
                int contagem = rs.getInt("frequencia");

                // Adiciona um novo objeto de dados simples à lista
                frequencies.add(new DashboardTagFrequencyController(nomeTag, contagem));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frequencies;
    }
}
