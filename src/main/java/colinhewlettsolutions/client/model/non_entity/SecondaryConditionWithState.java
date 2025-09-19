/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.non_entity;

import colinhewlettsolutions.client.model.non_entity.ConditionWithState;
import java.util.ArrayList;
import colinhewlettsolutions.client.model.entity.SecondaryCondition;

/**
 *
 * @author colin
 */
public class SecondaryConditionWithState extends ConditionWithState{
    public SecondaryConditionWithState(){
        
    }
    
    public SecondaryConditionWithState(SecondaryCondition secondaryCondition){
        this.condition = secondaryCondition;
    }
    
    /*
    private ArrayList<SecondaryConditionWithState> collection = new ArrayList<>();
    public ArrayList<SecondaryConditionWithState> get(){
        return collection;
    }
    public void set(ArrayList<SecondaryConditionWithState> value){
        collection = value;
    }
    
    private SecondaryCondition secondaryCondition = null;
    public SecondaryCondition getSecondaryCondition(){
        return secondaryCondition;
    }
    public void setSecondaryCondition(SecondaryCondition value){
        secondaryCondition = value;
    }
    */
    /*
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
*/
}
