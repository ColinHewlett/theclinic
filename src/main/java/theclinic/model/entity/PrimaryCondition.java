/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.model.entity.Condition;
import java.awt.Point;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class PrimaryCondition extends Condition implements IEntityRepositoryActions {
    private SecondaryCondition secondaryCondition = null;
    
    
            
    public PrimaryCondition(){
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(SecondaryCondition sc){
        secondaryCondition = sc;
        setIsPrimaryCondition(true);
    }
    
    public PrimaryCondition(Integer key){
        setKey(key);
        setIsPrimaryCondition(true);
    }

    public SecondaryCondition getSecondaryCondition(){
                return secondaryCondition;
    }
    public void setSecondaryCondition(SecondaryCondition value){
        secondaryCondition = value;
    }

    @Override 
    public Point count()throws StoreException{
        return getRepository().count(this);
    }
    
    @Override 
    public void create()throws StoreException{
        getRepository().create(this);
    }
        
    @Override 
    public void delete()throws StoreException{
        getRepository().delete(this);
    }
    
    @Override 
    public void drop()throws StoreException{

    }
    
    @Override 
    public Integer insert()throws StoreException{
        return getRepository().insert(this);
    }
    
    @Override 
    public PrimaryCondition read()throws StoreException{
        return getRepository().read(this);
    }
    
    @Override 
    public void update()throws StoreException{
        getRepository().update(this);
    }
}
