/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

//<editor-fold defaultstate="collapsed" desc="Imports">
import _system_environment_variables.SystemDefinitions;

import repository.Repository;
import repository.StoreException;//01/03/2023
import java.awt.Point;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//</editor-fold>

/**
 *
 * @author colin
 */
public class PatientNote extends Entity implements IEntityStoreActions{
    private LocalDateTime datestamp = null;
    private Integer patientKey = null;
    private String note = null;
    private Boolean isDeleted = null;
    private Patient patient = null;
    private ArrayList<PatientNote> collection = null;
    
    private Boolean checkIfPatientExistsWithKey(Integer key){
        return null;
    }
    
    public ArrayList<PatientNote> get(){
        ArrayList<PatientNote> patientNotes = new ArrayList<>();
        for(PatientNote patientNote : collection){
            patientNotes.add(patientNote);  
        }
        return patientNotes;
    }

    public void set(ArrayList<PatientNote> value){
        collection = value;
    }
    
    public PatientNote(LocalDateTime datestamp, int patientKey)throws StoreException{
        this.datestamp = datestamp;
        this.patientKey = patientKey;
        super.setIsPatientNote(true);
    }
    
    public PatientNote(LocalDateTime datestamp, Patient patient){
        this.datestamp = datestamp;
        this.patient = patient;
        this.patientKey = this.patient.getKey();
        super.setIsPatientNote(true);
    }
    
    public PatientNote(Patient patient){
        super.setIsPatientNote(true);
        this.patientKey = patient.getKey();
        this.patient = patient;
    }
    
    public PatientNote(){
        super.setIsPatientNote(true);
    }
    
    public void setDatestamp(LocalDateTime value){
        this.datestamp = value;
    }
    
    public LocalDateTime getDatestamp(){
        return datestamp;
    }
    
    /**
     * 
     * @param value; Integer representing patient pid
     * @throws StoreException if a patient with this pid cannot be found
     */
    public void setPatientKey(Integer value)throws StoreException{
        this.patientKey = value;
    }
    
    public Integer getPatientKey(){
        return patientKey;
    }
    
    public void setPatient(Patient value)throws StoreException{
        patient = value;
    }
    
    public Patient getPatient(){
        return patient;
    }
    
    public void setNote(String value){
        note = value;
    }
    
    public String getNote(){
        return note;
    }
    
    public void setIsDeleted(Boolean value){
        isDeleted = value;
    }
    
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    
    /**
     * Counts the number of patient notes stored on the system; 
     * which depends on the current setting of the object's scope setting; 
     * (all notes, or just those for a specific patient)
     * @return Integer, total number of the requested notes 
     * @throws StoreException 
     */
    @Override
    public Point count()throws StoreException{
        return new Repository().count(this);
    }
    
    /**
     * Creates a new PatientNote table in persistent store
     * @throws StoreException 
     */
    @Override
    public void create() throws StoreException{
        new Repository().create(this);
    }
    
    /**
     * Method updates this note's isDeleted property to true
     * @throws StoreException 
     */
    @Override
    public void delete() throws StoreException{
        new Repository().delete(this, getDatestamp(), getPatientKey());
    }
    
    /**
     * Not currently implemented
     * @throws StoreException 
     */
    @Override
    public void drop() throws StoreException{
        
    }
    
    /**
     * method assumes Repository will check compound pid (datestamp & patient key) exists for this PatientNote
     * @throws StoreException 
     */
    @Override
    public void insert() throws StoreException{
        new Repository().insert(this);
    }
    
    /**
     * scope of entity fetch from store is specified on entry; thus
     * -- SINGLE scope
     * ---- fetches this note for a particular patient from persistent store
     * ---- fields in the returned note for tha patient are uninitialised except for the key field
     * -- FOR_PATIENT scope
     * ---- fetches from persistent store all notes for this patient
     * 
     * @return Note(s)
     * @throws StoreException 
     */
    @Override
    public PatientNote read() throws StoreException{
        Iterator it;
        Patient p;
        PatientNote patientNote = null;  
        switch (getScope()){
            case SINGLE:
                patientNote = new Repository().read(this);
                if (this.getPatient()==null){
                    p = new Patient(patientNote.getPatientKey());
                    p.setScope(Scope.SINGLE);
                    patientNote.setPatient(p.read());
                }else{
                    throw new StoreException(
                            "Patient owner of this patient note is undefined.",
                            StoreException.ExceptionType.NULL_KEY_EXCEPTION);  
                }
                break;
            case DELETED_FOR_PATIENT:
              
                break;
            case FOR_PATIENT:
                this.set((new Repository().read(this)).get());
                it = this.get().iterator();
                while(it.hasNext()){
                    patientNote = (PatientNote)it.next();
                    Patient thePatient = new Patient(patientNote.getPatientKey());
                    thePatient.setScope(Scope.SINGLE);
                    patientNote.setPatient(thePatient.read());
                }
                break;
            default:
                break;       
        }
        return patientNote;
    }
    
    @Override
    public void update()throws StoreException{
        new Repository().update(this);
    }
            
    
}
