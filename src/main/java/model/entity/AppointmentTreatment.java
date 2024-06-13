/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import model.entity.Appointment;
import repository.StoreException;
import repository.Repository;
import java.awt.Point;
import java.util.ArrayList;
import model.IEntityStoreActions;

/**
 *
 * @author colin
 */
public class AppointmentTreatment extends Entity implements IEntityStoreActions {
    
    public AppointmentTreatment(){
        this.setIsAppointmentTreatment(true);
    }
    
    public AppointmentTreatment(Appointment appointment){
        this.appointment = appointment;
        this.setIsAppointmentTreatment(true);
    }
    
    public AppointmentTreatment(Appointment appointment, Treatment treatment){
        this.appointment = appointment;
        this.treatment = treatment;
        this.setIsAppointmentTreatment(true);
    }
    
    private ArrayList<AppointmentTreatment> collection = new ArrayList<>();
    public ArrayList<AppointmentTreatment> get(){
        return collection;
    }
    public void set(ArrayList<AppointmentTreatment> value){
        collection = value;
    }
    
    private Appointment appointment = null;
    public Appointment getAppointment(){
        return appointment;
    }
    public void setAppointment(Appointment value){
        appointment = value;
    }
    
    private Treatment treatment = null;
    public Treatment getTreatment(){
        return treatment;
    }
    public void setTreatment(Treatment value){
        treatment = value;
    }
    
    private String comment = null;
    public String getComment(){
        return comment;
    }
    public void setComment(String value){
        comment = value;
    }
    
    @Override
    public Point count() throws StoreException{
        return new Repository().count(this);
    }
    
    @Override
    public void create() throws StoreException{
        new Repository().create(this);
    }
    
    @Override
    public void delete() throws StoreException{
        new Repository().delete(this);
    }
    
    @Override
    public void drop() throws StoreException{

    }
    
    @Override
    public Integer insert() throws StoreException{
        return new Repository().insert(this);
    }
    
    @Override
    public AppointmentTreatment read() throws StoreException{
        set(new Repository().read(this).get());
        return this;
    }
    
    @Override
    public void update() throws StoreException{
        new Repository().update(this);
    }
}
