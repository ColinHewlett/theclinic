/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Point;
import java.util.ArrayList;
import repository.Repository;
import repository.StoreException;

/**
 *
 * @author colin
 */
public class PrimaryCondition extends Condition implements IEntityStoreActions {
    private Integer key = null;
    private SecondaryCondition secondaryCondition = null;
            
    public PrimaryCondition(){
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(SecondaryCondition sc){
        secondaryCondition = sc;
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(Integer key){
        this.key = key;
        setIsPrimaryCondition(true);
    }

    public Integer getKey(){
                return key;
    }
    public void setKey(Integer value){
        key = value;
    }
    
    public SecondaryCondition getSecondaryCondition(){
                return secondaryCondition;
    }
    public void setSecondaryCondition(SecondaryCondition value){
        secondaryCondition = value;
    }

    @Override 
    public Point count()throws StoreException{
        return new Repository().count(this);
    }
    
    @Override 
    public void create()throws StoreException{
        new Repository().create(this);
    }
        
    @Override 
    public void delete()throws StoreException{
        new Repository().delete(this);
    }
    
    @Override 
    public void drop()throws StoreException{

    }
    
    @Override 
    public Integer insert()throws StoreException{
        return new Repository().insert(this);
    }
    
    @Override 
    public PrimaryCondition read()throws StoreException{
        return new Repository().read(this);
    }
    
    @Override 
    public void update()throws StoreException{
        new Repository().update(this);
    }
}
