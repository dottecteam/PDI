package com.dottec.pdi.project.pdi.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Teste {
    public static void main(String[] args){
        try (Connection conn = Database.getConnection()) {
            System.out.println("DEU BOM");

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
        }
    }
}
