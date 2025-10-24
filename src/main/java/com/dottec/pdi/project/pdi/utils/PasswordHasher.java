package com.dottec.pdi.project.pdi.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private static final int COST = 16;
    
    public static String hash(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST));
    }
    
    public static boolean verify(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}