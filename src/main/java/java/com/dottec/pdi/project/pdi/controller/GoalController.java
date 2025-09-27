package com.dottec.pdi.project.pdi.controller;

import java.util.ArrayList;
import java.util.List;

import com.dottec.pdi.project.pdi.dao.*;
import com.dottec.pdi.project.pdi.model.*;
import com.dottec.pdi.project.pdi.validator.*;

public class GoalController{
	private GoalDAO goalDAO;
	private GoalValidator validator;
	
    public GoalController(){
    	this.goalDAO = new GoalDAO();
    	this.validator = new GoalValidator();
    }
    
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
    
    public List<Goal> goalsList(){
    	try {    		
    		return goalDAO.readAll();	
    	}catch(Exception e) {
    		System.err.println(e.getMessage());
    		return new ArrayList<>();
    	}
    }
    
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
    
    public boolean updateGoalName(int id, String name) {
        try {    		
            Goal goal = goalDAO.readById(id);
            if (goal == null) {
                System.err.println("Goal não encontrado: ID " + id);
                return false;
            }
            
            if (validator.goalValidatorName(name)) {
                goal.setName(name);
                goalDAO.update(goal); // ← AGORA SALVA NO BANCO
                return true;
            } else { 
                System.out.println("Nome inválido"); 
                return false;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    public boolean updateGoalStatus(int id, String status) {
        try {    		
            Goal goal = goalDAO.readById(id);
            if (goal == null) {
                System.err.println("Goal não encontrado: ID " + id);
                return false;
            }
            
            if (validator.goalValidatorStatus(status)) {
                goal.setStatus(status);
                goalDAO.update(goal);
                return true;
            } else { 
                System.out.println("Status invalido"); 
                return false;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    public boolean updateGoalDescription(int id, String description) {
        try {    		
            Goal goal = goalDAO.readById(id);
            if (goal == null) {
                System.err.println("Goal não encontrado: ID " + id);
                return false;
            }
            
            if (validator.goalValidatorDescription(description)) {
                goal.setDescription(description);
                goalDAO.update(goal);
                return true;
            } else { 
                System.out.println("Description invalido"); 
                return false;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateGoalDeadline(int id, String date) {
        try {    		
            Goal goal = goalDAO.readById(id);
            if (goal == null) {
                System.err.println("Goal não encontrado: ID " + id);
                return false;
            }
            
            if (validator.goalValidatorDate(date)) {
                goal.setDeadline(date);  
                goalDAO.update(goal);
                return true;
            } else { 
                System.err.println("Deadline invalido"); 
                return false;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateGoalCategory(int id, Category category) {
        try {    		
            Goal goal = goalDAO.readById(id);
            if (goal == null) {
                System.err.println("Goal não encontrado: ID " + id);
                return false;
            }
            
            if (validator.goalValidatorDate(category)) {
                goal.setCategory(category);  
                goalDAO.update(goal);
                return true;
            } else { 
                System.err.println("Category invalido"); 
                return false;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
//    public List<Goal> findGoalsByStatus(String status/*ou Status status se usar enum*/){
//    	try {
//            return goalDAO.findByStatus(status);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            return new ArrayList<>();
//        }
//    }	
    
//    public List<Goal> findByStatus(String category/*ou Category category se usar enum*/) {
//    	try{    		
//    		return goalDAO.findByCategory(category);    		
//    	}catch(Exception e) {
//    		System.err.println(e.getMessage());
//    		return new ArrayList<>();
//    	}
//    }
    
//    public List<Goal> findGoalsByEmployee(int employeeId) {
//        try {
//            return goalDAO.findByEmployeeId(employeeId);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            return new ArrayList<>();
//        }
//    }
}