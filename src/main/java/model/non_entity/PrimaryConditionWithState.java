/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import model.non_entity.ConditionWithState;
import java.util.ArrayList;
import model.entity.PrimaryCondition;
import model.entity.PrimaryCondition;

/**
 *
 * @author colin
 */
public class PrimaryConditionWithState extends ConditionWithState {
    public PrimaryConditionWithState(){
        
    }
    
    public PrimaryConditionWithState(PrimaryCondition primaryCondition){
        this.condition = primaryCondition;
    }

}
