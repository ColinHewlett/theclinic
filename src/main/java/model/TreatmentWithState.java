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
public class TreatmentWithState{

    public TreatmentWithState(){
        
    }
    
    public TreatmentWithState(Treatment treatment){
        this.treatment = treatment;
    }
    
    private ArrayList<TreatmentWithState> collection = new ArrayList<>();
    public ArrayList<TreatmentWithState> get(){
        return collection;
    }
    public void set(ArrayList<TreatmentWithState> value){
        collection = value;
    }
    
    private Treatment treatment = null;
    public Treatment getTreatment(){
        return treatment;
    }
    public void setTreatment(Treatment value){
        treatment = value;
    }
    
    private Boolean state = false;
    public Boolean getState(){
        return state;
    }
    public void setState(Boolean value){
        state = value;
    }

}
