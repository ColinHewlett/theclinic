/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author colin
 */
public class ID{
    public ID(String name, int size, boolean isBold){
        theName = name;
        theSize = size;
        theIsBold = isBold;
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
}
