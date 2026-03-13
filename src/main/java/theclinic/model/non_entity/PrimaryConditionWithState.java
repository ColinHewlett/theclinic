/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;

import theclinic.model.non_entity.ConditionWithState;
import java.util.ArrayList;
import theclinic.model.entity.PrimaryCondition;
import theclinic.model.entity.PrimaryCondition;

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
