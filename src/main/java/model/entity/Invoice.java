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
public class Invoice extends Entity implements IEntityStoreActions{
    private Integer key = null;
    private Patient patient = null;
    private String description = null;
    private double amount = 0.00;

    private ArrayList<Invoice> collection = new ArrayList<>();
    
    public Invoice(){
        setIsInvoice(true);
    }
    
    public Invoice(Integer key){
        this.key = key;
        setIsInvoice(true);
    }
    
    public Invoice(Patient patient){
        this.patient = patient;
        setIsInvoice(true);
    }
    
    public ArrayList<Invoice> get(){
        return collection;
    }
    public void set(ArrayList<Invoice> collection){
        this.collection = collection;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String value) {
        this.description = value;
    }
    
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient value) {
        this.patient = value;
    }
    
    public double getAmount() {
        return amount;
    }
    public void setAmount(double value) {
        this.amount = value;
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
    public void drop(){
        
    }

    @Override
    public Integer insert() throws StoreException{
        Integer value = null;
        value = new Repository().insert(this);
        setKey(value);
        return value;
    }
    
    
    
    /**
     * 
     * @return Invoice which is initialised with collection of medications and patient parent
     * @throws StoreException 
     */
    @Override
    public Invoice read()throws StoreException{
        Invoice m = new Repository().read(this);
        return m;
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
