/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colinhewlettsolutions.client.model.entity;

import colinhewlettsolutions.client.model.repository.StoreException;//01/03/2023
import java.awt.Point;
import colinhewlettsolutions.client.model.entity.interfaces.IStoreClient;

/**
 *
 * @author colin
 */
public class Entity implements IStoreClient{
    private int test = 0;
    private String systemDefinitionProperty = null;
    private Boolean isAppointment = false;
    private Boolean isAppointmentDate = false;
    private Boolean isPatient = false;
    private Boolean isPatientNotification = false;
    private Boolean isTableRowValue = false;
    private Boolean isPMSStore = false;
    private Boolean isSurgeryDaysAssignment = false;
    private Boolean isPatientNote = false;
    private Boolean isPrimaryCondition = false;
    private Boolean isSecondaryCondition = false;
    private Boolean isMedication = false;
    private Boolean isDoctor = false;
    private Boolean isClinicNote = false;
    private Boolean isTreatment = false;
    private Boolean isAppointmentTreatment = false;
    private Boolean isPatientPrimaryCondition = false;
    private Boolean isPatientSecondaryCondition = false;
    private Boolean isPatientQuestion = false;
    private Boolean isQuestion = false;
    private Boolean isInvoice = false;
    private Boolean isTreatmentCost = false;
    private Boolean isUser = false;
    private Boolean isToDo = false;
    private Boolean isPatientAppointmentData = false;
    private Boolean isArchivedPatient = false;
    private Boolean isTransmission = false;
    
    private Scope scope = null;
    private Point value = null;
    protected Boolean isDeleted = false;
    protected Boolean isCancelled = false;
    protected Boolean isUserSettings = false;
    
    public enum Properties{
        DATABASE_TYPE,
        DATABASE_URL
    }
    
    private static Repository repository = null;
    protected Repository getRepository(){
        return repository;
    }
    public static void setRepository(Repository value){
        repository = value;
    }
    
    public Boolean getIsCancelled(){
        return isCancelled;
    }
    
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    
    public void setIsCancelled(Boolean value){
        isCancelled = value;
    }
    
    public void setIsDeleted(Boolean value){
        isDeleted = value;
    }
    
    private Integer key = null;
    public Integer getKey(){
        return key;
    }
    void setKey(Integer value){
        this.key = value;
        /**
         * code update 6/12/2025
         * -- key value is null or not zero (is never zero)
         */
        if (key == null) {
            setIsKeyDefined(false);
        }else{
            if (key == 0) {
                key = null;
                setIsKeyDefined(false);
            }else setIsKeyDefined(true);
        }

        /*
        if (key!=null)
            if (key!=0) setIsKeyDefined(true);
            else setIsKeyDefined(false);
        else setIsKeyDefined(false); 
        */
    }
    
    private void resetAll(){
        setIsAppointment(false);
        setIsPatient(false);
        setIsPatientNotification(false);
        setIsPMSStore(false);
        setIsTableRowValue(false); 
        setIsSurgeryDaysAssignment(false);
        setIsPatientNote(false);
        setIsPrimaryCondition(false);
        setIsSecondaryCondition(false);
        setIsMedication(false);
        setIsDoctor(false);
        setIsClinicNote(false);
        setIsTreatment(false);
        setIsAppointmentTreatment(false);
        setIsPatientPrimaryCondition(false);
        setIsPatientSecondaryCondition(false);
        setIsPatientQuestion(false);
        setIsQuestion(false);
        setIsInvoice(false);
        setIsTreatmentCost(false);
        setIsUser(false);
        setIsToDo(false);
        setIsPatientAppointmentData(false);
        setIsArchivedPatient(false);
        setIsUserSettings(false);
        setIsTransmission(false);
    }
    
