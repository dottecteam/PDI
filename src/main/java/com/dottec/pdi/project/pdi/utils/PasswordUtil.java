package com.dottec.pdi.project.pdi.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int BCRYPT_LOG_ROUNDS = 12;

    public static String hashPassword(String password) {
        //Gera um valor aleatório e faz o hash em uma única chamada
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    // Verifica a senha
    public static boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }
}
