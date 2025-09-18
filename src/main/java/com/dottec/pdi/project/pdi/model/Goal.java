package com.dottec.pdi.project.pdi.model;

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Goal{

    private int id ;
    private String name;
    private String description;
    private String deadline;
    private String category;
    private int employeeId;
    private String status;

    public List<Activity> activities = new ArrayList<>();


    public Goal( int id , String name , String description , String deadline , String category , int employeeId , String status ){
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.category = category;
        this.employeeId = employeeId;
        this.status = status;

    }

    // Creating the get and set methods for the Goal class
    // Allowing the class to get and update its attributes

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDeadline() {
        return this.deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public String getCategory() {
        return this.category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getEmployeeId() {
        return this.employeeId;
    }
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // This division of the code will be responsible for creating the methods that can interact
    // with the list of activities by using the object Activity

    public List<Activity> getActivities() {
        return this.activities;
    }

    // Returning the number of activities by each Goal
    public int numberActivities(){
        if(this.activities.isEmpty()){
            return 0;
        }
        return this.activities.size();
    }

    // Adding a new activity, by utilizing the Object Activity defined on the other file
    public void addActivity(Activity activity){
        this.activities.add(activity);
    }

    // Removing already existing activity
    public void removeActivity(int id){
        if(this.activities.isEmpty()){
            System.out.println("No activities found");
        }

        Iterator<Activity> iterator = this.activities.iterator(); // Making a search in the array to find which activity corresponds to the given id
        while(iterator.hasNext()){ // Iterating the array to find the matching element
            Activity a = iterator.next();
            if(a.getId() == id){
                iterator.remove();
                break;
            }
        }
    }


    public Activity getActivityById(int id){
        for ( Activity activity : activities){
            if(activity.getId() == id){
                return activity;
            }
        }
        return null;
    }

    public String getStatusMessageById(int id){
        for ( Activity activity : activities){
            if(activity.getId() == id){
                return activity.getStatusMessage();
            }
        }
        return "Unknown status";
    }

    public String getActivityNameById(int id){
        for ( Activity activity : activities){
            if(activity.getId() == id){
                return activity.getName();
            }
        }
        return "Unknown activity";
    }













}