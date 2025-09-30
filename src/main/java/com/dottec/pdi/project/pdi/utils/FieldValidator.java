package com.dottec.pdi.project.pdi.utils;

import java.util.regex.Pattern;

public class FieldValidator {
    private static final String EMAIL_REGEX = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    public static boolean validarEmail(String email) {
        return Pattern.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email);
    }

    public static boolean validarCampo(String text){
        return text != null && !text.trim().isEmpty();
    }

    public static boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            return false;
        } else if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        } else {
            int soma = 0;

            for(int i = 0; i < 9; ++i) {
                soma += (cpf.charAt(i) - 48) * (10 - i);
            }

            int resto = 11 - soma % 11;
            int digito1 = resto != 10 && resto != 11 ? resto : 0;
            soma = 0;

            for(int i = 0; i < 10; ++i) {
                soma += (cpf.charAt(i) - 48) * (11 - i);
            }
            resto = 11 - soma % 11;
            int digito2 = resto != 10 && resto != 11 ? resto : 0;
            return digito1 == cpf.charAt(9) - 48 && digito2 == cpf.charAt(10) - 48;
        }
    }
}
