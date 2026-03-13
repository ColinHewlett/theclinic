/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.model.entity;

//<editor-fold defaultstate="collapsed" desc="Imports">
import theclinic.model.entity.Entity;
import theclinic.model.repository.StoreException;//01/03/2023
import java.awt.Point;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;
//</editor-fold>
/**
 *
 * @author colin.hewlett.solutions@gmail.com
 */
public class Notification extends Entity implements IEntityRepositoryActions {
  
//<editor-fold defaultstate="collapsed" desc="Private and protected state">
    private Patient patient = null;
    private LocalDate date = null;
    private String notification = null;
    private Boolean isActioned = false;
    private ArrayList<Notification> collection = new ArrayList<>();
    
    

//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Public interface">
    
//<editor-fold defaultstate="collapsed" desc="Public state and non-persistent store related operations">    
    public Notification(){
        this.setIsPatientNotification(true);  
    }
    
    public Notification(int key){
        this.setIsPatientNotification(true);;
        setKey(key);
    }
    
    public ArrayList<Notification> get(){
        return collection;
    }
        
    public void set(ArrayList<Notification> value){
        collection = value;
    }
    
    public Boolean getIsActioned(){
        return isActioned;
    }
    
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    
    public LocalDate getNotificationDate(){
        return date;
    }

    public String getNotificationText(){
        return notification;
    }
    
    public Patient getPatient(){
        return patient;
    }
    
    public void setIsActioned(Boolean value){
        isActioned = value;
    }
    
    public void setIsDeleted(boolean value){
        isDeleted = value;
    }
    
    public void setNotificationDate(LocalDate value){
        date = value;
    }
    
    public void setNotificationText(String value){
        notification = value;
    }
    
    public void setPatient(Patient value){
        patient = value;
    }
    
    public void action()throws StoreException{
        setIsActioned(true);
        this.update();
    }
    

//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Persistent storage related operations">  
    
    public void cancel() throws StoreException{
        getRepository().cancel(this, getKey());
    }
    
    public void uncancel()throws StoreException{
        getRepository().uncancel(this,getKey());
    }
    
    /**
     * Counts the number of patient notifications stored on the system; which depends on the current setting of the object's scope setting (all notifications, or just unactioned ones etc)
     * @return Integer, total number of the requested notification type 
     * @throws StoreException 
     */
    @Override
    public Point count()throws StoreException{
        return getRepository().count(this);
    }
    
    /**
     * Creates a new Notification table in persistent store
     * @throws StoreException 
     */
    @Override
    public void create() throws StoreException{
        getRepository().create(this);
    }
    
    /**
     * Method updates this notification's isDeleted property to true
     * @throws StoreException 
     */
    @Override
    public void delete() throws StoreException{
        getRepository().delete(this, getKey());
    }
    
    /**
     * Not currently implemented
     * @throws StoreException 
     */
    @Override
    public void drop() throws StoreException{
        
    }
    
    /**
     * method sends message to store to insert this patient notification
     * -- the store returns the key value of the inserted notification
     * -- this is used to initialise this patient notification's key
     * -- redundant op because store initialises notification's key value anyway
     * -- but store object might not; i.e. not a contractual obligation in store to do so
     * -- whereas this way a key value us expected back from the store
     * @throws StoreException 
     */
    @Override
    public Integer insert() throws StoreException{
        Integer patientKey = null;
        patientKey = getPatient().getKey();
        setKey(getRepository().insert(this, patientKey));
        return getKey();
    }
    
    /**
     * scope of entity fetch from store is specified on entry; thus
     * -- SINGLE scope
     * ---- fetches this patient notification from persistent store
     * ---- fields in the returned notification's patient are uninitialised except for the key field
     * -- FOR_PATIENT scope
     * ---- fetches from persistent store patient notifications belonging to this patient notification
     * ---- for all other scopes, fetches all notifications consistent with the scope (typically INACTIONED)
     * 
     * @return Notification
     * @throws StoreException 
     */
    @Override
    public Notification read() throws StoreException{
        Iterator it;
        Patient p;
        Notification patientNotification = null; 
        switch (getScope()){
            case SINGLE:
                patientNotification = getRepository().read(this, getKey());
                p = new Patient(patientNotification.getPatient().getKey());
                p.setScope(Scope.SINGLE);
                patientNotification.setPatient(p.read());
                break;
            case DELETED_FOR_PATIENT:
                set(getRepository().read(this, getPatient().getKey()).get());
                break;
            case FOR_PATIENT:
                set(getRepository().read(this, getPatient().getKey()).get());
                it = get().iterator();
                while(it.hasNext()){
                    patientNotification = (Notification)it.next();
                    p = new Patient(patientNotification.getPatient().getKey());
                    patientNotification.setPatient(Notification.this.getPatient());
                    p.setScope(Scope.SINGLE);
                    patientNotification.setPatient(p.read());
                }
                break;
            default:
                set(getRepository().read(this, null).get());
                it = get().iterator();
                while(it.hasNext()){
                    patientNotification = (Notification)it.next();
                    p = new Patient(patientNotification.getPatient().getKey());
                    p.setScope(Scope.SINGLE);
                    patientNotification.setPatient(p.read());        
                }       
        }
        return patientNotification;
    }

    protected  void recover() throws StoreException{
        boolean isPatientDeleted = false;
        Patient patient = getPatient();
        if (patient != null){
                if (patient.getIsKeyDefined()) {
                    patient.setScope(Scope.DELETED);
                    patient.read();
                    for(Patient p : patient.get()){
                        if (p.equals(patient)){
                            isPatientDeleted = true;
                            break;
                        }
                    }
                }else{
                    String error = "Patient of notification requested for recovery "
                            + "has not been defined. Recovery of "
                            + "notification aborted \n"
                            + "Raised in PatientNotification.recovery()";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                if (!isPatientDeleted){
                    getRepository().recover(this, getKey());
                }else{
                    String error = "Patient of notification requested for recovery "
                            + "is deleted on the system.Recovery of "
                            + "notification is aborted \n"
                            + "Raised in PatientNotification.recovery()";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                
        }else{
            String error = "Patient of the notification requested for recovey has "
                    + "not been defined. Recovery of notification aborted.\n"
                    + "Raised in PatientNotification.recover()";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    @Override
    public void update()throws StoreException{
        getRepository().update(this, getKey(), getPatient().getKey());
    }
//</editor-fold>
    
//</editor-fold>

}
