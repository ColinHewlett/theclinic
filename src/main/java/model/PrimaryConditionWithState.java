/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class PrimaryConditionWithState {
    public PrimaryConditionWithState(){
        
    }
    
    public PrimaryConditionWithState(PrimaryCondition primaryCondition){
        this.primaryCondition = primaryCondition;
    }
    
    private ArrayList<PrimaryConditionWithState> collection = new ArrayList<>();
    public ArrayList<PrimaryConditionWithState> get(){
        return collection;
    }
    public void set(ArrayList<PrimaryConditionWithState> value){
        collection = value;
    }
    
    private PrimaryCondition primaryCondition = null;
    public PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    public void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value;
    }
    
    private Boolean state = false;
    public Boolean getState(){
        return state;
    }
    public void setState(Boolean value){
        state = value;
    }
    
    private String comment = null;
    public String getComment(){
        return comment;
    }
    public void setComment(String value){
        comment = value;
    }
}
