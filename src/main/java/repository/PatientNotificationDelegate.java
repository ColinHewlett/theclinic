/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.repository;

import clinicpms.model.Notification;
//import colinhewlettsolutions.clinicpmswithmaven.model.Patient;
/**
 *
 * @author colin
 */
public class PatientNotificationDelegate extends Notification {
    
    protected PatientNotificationDelegate(){
        super();
    }
    
    protected PatientNotificationDelegate(Notification pn){
        super.setNotificationDate(pn.getNotificationDate());
        super.setNotificationText(pn.getNotificationText());
        super.setPatient(pn.getPatient());
        super.setIsActioned(pn.getIsActioned());
        super.setIsDeleted(pn.getIsDeleted());
        super.setIsCancelled(pn.getIsCancelled());
        super.set(pn.get());
        super.setScope(pn.getScope());
    }
    
    protected void setKey(Integer key){
        super.setKey(key);
    }
    
    protected Integer getKey(){
        return super.getKey();
    }
}
