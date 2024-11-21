/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import java.awt.Point;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import model.repository.StoreException;
import model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class ToDo extends Entity implements IEntityRepositoryActions{
    
    public ToDo(){
        setIsToDo(true);
    }
    
    public ToDo(User user){
        setIsToDo(true);
        this.user = user;
    }
    
    
    private ArrayList<ToDo> collection = null;
    public void set(ArrayList<ToDo> value){
        collection = value;
    }
    public ArrayList<ToDo> get(){
        return collection;
    }
    
    
    private User user = null;
    public void setUser(User value){
        user = value;
    }
    public User getUser(){
        return user;
    }
    
    private String description = null;
    public void setDescription(String value){
        description = value;
    }
    public String getDescription(){
        return description;
    }
    
    private LocalDate date = null;
    public void setDate(LocalDate value){
        date = value;
    }
    public LocalDate getDate(){
        return date;
    }
    
    private Boolean isActioned = null;
    public void setIsActioned(Boolean value){
        isActioned = value;
    }
    public Boolean getIsActioned(){
        if (isActioned == null)isActioned = false;
        return isActioned;
    }
    
    public void cancel() throws StoreException{
        new Repository().cancel(this);
    }

    @Override
    public Point count()throws StoreException{
        return new Repository().count(this);
    }
    
    /**
     * Creates a new Notification table in persistent store
     * @throws StoreException 
     */
    @Override
    public void create() throws StoreException{
        new Repository().create(this);
    }
    
    /**
     * Method updates this notification's isDeleted property to true
     * @throws StoreException 
     */
    @Override
    public void delete() throws StoreException{
        new Repository().delete(this);
    }
    
    /**
     * Not currently implemented
     * @throws StoreException 
     */
    @Override
    public void drop() throws StoreException{
        
    }
    
    /**
     * method sends message to store to insert this patient notification
     * -- the store returns the key value of the inserted notification
     * -- this is used to initialise this patient notification's key
     * -- redundant op because store initialises notification's key value anyway
     * -- but store object might not; i.e. not a contractual obligation in store to do so
     * -- whereas this way a key value us expected back from the store
     * @throws StoreException 
     */
    @Override
    public Integer insert() throws StoreException{
        setKey(new Repository().insert(this));
        return getKey();
    }
    
    /**
     * scope of entity fetch from store is specified on entry; thus
     * -- SINGLE scope
     * ---- fetches this patient notification from persistent store
     * ---- fields in the returned notification's patient are uninitialised except for the key field
     * -- FOR_PATIENT scope
     * ---- fetches from persistent store patient notifications belonging to this patient notification
     * ---- for all other scopes, fetches all notifications consistent with the scope (typically INACTIONED)
     * 
     * @return Notification
     * @throws StoreException 
     */
    @Override
    public ToDo read() throws StoreException{
        Iterator it;
        ToDo result = null; 
        result = new Repository().read(this);
        switch (getScope()){
            case SINGLE:
                break;
            default:
                if (result!=null) 
                    set(result.get());
                else 
                    set(new ArrayList<>());
                result = this;
        }
        return result;
    }
    
    @Override
    public void update()throws StoreException{
        new Repository().update(this);
    }
}

