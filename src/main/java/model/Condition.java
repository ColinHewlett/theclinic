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
public class Condition extends Entity {
    Integer key = null;
    private String description = null;
    private ArrayList<Condition> collection = new ArrayList<>();

    public ArrayList<Condition> get(){
        return collection;
    }
    public void set(ArrayList<Condition> value){
        collection = value;
    }    
    public String getDescription(){
        return description;
    }
    public void setDescription(String value){
        description = value;
    }
     public Integer getKey(){
                return key;
    }
    public void setKey(Integer value){
        key = value;
    }
}
