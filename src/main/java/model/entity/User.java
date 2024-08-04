/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import java.awt.Point;
import java.util.ArrayList;
import model.IEntityStoreActions;
import repository.Repository;
import repository.StoreException;

/**
 *
 * @author colin
 */
public class User extends Entity implements IEntityStoreActions{
   
    public User(Integer value){
        setKey(value);
    }
    
    public User(){}
    
    private ArrayList<User> collection = new ArrayList<>();
    public ArrayList<User> get(){
        return collection;
    }
    public void set(ArrayList<User> value){
        collection = value;
    }
    
    private byte[] password = null;
    public void setPassword(byte[] value){
        password = value;
    }
    public byte[] getPassword(){
        return password;
    }
    
    private byte[] salt = null;
    public void setSalt(byte[] value){
        salt = value;
    }
    public byte[] getSalt(){
        return salt;
    }
    
    private String username = null;
    public void setUsername(String value){
        username = value;
    }
    public String getUsername(){
        return username;
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
        Integer value = null;
        value = new Repository().insert(this);
        setKey(value);
        return getKey();
    }
    
    @Override
    public User read()throws StoreException{
        return new Repository().read(this);
    }

    @Override 
    public void update()throws StoreException{
        new Repository().update(this);
    }
    
}
