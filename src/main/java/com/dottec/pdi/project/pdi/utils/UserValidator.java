package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.model.enums.Role;
import java.util.regex.Pattern;

public class UserValidator {

    // Exige que o e-mail comece com uma letra (a-z ou A-Z).
    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9._%+-]*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    private static final Pattern PATTERN_PASSWORD =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$)$");

    /*
     * (?=.*[0-9])       - Pelo menos um dígito
     * (?=.*[a-z])       - Pelo menos uma letra minúscula
     * (?=.*[A-Z])       - Pelo menos uma letra maiúscula
     * (?=\\S+$)         - Sem espaços em branco
     */

    public static boolean emailValidate(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return PATTERN_EMAIL.matcher(email).matches();
    }

    public static boolean passwordValidate(String password) {
        if (password == null) {
            return false;
        }
        return PATTERN_PASSWORD.matcher(password).matches();
    }

    public static boolean roleValidate(Role role) {
        return role != null;
    }
}
