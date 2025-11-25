package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.model.Log;

import java.util.List;

public class UserController {
    private UserController() {
    }

    public static boolean addUser(User user) {
        if (UserDAO.findById(user.getId()) != null) {
            System.out.println("User already exists with ID: " + user.getId());
            return false;
        }

        try {
            UserDAO.insert(user);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("CREATE_USER");
                log.setLogDetails("User created: " + user.getName() + " (" + user.getEmail() + ")");
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }

            return true;
        } catch (Exception e) {
            System.err.println("User insert failed. Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean inactivateUser(int id) {
        User user = UserDAO.findById(id);
        if (user == null) {
            System.out.println("User not found with ID: " + id);
            return false;
        }

        try {
            UserDAO.softDelete(user);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("INACTIVATE_USER");
                log.setLogDetails("User inactivated: " + user.getName() + " (ID: " + user.getId() + ")");
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }

            return true;
        } catch (Exception e) {
            System.err.println("User soft delete failed for ID " + id + ". Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateUser(User user) {
        if (UserDAO.findById(user.getId()) == null) {
            System.out.println("User not found for update with ID: " + user.getId());
            return false;
        }

        try {
            UserDAO.update(user);

            User loggedUser = AuthController.getInstance().getLoggedUser();
            if (loggedUser != null) {
                Log log = new Log();
                log.setLogAction("UPDATE_USER");
                log.setLogDetails("User updated: " + user.getName() + " (ID: " + user.getId() + ")");
                log.setLogUserId(loggedUser.getId());
                LogController.addLog(log);
            }

            return true;
        } catch (Exception e) {
            System.err.println("User update failed for ID " + user.getId() + ". Error: " + e.getMessage());
            return false;
        }
    }

    public static User findById(int id) {
        return UserDAO.findById(id);
    }

    public static User findByEmail(String email) {
        return UserDAO.findByEmail(email);
    }

    public static List<User> findAll() {
        return UserDAO.listAll();
    }
}