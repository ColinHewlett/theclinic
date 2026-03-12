/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.controller.SystemDefinition;
import java.awt.Point;
import java.util.ArrayList;
import theclinic.controller.SystemDefinition.Properties;
import theclinic.model.repository.StoreException;
import theclinic.model.repository.LoginException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;


/**
 *
 * @author colin
 */
public class User extends Entity implements IEntityRepositoryActions{

    public User(String userName){
        username = userName;
        setIsUser(true);
    }
    
    public User(){
        setIsUser(true);
    }
    
    
    private ArrayList<User> collection = new ArrayList<>();
    public ArrayList<User> get(){
        return collection;
    }
    public void set(ArrayList<User> value){
        collection = value;
    }
    
    private String password = null;
    public void setPassword(String value){
        password = value;
    }
    public String getPassword(){
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
    
    private byte[] hashPasswordBytes = null;
    private void setHashPasswordBytes(byte[] value){
        hashPasswordBytes = value;
    }
    
    @Override
    public Integer insert()throws StoreException{
        Integer value = null;
        value = getRepository().insert(this);
        setKey(value);
        return getKey();
    }
    
    @Override
    public User read()throws StoreException{
        return getRepository().read(this);
    }

    @Override 
    public void update()throws StoreException{
        getRepository().update(this);
    }
    
}
