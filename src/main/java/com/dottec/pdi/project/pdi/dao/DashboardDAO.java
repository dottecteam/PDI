package com.dottec.pdi.project.pdi.dao;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.Category;
import com.dottec.pdi.project.pdi.enums.CategoryType;
import com.dottec.pdi.project.pdi.controllers.DashboardTagFrequencyController;
import com.dottec.pdi.project.pdi.controllers.DashboardStatusData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DashboardDAO {


    //INTERAÇÕES COM O BANCO PARA *GERENTE GERAL*
    public static List<DashboardTagFrequencyController> getTopTags() {
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



    //INTERAÇÕES NO BANCO PARA *GERENTE DE SETOR*
    public static List<DashboardTagFrequencyController> getTopTagsDepartment(int id) {
        //Lista de dados
        List<DashboardTagFrequencyController> frequencies = new ArrayList<>();

        //Select na tabela de departments para ver quais tags são mais utilizadas por setor
        String sql = "SELECT T.tag_name, COUNT(T.tag_id) AS frequencia FROM goal_tags AS GT JOIN goals AS G ON GT.goal_id = G.goa_id JOIN \n" +
                "collaborators AS C ON G.collaborator_id = C.col_id JOIN tags AS T ON GT.tag_id = T.tag_id WHERE C.department_id = ? GROUP BY \n" +
                "T.tag_id, T.tag_name ORDER BY frequencia DESC LIMIT 15;";


        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            //Define o ID do departamento no '?' da query
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                // 3. O resto é idêntico: iterar e "traduzir"
                while (rs.next()) {
                    String tagName = rs.getString("tag_name");
                    int frequencia = rs.getInt("frequencia");

                    // 4. Cria o novo "record" de dados
                    frequencies.add(new DashboardTagFrequencyController(tagName, frequencia));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frequencies;
    }

    public static List<DashboardStatusData> getGoalStatusCountsForDepartment(int departmentId) {

        List<DashboardStatusData> statusCounts = new ArrayList<>();

        String sql = "SELECT G.goa_status, COUNT(G.goa_id) AS quantidade\n" +
                "FROM goals AS G JOIN collaborators AS C ON \n" +
                "G.collaborator_id = C.col_id WHERE C.department_id = ?\n" +
                "AND G.goa_status IN ('completed', 'in_progress') GROUP BY G.goa_status;";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("goa_status");
                    int quantidade = rs.getInt("quantidade");

                    statusCounts.add(new DashboardStatusData(status, quantidade));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contagem de status de metas por setor: " + e.getMessage(), e);
        }

        return statusCounts;
    }
}
