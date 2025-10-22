package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.model.User;

public class AuthController {
    private static AuthController instance;
    private User loggedUser;

    private AuthController() {}

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    // --- MÉTODOS DE INSTÂNCIA (NÃO-STATIC) ---

    public void login(User user) {
        // 'this' funciona aqui porque é um método de instância
        this.loggedUser = user;
    }

    // Retorna o usuário atualmente logado
    public User getLoggedUser() {
        return this.loggedUser;
    }

    // Faz logout limpando o usuário logado
    public void logout() {
        this.loggedUser = null;
    }

    // Verifica se há um usuário logado
    public boolean isAuthenticated() {
        return this.loggedUser != null;
    }
}