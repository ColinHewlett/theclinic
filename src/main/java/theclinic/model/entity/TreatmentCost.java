/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import java.awt.Point;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class TreatmentCost extends Entity implements IEntityRepositoryActions{
    private Invoice invoice = null;
    private String description = null;
    private double amount = 0.00;

    private ArrayList<TreatmentCost> collection = new ArrayList<>();
    
    
    
    public TreatmentCost(){
        setIsTreatmentCost(true);
    }
    
    public TreatmentCost(Integer key){
        super.setKey(key);
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
     * @return TreatmentCost which is initialised with collection of medications and invoice parent
     * @throws StoreException 
     */
    @Override
    public TreatmentCost read()throws StoreException{
        TreatmentCost m = getRepository().read(this);
        return m;
    }
    
    @Override
    public void update() throws StoreException{
        getRepository().update(this);
    }
}
