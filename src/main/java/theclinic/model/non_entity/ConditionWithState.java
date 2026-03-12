/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;

import java.util.ArrayList;
import theclinic.model.entity.Condition;

/**
 *
 * @author colin
 */
public class ConditionWithState {
    
    private ArrayList<ConditionWithState> collection = new ArrayList<>();
    public ArrayList<ConditionWithState> get(){
        return collection;
    }
    public void set(ArrayList<ConditionWithState> value){
        collection = value;
    } 
    
    protected Condition condition = null;
    public Condition getCondition(){
        return condition;
    }
    public void setCondition(Condition value){
        condition = value;
    }
    
    protected Boolean state = false;
    public Boolean getState(){
        return state;
    }
    public void setState(Boolean value){
        state = value;
    }
    
    protected String comment = null;
    public String getComment(){
        return comment;
    }
    public void setComment(String value){
        comment = value;
    }
}
