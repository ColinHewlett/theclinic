/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import model.Appointment;
import model.Entity;
import model.Notification;
import model.Patient;
import model.PatientNote;
import model.SurgeryDaysAssignment;
import java.awt.Point;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author colin
 */
public interface IStoreActions {  
    public Point count(Appointment appointment, Integer appointeeKey)throws StoreException;
    public Point count(Patient patient)throws StoreException;
    public Point count(Notification patientNotification)throws StoreException;
    public Point count(SurgeryDaysAssignment surgeryDaysAssignment)throws StoreException;
    
    public void create(Appointment a) throws StoreException;
    public void create(Notification pn) throws StoreException;
    public void create(Patient p )throws StoreException;
    public void create(SurgeryDaysAssignment s)throws StoreException;
    public void create(PatientNote p)throws StoreException;
    
    public void delete(Appointment a, Integer key) throws StoreException;
    public void delete(Patient p, Integer patientKey) throws StoreException;
    public void delete(Notification pn, Integer key)throws StoreException;
    public void delete(PatientNote p, LocalDateTime datestamp, Integer patientKey)throws StoreException;
    
    public List<String[]> importEntityFromCSV(Entity entity) throws StoreException;
    
    public Integer insert(Appointment a, Integer appointeeKey) throws StoreException; 
    public Integer insert(Patient p, Integer patientKey, Integer guardianKey) throws StoreException;
    public Integer insert(Notification pn, Integer patientKey) throws StoreException;
    public Integer insert(SurgeryDaysAssignment p) throws StoreException;
    public Integer insert(PatientNote pn) throws StoreException;
    
    //public void populate(SurgeryDaysAssignment data)throws StoreException;
    
    public Appointment read(Appointment a, Integer key)throws StoreException ;
    public Patient read(Patient p, Integer key) throws StoreException;
    public Notification read(Notification value, Integer key)throws StoreException;
    public PatientNote read(PatientNote pn, Integer key)throws StoreException;
    public SurgeryDaysAssignment read(SurgeryDaysAssignment value) throws StoreException;
    
    public void recover(Appointment a, Integer key) throws StoreException;
    public void recover(Patient p, Integer key) throws StoreException;
    public void recover(Notification p, Integer key) throws StoreException;

    public void update(Appointment a, Integer key, Integer appointeeKee) throws StoreException;
    public void update(SurgeryDaysAssignment value) throws StoreException;
    public void update(Patient p, Integer key, Integer guardianKey) throws StoreException;
    public void update(Notification pn, Integer key, Integer patientKey)throws StoreException;
    public void update(PatientNote pn, Integer patientNoteKey)throws StoreException;
}
