/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import model.entity.Notification;
//import colinhewlettsolutions.clinicpmswithmaven.model.Patient;
/**
 *
 * @author colin
 */
public class NotificationDelegate extends Notification {
    int test = 0;
    
    protected NotificationDelegate(){
        super();
    }
    
    protected NotificationDelegate(Notification pn){
        super.setNotificationDate(pn.getNotificationDate());
        super.setNotificationText(pn.getNotificationText());
        super.setPatient(pn.getPatient());
        super.setIsActioned(pn.getIsActioned());
        super.setIsDeleted(pn.getIsDeleted());
        super.setIsCancelled(pn.getIsCancelled());
        super.set(pn.get());
        super.setScope(pn.getScope());
    }
    
}
