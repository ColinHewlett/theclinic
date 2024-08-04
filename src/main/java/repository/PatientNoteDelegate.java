/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import patient_view_original_class.PatientNote;
/**
 *
 * @author colin
 */
public class PatientNoteDelegate extends PatientNote{
    
    protected PatientNoteDelegate(){
        super();
    }
    
    protected PatientNoteDelegate(PatientNote pn){
        super.setDatestamp(pn.getDatestamp());
        super.setNote(pn.getNote());
        super.setPatient(pn.getPatient());
        super.setPatientKey(pn.getPatientKey());
        super.setIsDeleted(pn.getIsDeleted());
        super.set(pn.get());
        super.setScope(pn.getScope());
        super.setLastUpdated(pn.getLastUpdated());
    }

}
