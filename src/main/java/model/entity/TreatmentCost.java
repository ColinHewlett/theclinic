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
public class TreatmentCost extends Entity implements IEntityStoreActions{
    private Integer key = null;
    private Invoice invoice = null;
    private String description = null;
    private double amount = 0.00;

    private ArrayList<TreatmentCost> collection = new ArrayList<>();
    
    public TreatmentCost(){
        setIsTreatmentCost(true);
    }
    
    public TreatmentCost(Integer key){
        this.key = key;
        setIsTreatmentCost(true);
    }
    
    public TreatmentCost(Invoice invoice){
        this.invoice = invoice;
        setIsTreatmentCost(true);
    }
    
    public ArrayList<TreatmentCost> get(){
        return collection;
    }
    public void set(ArrayList<TreatmentCost> collection){
        this.collection = collection;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String value) {
        this.description = value;
    }
    
    public Invoice getInvoice() {
        return invoice;
    }
    public void setInvoice(Invoice value) {
        this.invoice = value;
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
     * @return TreatmentCost which is initialised with collection of medications and invoice parent
     * @throws StoreException 
     */
    @Override
    public TreatmentCost read()throws StoreException{
        TreatmentCost m = new Repository().read(this);
        return m;
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
