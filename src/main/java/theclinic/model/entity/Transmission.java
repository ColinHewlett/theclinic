/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.model.entity.interfaces.IEntityRepositoryActions;
import theclinic.model.repository.StoreException;
import java.awt.Point;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author colin
 */
public class Transmission extends Entity implements IEntityRepositoryActions{
    
    enum Mode {EMAIL, MAIL, SMP, NONE}
    enum Status {PENDING, TRANSMITTED, NONE}
    
    public Transmission(){
        setIsTransmission(true);
    }
    
    public Transmission(Integer key){
        setKey(key);
    }
    
    private ArrayList<Transmission> collection = new ArrayList<>();
    public ArrayList<Transmission> get(){
        return collection;
    }
    public void set(ArrayList<Transmission> value){
        collection = value;
    }
    
    private Patient patient = null;
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient value) {
        this.patient = value;
    }
    
    private Mode mode = null;
    public Mode getMode() {
        return mode;
    }
    public void setMode(Mode value) {
        this.mode = value;
    }
    
    private Status status = null;
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status value) {
        this.status = value;
    }
    
    private LocalDate date = null;
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate value) {
        this.date = value;
    }
    
    private String content = null;
    public String getContent() {
        return content;
    }
    public void setContent(String value) {
        this.content = value;
    }
    
    /**
     * persistent storage operations
     */
    public Point count()throws StoreException{
        return getRepository().count(this);
    }
    
    public void create()throws StoreException{
        getRepository().create(this);
    }
    
    public void delete()throws StoreException{
        getRepository().delete(this);
    }
    
    public void drop()throws StoreException{
        
    }
    
    public Integer insert()throws StoreException{
        Integer value = null;
        value = getRepository().insert(this);
        setKey(value);
        return getKey();
    }
    
    public Entity read()throws StoreException{
        return getRepository().read(this);
    }
    
    public void update()throws StoreException{
        getRepository().update(this);
    }
}
