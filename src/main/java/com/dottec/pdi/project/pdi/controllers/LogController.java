package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.LogDAO;
import com.dottec.pdi.project.pdi.model.Log;
import java.util.List;


public class LogController {

    private LogController(){};

    // ------- Adding methods that call the LogDAO methods ---- //
    public static void addLog(Log log) {
        try {
            LogDAO.insert(log);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    // --------- Delete log from the DataBase ------- //
    public static  boolean deleteLog(int id) {
        Log existingLog = LogDAO.getById(id);
        if (existingLog == null) {
            return false;
        }
        try {
            LogDAO.delete(id);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    // ------------ Method find log by ID -------- //

    public static  Log getLog(int id) {
        try {
            return LogDAO.getById(id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static boolean updateLog(Log log) {
        Log existingLog = LogDAO.getById(log.getLogId());

        if (existingLog == null) {
            return false;
        }
        try {
            LogDAO.update(log);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    public static List<Log> getAllLogs() {
        try {
            return LogDAO.getAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}