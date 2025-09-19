/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.entity;


/**
 *
 * @author colin
 */
public class PatientSecondaryCondition extends PatientCondition{
    
    public PatientSecondaryCondition(Patient patient){
        this.patient = patient;
        this.setIsPatientSecondaryCondition(true);
    }
    
    public PatientSecondaryCondition(Patient p, SecondaryCondition sc){
        patient = p;
        condition = sc;
        setIsPatientSecondaryCondition(true);
    }
    
}
