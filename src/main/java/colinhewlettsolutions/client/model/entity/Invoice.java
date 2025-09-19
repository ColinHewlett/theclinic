/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;

import java.awt.Point;
import java.util.ArrayList;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class Invoice extends Entity implements IEntityRepositoryActions{
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
    
    public Integer getKey() {
        return key;
    }
    public void setKey(Integer key) {
        this.key = key;
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
    public void drop(){
        
    }

    @Override
    public Integer insert() throws StoreException{
        Integer value = null;
        value = getRepository().insert(this);
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
        Invoice m = getRepository().read(this);
        return m;
    }
    
    @Override
    public void update() throws StoreException{
        getRepository().update(this);
    }
}
