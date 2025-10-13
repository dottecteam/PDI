// --------------- Creating the DepartmentController script ---------- //

package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.model.Department;


public class DepartmentController {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();


    // -------------- Creating the methods for the DepartmentDao Controller class ------------//

    // -------- Adding department ----------- //
    public boolean addDepartment( Department department){
        Department existingDepartment = departmentDAO.findById(department.getId());

        if( existingDepartment != null){
            System.out.println( "Department already exists" );
            return false;
        }
        // ---  If there is no such department ---- //

        try{
            departmentDAO.insert(department);
            return true;
        }catch( Exception e){
            System.out.println( "Department insert failed. Error message: " + e.getMessage() );
            return false;
        }

    }

    // -------- Remove department method ------ //
    public boolean deleteDepartment( int id){
        Department existingDepartment = departmentDAO.findById(id);

        if( existingDepartment == null) {
            System.out.println("Department not found");
            return false;
        }

        try {
            departmentDAO.delete(id); // Deleting in the Data-Base
            return true;
        }catch ( Exception e ) {
            System.out.println("Department delete failed. Error message: " + e.getMessage());
            return false;
        }
    }

    // -------- Update department --------- //
    public boolean  updateDepartment( Department department){
        Department existingDepartment = departmentDAO.findById(department.getId());

        if( existingDepartment == null){
            System.out.println("Department not found");
            return false;
        }

        try{
            departmentDAO.update(department);
            return true;
        }catch( Exception e){
            System.out.println( "Department update failed. Error message: " + e.getMessage() );
            return false;
        }

    }

    // ---------- Getting Department per Id -------//

    public Department findById(int id){
        return departmentDAO.findById(id);
    }
}
