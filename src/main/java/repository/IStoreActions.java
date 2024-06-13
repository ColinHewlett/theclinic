/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import model.entity.SecondaryCondition;
import model.entity.PrimaryCondition;
import model.entity.Treatment;
import model.entity.SurgeryDaysAssignment;
import model.entity.Question;
import model.entity.PatientQuestion;
import model.entity.PatientCondition;
import model.entity.Patient;
import model.entity.Notification;
import model.entity.Medication;
import model.entity.Entity;
import model.entity.Doctor;
import model.entity.ClinicalNote;
import model.entity.AppointmentTreatment;
import model.entity.Appointment;
import model.*;
import java.awt.Point;
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
    public Point count(Doctor doctor)throws StoreException;
    public Point count(Medication medication)throws StoreException;
    public Point count(ClinicalNote clinicNote)throws StoreException;
    public Point count(Treatment treatment)throws StoreException;
    public Point count(AppointmentTreatment appointmentTreatment)throws StoreException;
    public Point count(Question treatment)throws StoreException;
    public Point count(PatientQuestion appointmentQuestion)throws StoreException;
    public Point count(PatientCondition patientCondition)throws StoreException;
    
    public void create(Appointment a) throws StoreException;
    public void create(Notification pn) throws StoreException;
    public void create(Patient p )throws StoreException;
    public void create(SurgeryDaysAssignment s)throws StoreException;
    /*28/03/2024public void create(PatientNote p)throws StoreException;*/
    public void create(Doctor doctor)throws StoreException;
    public void create(Medication medication)throws StoreException;
    public void create(ClinicalNote clinicNote)throws StoreException;
    public void create(PrimaryCondition pc)throws StoreException;
    public void create(SecondaryCondition sc)throws StoreException;
    public void create(Treatment treatment)throws StoreException;
    public void create(AppointmentTreatment appointmentTreatment)throws StoreException;
    public void create(Question treatment)throws StoreException;
    public void create(PatientQuestion appointmentQuestion)throws StoreException;
    public void create(PatientCondition patientCondition)throws StoreException;
    
    public void delete(Appointment a, Integer key) throws StoreException;
    public void delete(Patient p, Integer patientKey) throws StoreException;
    public void delete(Notification pn, Integer key)throws StoreException;
    /*28/03/2024public void delete(PatientNote p, LocalDateTime datestamp, Integer patientKey)throws StoreException;*/
    public void delete(PrimaryCondition pc)throws StoreException;
    public void delete(SecondaryCondition sc)throws StoreException;
    public void delete(Doctor doctor)throws StoreException;
    public void delete(Medication medication)throws StoreException;
    public void delete(ClinicalNote clinicNote)throws StoreException;
    public void delete(Treatment treatment)throws StoreException;
    public void delete(AppointmentTreatment appointmentTreatment)throws StoreException;
    public void delete(Question treatment)throws StoreException;
    public void delete(PatientQuestion appointmentQuestion)throws StoreException;
    public void delete(PatientCondition patientCondition)throws StoreException;
    
    public List<String[]> importEntityFromCSV(Entity entity) throws StoreException;
    
    public Integer insert(Appointment a, Integer appointeeKey, Integer patientNoteKey) throws StoreException; 
    public Integer insert(Patient p, Integer patientKey, Integer guardianKey) throws StoreException;
    public Integer insert(Notification pn, Integer patientKey) throws StoreException;
    public Integer insert(SurgeryDaysAssignment p) throws StoreException;
    /*28/03/2024public Integer insert(PatientNote pn) throws StoreException;*/
    public Integer insert(Doctor doctor)throws StoreException;
    public Integer insert(Medication medication)throws StoreException;
    public Integer insert(PrimaryCondition pc)throws StoreException;
    public Integer insert(SecondaryCondition sc)throws StoreException;
    public Integer insert(ClinicalNote clinicNote)throws StoreException;
    public Integer insert(Treatment treatment)throws StoreException;
    public Integer insert(AppointmentTreatment appointmentTreatment)throws StoreException;
    public Integer insert(Question treatment)throws StoreException;
    public Integer insert(PatientQuestion appointmentQuestion)throws StoreException;
    public Integer insert(PatientCondition patientCondition)throws StoreException;
    
    //public void populate(SurgeryDaysAssignment data)throws StoreException;
    
    public Appointment read(Appointment a, Integer key)throws StoreException ;
    public Patient read(Patient p, Integer key) throws StoreException;
    public Notification read(Notification value, Integer key)throws StoreException;
    /*28/03/2024public PatientNote read(PatientNote pn, Integer key)throws StoreException;*/
    public SurgeryDaysAssignment read(SurgeryDaysAssignment value) throws StoreException;
    public Doctor read(Doctor doctor)throws StoreException;
    public Medication read(Medication medication)throws StoreException;
    public PrimaryCondition read(PrimaryCondition pc)throws StoreException;
    public SecondaryCondition read(SecondaryCondition sc)throws StoreException;
    public ClinicalNote read(ClinicalNote clinicNote)throws StoreException;
    public Treatment read(Treatment treatment)throws StoreException;
    public AppointmentTreatment read(AppointmentTreatment appointmentTreatment)throws StoreException;
    public Question read(Question treatment)throws StoreException;
    public PatientQuestion read(PatientQuestion appointmentQuestion)throws StoreException;
    //public PatientPrimaryCondition read(PatientPrimaryCondition patientPrimaryCondition)throws StoreException;
    //public PatientSecondaryCondition read(PatientSecondaryCondition patientSecondaryCondition)throws StoreException;
    PatientCondition read(PatientCondition patientCondition)throws StoreException;
    
    public void recover(Appointment a, Integer key) throws StoreException;
    public void recover(Patient p, Integer key) throws StoreException;
    public void recover(Notification p, Integer key) throws StoreException;

    public void update(Appointment a, Integer key, Integer appointeeKee/*28/03/2024, Integer patientNoteKey*/) throws StoreException;
    public void update(SurgeryDaysAssignment value) throws StoreException;
    public void update(Patient p, Integer key, Integer guardianKey) throws StoreException;
    public void update(Notification pn, Integer key, Integer patientKey)throws StoreException;
    /*28/03/2024public void update(PatientNote pn, Integer patientNoteKey)throws StoreException;*/
    public void update(Doctor doctor)throws StoreException;
    public void update(Medication medication)throws StoreException;
    public void update(PrimaryCondition pc)throws StoreException;
    public void update(SecondaryCondition sc)throws StoreException;
    public void update(ClinicalNote clinicNote)throws StoreException;
    public void update(Treatment treatment)throws StoreException;
    public void update(AppointmentTreatment appointmentTreatment)throws StoreException;
    public void update(Question treatment)throws StoreException;
    public void update(PatientQuestion appointmentQuestion)throws StoreException;
    public void update(PatientCondition patientCondition) throws StoreException;
}
