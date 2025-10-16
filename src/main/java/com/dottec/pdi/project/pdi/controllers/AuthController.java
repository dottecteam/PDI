package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.config.Database;
import com.dottec.pdi.project.pdi.model.enums.Status;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.model.enums.Role;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthController {

    //Guarda o usuario
    private static User loggedUser;

    //Retorna o usuário atualmente logado
    public static User getLoggedUser() {
        return loggedUser;
    }

    //Faz login e autentica no banco
    public static boolean login(String email, String password) {
        String sql = "SELECT * FROM users WHERE use_email = ? AND use_password_hash = ?";

        Database db = null;
        try{
            db = new Database();
            Connection conn = db.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, password);



            ResultSet login = stmt.executeQuery();

            if (login.next()) {
                Status status = Status.valueOf(login.getString("use_status").toUpperCase());
                Role role = Role.valueOf(login.getString("use_role").toUpperCase());

                loggedUser = new User(
                        login.getInt("use_id"),
                        login.getString("use_name"),
                        login.getString("use_email"),
                        login.getString("use_password_hash"),
                        login.getInt("department_id"),
                        role,
                        status
                );
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
        }

        return false;
    }

    // Faz logout limpando o usuário logado
    public static void logout() {
        loggedUser = null;
    }

    //Verifica se há um usuário logado
    public static boolean isAuthenticated() {
        return loggedUser != null;
    }
}