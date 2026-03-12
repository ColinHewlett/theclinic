/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;
import java.awt.Color;

/**
 *
 * @author colin
 */
public class ID{
    public ID(String name, int size, boolean isBold, String color){
        theName = name;
        theSize = size;
        theIsBold = isBold;
        theColor = color;
    }

    private Integer theSize;
    public Integer Size(){
        return theSize;
    }

    private String theName;
    public String Name(){
        return theName;
    }

    private boolean theIsBold;
    public boolean IsBold(){
        return theIsBold;
    }
    
    private String theColor = "000000";
    public String Color(){
        return theColor;
    }
}
