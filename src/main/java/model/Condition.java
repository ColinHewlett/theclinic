/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Point;
/**
 *
 * @author colin
 */
public class Condition extends Entity {
    private String description = null;
    private boolean state = false;

    public String getDescription(){
        return description;
    }
    public void setDescription(String value){
        description = value;
    }

    public boolean getState(){
        return state;
    }
    public void setState(boolean value){
        state = value;
    }
}
