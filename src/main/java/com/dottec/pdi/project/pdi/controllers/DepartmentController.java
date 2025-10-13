package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.model.Department;

import java.util.List;

public class DepartmentController {
    private DepartmentController() {}

    public static List<Department> findAllDepartments() {
        return DepartmentDAO.readAll();
    }

    public boolean addDepartment(Department department){
        Department existingDepartment = DepartmentDAO.findById(department.getId());
        if(existingDepartment != null) {
            System.out.println("Department with ID " + department.getId() + " already exists.");
            return false;
        }
        DepartmentDAO.insert(department);
        return true;
    }

    public boolean deleteDepartment(Department department){
        Department existingDepartment = DepartmentDAO.findById(department.getId());

        if( existingDepartment == null) {
            System.out.println("Department not found for deletion.");
            return false;
        }

        DepartmentDAO.delete(department);
        return true;
    }

    public boolean updateDepartment(Department department){
        Department existingDepartment = DepartmentDAO.findById(department.getId());

        if( existingDepartment == null){
            System.out.println("Department not found for update.");
            return false;
        }

        DepartmentDAO.update(department);
        return true;
    }

    public Department findById(int id){
        Department existingDepartment = DepartmentDAO.findById(id);

        if( existingDepartment == null){
            System.out.println("Department not found with ID: " + id);
        }

        return existingDepartment;
    }
}