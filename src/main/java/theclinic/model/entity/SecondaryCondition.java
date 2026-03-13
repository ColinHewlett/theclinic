/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.model.entity.PrimaryCondition;
import theclinic.model.entity.Condition;
import java.awt.Point;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class SecondaryCondition extends Condition implements IEntityRepositoryActions {
    private PrimaryCondition primaryCondition = null;
    

    public SecondaryCondition(){
        setIsSecondaryCondition(true);
    }
    
    public SecondaryCondition(Integer key){
        setKey(key);
        setIsSecondaryCondition(true);
    }

    public SecondaryCondition(PrimaryCondition pc){
        primaryCondition = pc;
        setIsSecondaryCondition(true);
    }

    public PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    public void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value;
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
    public SecondaryCondition read()throws StoreException{
        return getRepository().read(this);
    }

    @Override 
    public void update()throws StoreException{
        getRepository().update(this);
    }
}
