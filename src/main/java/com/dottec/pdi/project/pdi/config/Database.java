package com.dottec.pdi.project.pdi.config;

// Imports
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Connection Database Class
public class Database {

// File .env must be in PDI with DB_URL, DB_USER, DB_PASSWORD
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");
    private static Connection connection;

   // Constructor
    public Database() throws SQLException{
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getConnection(){
        return connection;
    }

    public void close(){
        if (connection != null){
            try{
                connection.close();
                System.out.println("Conexão Encerrada.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
