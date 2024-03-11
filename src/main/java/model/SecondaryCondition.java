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
public class SecondaryCondition extends Condition implements IEntityStoreActions {
    private Integer key = null;
    private PrimaryCondition primaryCondition = null;
    private ArrayList<SecondaryCondition> collection = new ArrayList<>();

    public SecondaryCondition(){
        setIsSecondaryCondition(true);
    }
    
    public SecondaryCondition(Integer key){
        this.key = key;
        setIsSecondaryCondition(true);
    }

    public SecondaryCondition(PrimaryCondition pc){
        primaryCondition = pc;
        setIsSecondaryCondition(true);
    }

    public ArrayList<SecondaryCondition> get(){
        return collection;
    }
    public void set(ArrayList<SecondaryCondition> value){
        collection = value;
    }
    
    
    public Integer getKey(){
        return key;
    }
    public void setKey(Integer value){
        key = value;
    }

    public PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    public void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value;
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
    public SecondaryCondition read()throws StoreException{
        return new Repository().read(this);
    }

    @Override 
    public void update()throws StoreException{
        new Repository().update(this);
    }
}
