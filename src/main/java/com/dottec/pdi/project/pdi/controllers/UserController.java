// ------------- Making the UserController class -------------//
package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.model.User;
import java.util.List;


public class UserController {
    // ------------- User Controller class -------------//
    private final UserDAO userDAO = new UserDAO();

    // ------------- Creating the methods for the UserDao Controller class ------------//
    // -------- Adding user ----------- //

    public boolean addUser(User user) {
        User existingUser = userDAO.findById(user.getId());

        if (existingUser != null) {
            System.out.println("User already exists");
            return false;
        }
        // ---  If there is no such user ---- //

        try {
            userDAO.insert(user);
            return true;
        } catch (Exception e) {
            System.out.println("User insert failed. Error message: " + e.getMessage());
            return false;
        }

    }

    // -------- Remove user method ------ //
    public boolean inactivateUser(int id) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            System.out.println("User not found");
            return false;
        }
        try {
            userDAO.softDelete(id); // Soft Deleting in the Data-Base
            return true;
        } catch (Exception e) {
            System.out.println("User delete failed. Error message: " + e.getMessage());
            return false;
        }
    }
    // -------- Update user --------- //
    public boolean updateUser(User user) {
        User existingUser = userDAO.findById(user.getId());
        if (existingUser == null) {
            System.out.println("User not found");
            return false;
        }
        try {
            userDAO.update(user); // Updating in the Data-Base
            return true;
        } catch (Exception e) {
            System.out.println("User update failed. Error message: " + e.getMessage());
            return false;
        }
    }

    // ---------- Getting User per Id -------//
    public User findById(int id) {
        return userDAO.findById(id);
    }

    // --------- Getting User per Email -------//
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    // ---------- Getting all Users -------//
    public List<User> findAll() {
        return userDAO.listAll();
    }

}