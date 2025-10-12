package com.dottec.pdi.project.pdi.utils;


//adicionar o BCrypt no arquivo do maven
public class PasswordHasher {
    private static final int COST = 16;
    
    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST));
    }
    
    public static boolean verify(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}