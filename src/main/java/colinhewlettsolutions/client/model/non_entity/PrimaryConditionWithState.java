/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.non_entity;

import colinhewlettsolutions.client.model.non_entity.ConditionWithState;
import java.util.ArrayList;
import colinhewlettsolutions.client.model.entity.PrimaryCondition;
import colinhewlettsolutions.client.model.entity.PrimaryCondition;

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
