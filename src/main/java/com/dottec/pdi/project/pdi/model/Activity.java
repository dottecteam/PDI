package com.dottec.pdi.project.pdi.model;



public class Activity{

    private int id;
    private String name ;
    private String description ;
    private String deadline ;
    private String createdAt;

    private Status status;

    private enum Status{
        CANCELED,
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    private String updateAt;




    public Activity( int id , String name , String description , String createdAt , Status status , String updateAt )
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.updateAt = updateAt;
    }

    public int  getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setName( String name ){
        this.name = name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription( String description ){
        this.description = description;
    }

    public String getDeadline(){
        return this.deadline;
    }

    public void setDeadline( String deadline ){
        this.deadline = deadline;
    }

    public String getCreatedAt(){
        return this.createdAt;
    }


    public Status getStatus(){
        return this.status;
    }

    public void setStatus( Status status ){
        this.status = status;
    }

    public String getStatusMessage(){
        switch (this.status){
            case IN_PROGRESS:
                return "In Progress";
            case COMPLETED:
                return "Completed";
            case CANCELED:
                return "Canceled";
            case PENDING:
                return "Pending";
        }
        return "Unknown status";
    }




}