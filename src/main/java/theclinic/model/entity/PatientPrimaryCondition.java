/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;


/**
 *
 * @author colin
 */
public class PatientPrimaryCondition extends PatientCondition  {
    
    public PatientPrimaryCondition(){
        this.setIsPatientPrimaryCondition(true);
    }
    
    public PatientPrimaryCondition(Patient patient){
        this.patient = patient;
        this.setIsPatientPrimaryCondition(true);
    }
    
    public PatientPrimaryCondition(Patient patient, PrimaryCondition primaryCondition){
        this.patient = patient;
        this.condition = primaryCondition;
        this.setIsPatientPrimaryCondition(true);
    }
    
    /*
    private ArrayList<PatientPrimaryCondition> collection = new ArrayList<>();
    public ArrayList<PatientPrimaryCondition> get(){
        return collection;
    }
    public void set(ArrayList<PatientPrimaryCondition> value){
        collection = value;
    }
    */
    
    /*
    private String comment = null;
    public String getComment(){
        return comment;
    }
    public void setComment(String value){
        comment = value;
    }*/
    /*
    private Patient patient = null;
    public Patient getPatient(){
        return patient;
    }
    public void setPatient(Patient value){
        patient = value;
    }*/
    /*
    private PrimaryCondition primaryCondition = null;
    public PrimaryCondition getPrimaryCondition(){
        return primaryCondition;
    }
    public void setPrimaryCondition(PrimaryCondition value){
        primaryCondition = value;
    }
    */
    
}
