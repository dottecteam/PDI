package com.dottec.pdi.project.pdi.config;

import java.sql.Connection;
import java.sql.SQLException;

public class Teste {
    public static void main (String[] args) throws SQLException {
        Connection conn = Database.getConnection();
        System.out.println("Conectado");
    }
}
