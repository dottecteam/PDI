package com.dottec.pdi.project.pdi.controller;

import java.com.dottec.pdi.project.pdi.dao.GoalDAO;
import java.com.dottec.pdi.project.pdi.model.Goal;
import java.com.dottec.pdi.project.pdi.validator.GoalValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;
import java.util.function.Consumer;
import java.util.function.Predicate;


import com.dottec.pdi.project.pdi.dao.*;
import com.dottec.pdi.project.pdi.model.*;
import com.dottec.pdi.project.pdi.validator.*;

public class GoalController{
	private final GoalDAO goalDAO;
	private final GoalValidator validator;
	
    public GoalController(){
    	this.goalDAO = new GoalDAO();
    	this.validator = new GoalValidator();
    }


    // -------------- Add goal to the Database ---------- //
    
    public boolean addGoal(String name , String description , String deadline , String category , int employeeId , String status) {
    	try {
    		Goal goal = new Goal(0 ,name, description, deadline, category, employeeId, status);
    		if(validator.goalValidator(goal)) goalDAO.create(goal); 
    		else { System.err.println("Goal inválido"); return false;}
    		return true;
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return false;
    	}
    }
    
    // ------------- Gathering the goals from the database ----------------- //
    public List<Goal> goalsList(){
    	try {    		
    		return goalDAO.readAll();	
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return new ArrayList<>();
    	}
    }
    
    // -------------------- Update Goal --------------------------- //

    public boolean updateGoal(Goal goal) {
    	try {
    		if(validator.goalValidator(goal)) goalDAO.update(goal); 
    		else { System.out.println("Goal inválido"); return false;}
    		return true;
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return false;
    	}
    }
    
    // -------------------- Remove goal -------------------------- //
    public boolean removeGoal(int id) {
    	try {
    		goalDAO.delete(id);
    		return true;
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return false;
    	}
    }	
    
    public Goal findGoal(int id) {
    	try {
    		return goalDAO.readById(id);
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
            return null;
    	}
    }

    // ----------- Private update helper method ------------------

    private boolean updateField( int id, Consumer<Goal> setter, Predicate<Goal> validatorCheck , String errorMessage){
        try{
            Goal goal = goalDAO.readById(id);
            // Cheking to see if the goal exists or not in the database
            if( goal == null){
                System.err.println("Meta não encontrada");
                return false;
            }
            if ( validatorCheck.test(goal)){
                setter.accept(goal);
                goalDAO.update(goal);
                return true;
            }else{
                System.err.println(errorMessage);
                return false;
            } 
        }catch ( Exception e){
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    // --------------- Update Fields ----------------- //

    public boolean updateGoalName(int id, String name) {
        return updateField(id, g -> g.setName(name), g -> validator.goalValidatorName(name), "Nome inválido");
    }

    public boolean updateGoalStatus(int id, String status) {
        return updateField(id, g -> g.setStatus(status), g -> validator.goalValidatorStatus(status), "Status inválido");
    }

    public boolean updateGoalDescription(int id, String description) {
        return updateField(id, g -> g.setDescription(description), g -> validator.goalValidatorDescription(description), "Descrição inválida");
    }

    public boolean updateGoalDeadline(int id, String deadline) {
        return updateField(id, g -> g.setDeadline(deadline), g -> validator.goalValidatorDate(deadline), "Deadline inválida");
    }

    public boolean updateGoalCategory(int id, Category category) {
        return updateField(id, g -> g.setCategory(category), g -> validator.goalValidatorCategory(category), "Categoria inválida");
    }


}