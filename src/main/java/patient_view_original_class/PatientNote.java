/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patient_view_original_class;

import model.*;
//<editor-fold defaultstate="collapsed" desc="Imports">

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

    private Boolean checkIfPatientExistsWithKey(Integer key){
        return null;
    }
    
    private ArrayList<PatientNote> collection = new ArrayList<>();;
    public ArrayList<PatientNote> get(){  
        return collection;
    }

    public void set(ArrayList<PatientNote> value){
        collection = value;
    }
    
    
    public PatientNote(Integer key){
        this.key = key;
        super.setIsPatientNote(true);
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
    
    private Integer key = null;
    protected void setKey(Integer value){
        this.key = value;
    }
    protected Integer getKey(){
        return key;
    }
    
    private LocalDateTime datestamp = null;
    public void setDatestamp(LocalDateTime value){
        this.datestamp = value;
    }
    public LocalDateTime getDatestamp(){
        return datestamp;
    }
    
    private Integer patientKey = null;
    public void setPatientKey(Integer value){
        this.patientKey = value;
    }
    public Integer getPatientKey(){
        return patientKey;
    }
    
    private Patient patient = null;
    public void setPatient(Patient value){
        setPatientKey(value.getKey());
        patient = value;
    }
    public Patient getPatient(){
        return patient;
    }
    
    private String note = null;
    public void setNote(String value){
        note = value;
    }
    public String getNote(){
        return note;
    }
    
    private Boolean isDeleted = null;
    public void setIsDeleted(Boolean value){
        isDeleted = value;
    }
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    
    private LocalDateTime lastUpdated = null;
    public LocalDateTime getLastUpdated(){
        return lastUpdated;
    }
    public void setLastUpdated(LocalDateTime value){
        lastUpdated = value;
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
        /*28/03/2024return new Repository().count(this);*/
        return null;
    }
    
    /**
     * Creates a new PatientNote table in persistent store
     * @throws StoreException 
     */
    @Override
    public void create() throws StoreException{
        /*28/03/2024new Repository().create(this);*/

    }
    
    /**
     * Method updates this note's isDeleted property to true
     * @throws StoreException 
     */
    @Override
    public void delete() throws StoreException{
        /*28/03/2024new Repository().delete(this, getDatestamp(), getPatientKey());*/
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
    public Integer insert() throws StoreException{
        /*28/03/2024setKey(new Repository().insert(this));*/
        return getKey();
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
                /*28/03/2024patientNote = new Repository().read(this, getKey());*/
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
                /*28/03/2024this.set((new Repository().read(this, null)).get());*/
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
    
    public void recover()throws StoreException{
        this.setScope(Scope.DELETED);
        /*28/03/2024new Repository().recover(this,getKey());*/
    }
    
    @Override
    public void update()throws StoreException{
        /*28/03/2024new Repository().update(this, getKey());*/
    }
    /**
     * Part of data migration facility; refer to DesktopVC main() method for its invocation
     * @throws StoreException 
     */
    public void createNotesFromAppointmentTable()throws StoreException{
        Appointment appointment = new Appointment();
        appointment.setScope(Scope.ALL);
        appointment.read();
        Iterator<Appointment> it = ((Appointment)appointment).get().iterator();
            while (it.hasNext()){
                Appointment a = it.next();
                //PatientDelegate delegate = new PatientDelegate(a.getPatient());
                PatientNote patientNote = new PatientNote(a.getPatient());
                patientNote.setDatestamp(a.getStart());
                patientNote.setNote(a.getNotes());
                patientNote.setLastUpdated(LocalDateTime.now());
                /*28/03/2024patientNote.setKey(new Repository().insert(patientNote));*/
                /*28/03/2024a.setPatientNote(patientNote);*/
                a.update();
            }
    }
    
    public void createPatientNoteFromAppointment(Appointment a)throws StoreException{
        PatientNote patientNote = new PatientNote(a.getPatient());
        patientNote.setDatestamp(a.getStart());
        patientNote.setNote(a.getNotes());
        patientNote.setLastUpdated(LocalDateTime.now());
        /*28/03/2024patientNote.setKey(new Repository().insert(patientNote));*/
        /*28/03/2024a.setPatientNote(patientNote);*/
        a.update();
    }
    
}
