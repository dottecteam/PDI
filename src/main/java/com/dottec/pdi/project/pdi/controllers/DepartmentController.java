package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.model.Department;

import java.util.List;

public class DepartmentController {
    public static List<Department> findAllDepartments() {
        return DepartmentDAO.readAll();
    }
}