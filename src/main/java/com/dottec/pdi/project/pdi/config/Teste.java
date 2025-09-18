package com.dottec.pdi.project.pdi.config;

import java.sql.SQLException;

public class Teste {
    public static void main(String[] args){
        try {
            Database db = new Database();
            db.getConnection();
            System.out.println("Conexão realizada com sucesso!");
            db.close();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco: " + e.getMessage());
        }
    }
}
