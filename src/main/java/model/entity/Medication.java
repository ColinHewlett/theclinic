/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import model.entity.Entity;
import repository.StoreException;
import repository.Repository;
import java.awt.Point;
import java.util.ArrayList;
import model.IEntityStoreActions;


/**
 * 
 * @author colin
 */
public class Medication extends Entity implements IEntityStoreActions{
    private Integer patientKey = null;
    private Patient patient = null;
    private String description = null;
    private String notes = null;
    private ArrayList<Medication> collection = new ArrayList<>();
    
    public Medication(){
        setIsMedication(true);
    }
    
    public Medication(Integer key){
        setKey(key);
        setIsMedication(true);
    }
    
    public Medication(Patient patient){
        this.patient = patient;
        setIsMedication(true);
    }

    public Integer getPatientKey() {
        return patientKey;
    }

    public void setPatientKey(Integer key) {
        this.patientKey = key;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String value) {
        this.notes = value;
    }
    
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient value) {
        this.patient = value;
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
    
    public ArrayList<Medication> get(){
        return collection;
    }
    public void set(ArrayList<Medication> collection){
        this.collection = collection;
    }
    
    /**
     * 
     * @return Medication which is initialised with collection of medications and patient parent
     * @throws StoreException 
     */
    @Override
    public Medication read()throws StoreException{
        Medication m = new Repository().read(this);
        return m;
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