    /**
     * defines the scope of the Entity.read() operation
     */
    public enum Scope { ALL,
                        APPOINTEE_REMINDERS,    //all entities
                        ARCHIVED,
                        APPOINTMENTS_WITH_TREATMENTS_COUNT, //used on comment migration
                        CONSULTATION_NOT_PAID,   //used on comment migration
                        PAID_195_POUNDS,        //used on comment migration
                        TRAINING_TO_UNBOOKABLE, //used on comment migration
                        ADMIN_TO_UNBOOKABLE,    //used on comment migration
                        LABWORK_CHECK,          //used on comment migration
                        LABWORK_RECEIVED,       //used on comment migration
                        CHECK_FOR_LABWORK,       //used on comment migration
                        BANK_HOLIDAY_TO_UNBOOKABLE, //used on comment migration
                        BLANK_COMMENT,          //used on comment migration
                        CANCELLED,              //cancelled appointments
                        COPY_NOTES_TO_COMMENT,  //used on comment migration
                        EMERGENCY,              //on an appointment deletion to make deletion permanent
                        PENDING,                //all pending transmission requests
                        PENDING_FOR_PATIENT,    //pending transmission for patient
                        BY_PATIENT,                 //PatientAppointmentData ordered by patient surname
                        BY_RECALL_DATE,             //PatientAppointmentData ordered by recall date
                        FOR_APPOINTMENT,        //clinic note or treatment for appointment
                        FOR_TREATMENT,          //appointment for treatment
                        FOR_QUESTION,
                        FOR_CATEGORY,           //questions in this category
                        FOR_INVOICE,
                        FOR_DAY,                //appointments for this day
                        FOR_PATIENT,            //appointments/notes for this patient
                        FOR_PRIMARY_CONDITION,  //secondary condition(s) for this primary condition
                        FOR_SECONDARY_CONDITION,
                        FOR_DAY_AND_NON_EMERGENCY_APPOINTMENT,
                        FOR_DAY_AND_EMERGENCY_APPOINTMENT,
                        DELETED_FOR_PATIENT,    //deleted appointments for this patient (when a deleted patient is being recovered)         
                        DELETED,                //deleted patients
                        FROM_DAY,               //appointmens from this day
                        PATIENT_APPOINTMENT_DATA_WITH_APPOINTMENT,
                        PATIENT_APPOINTMENT_DATA_WITHOUT_APPOINTMENT, //patient appointment data for patient with no appointments
                        SINGLE,                 // this entity only
                        UNACTIONED,             //unactioned patient notifications
                        USER_SCHEDULE_DIARY_SETTINGS,
                        USER_SCHEDULE_LIST_SETTINGS,
                        USER_SYSTEM_WIDE_SETTINGS,
                        WITH_NAME,              //for duplicated username check (ADD_NEW_CREDENTIAL)
                                                //to check if specified username exists (LOGIN_REQUEST)
                        WITH_CREDENTIAL         //when both username and password need to be correct (LOGIN_REQUEST)
                        }
    
    public static void createPMSDatabase()throws StoreException{
        /*
        IStoreActions store = Store.FACTORY();
        
        store.create();
*/
    }

    public void setSystemDefinitionProperty(String value){
        systemDefinitionProperty = value;
    }
    public String getSystemDefinitionProperty(){
        return systemDefinitionProperty;
    }
    
    
    public Point getValue(){
        return value;
    }
    
    public void setValue(Point v){
        value = v;
    }
    
    public Scope getScope(){
        return scope;
    }
    
    public void setScope(Scope value){
        scope = value;
    }
    
    
    
    private Boolean isKeyDefined = false;
    public Boolean getIsKeyDefined(){
        return isKeyDefined;
    }
    public void setIsKeyDefined(Boolean value){
        isKeyDefined = value;
    }

    
    public Boolean getIsAppointment(){
        return isAppointment;
    }
    public Boolean getIsAppointmentDate(){
        return isAppointmentDate;
    }

    public Boolean getIsPatient(){
        return isPatient;
    }
    public Boolean getIsPatientNotification(){
        return isPatientNotification;
    }

    public final Boolean getIsTableRowValue(){
        return isTableRowValue;
    }
    public final Boolean getIsPMSStore(){
        return isPMSStore;
    }
    public Boolean getIsSurgeryDaysAssignment(){
        return isSurgeryDaysAssignment;
    } 
    
    public Boolean getIsPatientNote(){
        return isPatientNote;
    }
    
    public Boolean getIsPrimaryCondition(){
        return isPrimaryCondition;
    }
    
    public Boolean getIsSecondaryCondition(){
        return isSecondaryCondition;
    }
    
    public Boolean getIsMedication(){
        return isMedication;
    }
    
    public Boolean getIsDoctor(){
        return isDoctor;
    }
    
    public Boolean getIsClinicNote(){
        return isClinicNote;
    }
    
    public Boolean getIsTreatment(){
        return isTreatment;
    }
    
    public Boolean getIsAppointmentTreatment(){
        return isAppointmentTreatment;
    }
    
