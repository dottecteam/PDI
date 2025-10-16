package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.AuthDAO;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.enums.UserStatus;
import com.dottec.pdi.project.pdi.utils.PasswordHasher; // Importa a classe que você definiu

public class AuthController {

    private static User loggedUser;

    private AuthController() {}

    public static boolean login(String email, String rawPassword) {
        User user = AuthDAO.findUserByEmail(email);

        if (user == null) {
            System.out.println("Login falhou: Usuário não encontrado.");
            return false;
        }

        if (!PasswordHasher.verify(rawPassword, user.getPasswordHash())) {
            System.out.println("Login falhou: Senha incorreta.");
            return false;
        }

        if (user.getStatus() != UserStatus.active) {
            System.out.println("Login falhou: Usuário inativo (" + user.getStatus().name() + ").");
            return false;
        }

        loggedUser = user;
        System.out.println("Login bem-sucedido para: " + user.getName());
        return true;
    }

    public static void logout() {
        if (loggedUser != null) {
            System.out.println("Logout efetuado para: " + loggedUser.getName());
            loggedUser = null;
        }
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static boolean isAuthenticated() {
        return loggedUser != null;
    }
}