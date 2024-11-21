/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;

import model.entity.interfaces.IEntityRepositoryActions;
import java.awt.Point;
import java.util.ArrayList;
import model.repository.StoreException;


/**
 *
 * @author colin
 */
public class PatientAppointmentData extends Entity implements IEntityRepositoryActions{
    
    public PatientAppointmentData(){
        setIsPatientAppointmentData(true);
    }
    
    
    
    private ArrayList<PatientAppointmentData> collection = null;
    public ArrayList<PatientAppointmentData> get() {
        return collection;
    }
    public void set(ArrayList<PatientAppointmentData> value) {
        this.collection = value;
    }
    
    @Override
    public Point count() throws StoreException{
        //return new Repository().count(this);
        return null;
    }
    
    @Override
    public void create()throws StoreException{
        //new Repository().create(this);
    }
    
    @Override
    public void delete()throws StoreException{
        //new Repository().delete(this);
    }
    
    @Override
    public void drop() throws StoreException{
        
    }
    
    @Override
    public PatientAppointmentData read()throws StoreException{
        PatientAppointmentData pad = new Repository().read(this);
        if (pad!=null){
            /*
            for (PatientAppointmentData _pad : pad.get()){
                _pad.setClinicalNote();
            }
            */
        }
        return pad;
    }
    
    @Override
    /**
     * @return value is always null because the insert operation replenishes the contents of the PatientAppointmentDSata table
     * In other model entities the insert operation a single row is added to the table and is identified by a unique key value which is then returned
     * Thus the syntax is made uniform for all entity insert operations
     */
    public Integer insert() throws StoreException{
        //new Repository().insert(this);
        return null;
    }
    
    public void update() throws StoreException{

    }
    
    private Appointment appointment = null;
    public Appointment getAppointment(){
        return appointment;
    }
    public void setAppointment(Appointment value){
        /**
         * received appointment has only 2 fields initialised
         * -- key
         * -- start
         */
        appointment = value;
    }
    
    private ClinicalNote clinicalNote = null;
    public ClinicalNote getClinicalNote(){
        return clinicalNote;
    }
    private void setClinicalNote()throws StoreException{
        
        clinicalNote = new ClinicalNote(getAppointment().getKey());
        clinicalNote.setScope(Scope.SINGLE);
        clinicalNote = clinicalNote.read();
    }
    
    private Patient patient = null;
    public Patient getPatient(){
        return patient;
    }
    
    public void setPatient(Patient value){
        /**
         * received patient has only the following fields initialised
         * -- key
         * -- getName().getForenames();
         * -- getName().getSurname();
         * -- getRecall().getDentalDate()
         * -- getRecall().getDentalFrequency()
         * -- getRecall().getGBTDate()
         * -- getRecall().getGBTFrequency()
         */
        patient = value;
    }
    
    
    private Integer fromYear = null;
    public Integer getFromYear(){
        return  fromYear;
    }
    public void setFromYear(Integer value){
        fromYear = value;
    }
    
    private Integer toYear = null;
    public Integer getToYear(){
        return  toYear;
    }
    public void setToYear(Integer value){
        toYear = value;
    }
    
    /*
    private Integer appointmentKey = null;
    public Integer getAppointmentKey(){
        return  appointmentKey;
    }
    public void setAppointmentKey(Integer key){
        appointmentKey = key;
    }
    */

    private Integer clinicalNoteKey = null;
    public Integer getClinicalNoteKey(){
        return  clinicalNoteKey;
    }
    public void setClinicalNoteKey(Integer key){
        clinicalNoteKey = key;
    }

    /*
    private Integer patientKey = null;
    public Integer getPatientKey(){
        return  patientKey;
    }
    public void setPatientKey(Integer key){
        patientKey = key;
    }
    */
    
    
}