    public Boolean getIsPatientPrimaryCondition(){
        return isPatientPrimaryCondition;
    }
    
    public Boolean getIsPatientSecondaryCondition(){
        return isPatientSecondaryCondition;
    }
    
    public Boolean getIsTransmission(){
        return isTransmission;
    }
    
    protected void setIsTransmission(Boolean value){
        if (value) resetAll();
        isTransmission= value;
    }
    
    protected void setIsPatientSecondaryCondition(Boolean value){
        if (value) resetAll();
        isPatientSecondaryCondition= value;
    }
    
    public Boolean getIsPatientQuestion(){
        return isPatientQuestion;
    }
    
    public Boolean getIsQuestion(){
        return isQuestion;
    }
    
    public Boolean getIsInvoice(){
        return isInvoice;
    }
    
    public Boolean getIsTreatmentCost(){
        return isTreatmentCost;
    }
    
    public Boolean getIsUser(){
        return isUser;
    }
    
    public Boolean getIsToDo(){
        return isToDo;
    }
    
    public Boolean getIsPatientAppointmentData (){
        return isPatientAppointmentData ;
    }
    
    public Boolean getIsArchivedPatient (){
        return isArchivedPatient ;
    }
    
    public Boolean getIsUserSettings (){
        return isUserSettings ;
    }
    
    protected void setIsUserSettings (Boolean value){
        if (value) resetAll();
        isUserSettings = value;
    }
    
    protected void setIsArchivedPatient (Boolean value){
        if (value) resetAll();
        isArchivedPatient = value;
    }
    
    protected void setIsPatientAppointmentData (Boolean value){
        if (value) resetAll();
        isPatientAppointmentData = value;
    }
    
    protected void setIsToDo(Boolean value){
        if (value) resetAll();
        isToDo= value;
    }
    
    protected void setIsUser(Boolean value){
        if (value) resetAll();
        isUser= value;
    }
    
    protected void setIsTreatmentCost(Boolean value){
        if (value) resetAll();
        isTreatmentCost= value;
    }
    
    protected void setIsInvoice(Boolean value){
        if (value) resetAll();
        isInvoice= value;
    }
    
    protected void setIsQuestion(Boolean value){
        if (value) resetAll();
        isQuestion= value;
    }
  
    protected void setIsPatientQuestion(Boolean value){
        if (value) resetAll();
        isPatientQuestion= value;
    }
    
    protected void setIsPatientPrimaryCondition(Boolean value){
        if (value) resetAll();
        isPatientPrimaryCondition= value;
    }
    
    protected void setIsAppointmentTreatment(Boolean value){
        if (value) resetAll();
        isAppointmentTreatment= value;
    }
    
    protected void setIsTreatment(Boolean value){
        if (value) resetAll();
        isTreatment= value;
    }
    
    protected void setIsClinicNote(Boolean value){
        if (value) resetAll();
        isClinicNote= value;
    }
    
    protected void setIsDoctor(Boolean value){
        if (value) resetAll();
        isDoctor= value;
    }
    
    protected void setIsMedication(Boolean value){
        if (value) resetAll();
        isMedication= value;
    }
    public void setIsSecondaryCondition(Boolean value){
        if (value) resetAll();
        isSecondaryCondition= value;
    }
    public void setIsPrimaryCondition(Boolean value){
        if (value) resetAll();
        isPrimaryCondition= value;
    }
    protected void setIsAppointment(Boolean value){
        if (value) resetAll();
        isAppointment= value;
    }
    protected void setIsAppointmentDate(Boolean value){
        if (value) resetAll();
        isAppointmentDate = value;
    }

    protected void setIsPatient(Boolean value){
        if (value) resetAll();
        isPatient = value;
    }
    protected void setIsPatientNotification(Boolean value){
        if (value) resetAll();
        isPatientNotification = value;
    }

    protected final void setIsTableRowValue(Boolean value){
        if (value) resetAll();
        isTableRowValue = value;
    }
    protected final void setIsPMSStore(Boolean value){
        if (value) resetAll();
        isPMSStore = value;
    }
    protected void setIsSurgeryDaysAssignment(Boolean value){
        if (value) resetAll();
        isSurgeryDaysAssignment = value;
    } 
    protected void setIsPatientNote(Boolean value){
        if (value) resetAll();
        isPatientNote = value;
    }

}
