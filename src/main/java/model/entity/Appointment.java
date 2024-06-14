/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;


//<editor-fold defaultstate="collapsed" desc="Imports">

import model.non_entity.SystemDefinition;
import repository.Repository;
import repository.StoreException;//01/03/2023
import java.awt.Point;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.IEntityStoreActions;
//</editor-fold>

/**
 *
 * @author colin.hewlett.solutions@gmail.com
 */
public class Appointment extends Entity implements IEntityStoreActions{
    
//<editor-fold defaultstate="collapsed" desc="Private and protected state">
    private Boolean isKeyDefined = false;
    private Integer key = null;
    private LocalDateTime start = null;
    private Duration duration  = null;
    private String notes = null;
    private Patient patient;
    private Boolean hasPatientBeenContacted = null;
    /*28/03/2024private PatientNote patientNote = null;*/
    private ArrayList<Appointment> collection = null;
    private static final DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * 
     * @return 
     */
    public Integer getKey() {
        return key;
    }
    
    /**
     * 
     * @param key 
     */
    protected void setKey(Integer key) {
        this.key = key;
        if (key!=null)
            if (key!=0) setIsKeyDefined(true);
            else setIsKeyDefined(false);
        else setIsKeyDefined(false);  
    }
//</editor-fold>   

//<editor-fold defaultstate="collapsed" desc="Public interface">
//<editor-fold defaultstate="collapsed" desc="Public state/behaviour, not including persistent storage related operations">
    public Appointment(){
        super.setIsAppointment(true);
    } 

    /**
     * 
     * @param key 
     */
    public Appointment( int key) {
        this.key = key;
        super.setIsAppointment(true);
    }
    public ArrayList<Appointment> get(){
        return collection;
    }
    
    public void set(ArrayList<Appointment> value){
        collection = value;
    }
    /**
     * 
     * @return 
     */
    public String getAppointeeNamePlusSlotStartTime(){
        String result = getAppointeeName();
        LocalTime start = getStart().toLocalTime();
        result = result + " which starts at " + start.format(DateTimeFormatter.ofPattern("HH:mm"));
        return result;
    }
    
    public String getAppointmentDate(){
        return getStart().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    }
    
    public boolean getIsUnbookableSlot(){
        return getPatient().getKey() == SystemDefinition.UNBOOKABLE_APPOINTMENT_SLOT;
    }
    
    
    public String getUnbookableSlotStartTime(){
        LocalTime start = getStart().toLocalTime();
        return start.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
  
    /**
     * 
     * @return 
     */
    public String getAppointeeName(){
        String title;
        String forenames;
        String surname;
        
        title = getPatient().getName().getTitle();
        forenames = getPatient().getName().getForenames();
        surname = getPatient().getName().getSurname();
        if (title.length()==0) title = "?";
        if (forenames.length() == 0) forenames = "<...>";
        if (surname.length() == 0) surname = "<...>";
       
        return title + " " + forenames + " " + surname;
    }
    
    /**
     * 
     * @return 
     */
    public LocalDateTime getSlotStartTime(){
        return this.getStart();
    }
    
    /**
     * 
     * @return 
     */
    public LocalDateTime getSlotEndTime(){
        return getStart().plusMinutes(getDuration().toMinutes());
    }
    /*
    public void setPatientNote(PatientNote value){
        patientNote = value;
    }
    public PatientNote getPatientNote(){
        return patientNote;
    }
    */
    /**
     * 
     * @return 
     */
    public LocalDateTime getStart() {
        return start;
    }
    
    /**
     * 
     * @param start 
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }
    
    /**
     * 
     * @return 
     */
    public Duration getDuration() {
        return duration;
    }
    
    /**
     * 
     * @param duration 
     */
    public void setDuration(Duration  duration) {
        this.duration = duration;
    }

    /**
     * 
     * @return 
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * 
     * @param notes 
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * 
     * @return 
     */
    public Boolean getHasPatientBeenContacted(){
        if (hasPatientBeenContacted == null) hasPatientBeenContacted = Boolean.FALSE;
        return hasPatientBeenContacted;
    }
    
    public void setHasPatientBeenContacted(Boolean value){
        hasPatientBeenContacted = value;
    }
    
    /*
    public Boolean getIsPatientNoteDefined(){
        Boolean result = null;
        
        PatientNote test = getPatientNote();
        if (test!=null){
            if (test.getKey()==0)
            result = false;
        
            
            else result = true; 
        }
        return result;
    }
    */
    /**
     * 
     * @return 
     */
    public Boolean getIsKeyDefined(){
        return isKeyDefined;
    }
    
    /**
     * 
     * @param value 
     */
    public void setIsKeyDefined(Boolean value){
        isKeyDefined = value;
    }

    /**
     * 
     * @return 
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * 
     * @param patient 
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
//</entity-fold>
    

//</editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="Persistent store related operations">
    
    public void cancel() throws StoreException{
        new Repository().cancel(this, getKey());
    }
    
    public void uncancel()throws StoreException{
        new Repository().uncancel(this,getKey());
    }
    
    /**
     * request the concrete store class for the number of specified appointment records in persistent store
     * @return Integer
     * @throws StoreException 
     */
    
    @Override
    public Point count()throws StoreException{
        Integer pid = null; 
        switch (getScope()){
            case FOR_PATIENT:
                key = getPatient().getKey();      
            
        }
        
        return new Repository().count(this,pid);

    }
    

    
    /**
     * Requests the concrete store class to create a new table for the storage of appointment records in persistent store
     * @throws StoreException 
     */
    @Override
    
    public void create()throws StoreException{
        if (SystemDefinition.getPMSDebug().equals("ENABLED"))
            new Repository().create(this);
    }

    /**
     * Save this Appointment to persistent store. 
     * -- The key of the appointment will be undefined because its a new appointment and will be automatically generated in the concrete store class
     * -- The key of the patient appointee must be specified in the call to the concrete store class
     * @throws StoreException 
     */
    @Override
    public Integer insert() throws StoreException{
        Integer pid = null;
        this.setKey(new Repository().insert(
                    this, getPatient().getKey(), null));
        return pid;
    }
    
    /**
     * This appointment's persistent image in store is deleted
     * @throws StoreException 
     */
    @Override
    public void delete() throws StoreException{
        new Repository().delete(this, getKey());
    }
    
    /**
     * Not currently implemented
     * @throws StoreException 
     */
    @Override
    public void drop() throws StoreException{
        //IStoreActions store = Store.FACTORY(this);
        //store.drop(this);        
    }
    
    /**
     * On entry assumes the scope of the read() operation is defined; the following scopes are checked for in the class
     * -- SINGLE scope specifies this appointment (key defined) is read from persistent store
     * -- FOR_PATIENT specifies the collection of appointments for this patient (not including cancelled appointment) 
     * ---- note-> an apppointment with a single field (patient key) is all that is required for this operation
     * -- DELETED_FOR_PATIENT but reads only deleted appointments associated with this patient 
     * ---- this supports the recovery of previously deleted patient where all the patient's appointments would have been deleted
     * @return -- if SINGLE read scope; Appointment object with atate initialised from the persistent store image fetched
     *         -- else this Appointment with unaltered state apart from the appointment collection fetched from persistent store
     * @throws StoreException cou;d either be
     * -- forwarded from the Registery (Store); typically triggered by an SQLException
     * -- or explicitly generated in the model Appointment class because the read appointment from store has not got a key defined
     * 
     */
    @Override
    public Appointment read() throws StoreException{
        /*28/03/2024PatientNote patientNote = null;*/
        Patient patient = null;
        Appointment result = null;
        ArrayList<Appointment> appointments = null;
        switch (getScope()){
            case FOR_PATIENT:
                try{
                    key = getPatient().getKey();
                    set(((Appointment)new Repository().read(this, key)).get());
                    
                    /*28/03/2024set(new Repository().read(this, key).get());*/
                    
                    for(Appointment a : this.get()){
                        if (a.getPatient().getIsKeyDefined()){
                            Integer key = a.getPatient().getKey();
                            patient = new Patient(key);
                            patient.setScope(Scope.SINGLE);
                            a.setPatient(patient.read());
                        }else{
                            String message = "A key has not been defined for the appointee object in the read Appointment, "
                                    + "raised in Appointment::Read(scope = " + this.getScope().toString() + ")";
                            throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                        }
                    }
                    result = this;
                }catch(NullPointerException ex){
                    String message = "The patient object in an appointment "
                            + "object fetched from store is null, which should "
                            + "never be the case. Raised in Appointment::Read(scope = " + this.getScope().toString() + ")";
                    throw new StoreException(message, StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                break;
            case DELETED_FOR_PATIENT:
                try{
                    key = getPatient().getKey();
                    /*28/03/2024 update*/
                    set(((Appointment)new Repository().read(this, key)).get());
                    
                    for(Appointment a : this.get()){
                        if (a.getPatient().getIsKeyDefined()){
                            patient = new Patient(a.getPatient().getKey());
                            patient.setScope(Scope.DELETED);
                            a.setPatient(patient.read());
                        }else{
                        String message = "A key has not been defined for the appointee object in the read Appointment, "
                                + "raised in Appointment::Read(scope = " + this.getScope().toString() + ")";
                        throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                        }
                    }
                    result = this;
                }catch(NullPointerException ex){
                    String message = "The patient object in an appointment "
                            + "object fetched from store is null, which should "
                            + "never be the case. Raised in Appointment::Read(scope = " + this.getScope().toString() + ")";
                    throw new StoreException(message, StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                break;
            case SINGLE:
                try{
                    /*28/03/2024 code update*/
                    result = (Appointment)new Repository().read(this,getKey());
                        
                    if (result.getPatient().getIsKeyDefined()){
                        patient = new Patient(result.getPatient().getKey());
                        patient.setScope(Scope.SINGLE);
                        result.setPatient(patient.read()); 
                    }else{
                        String message = "A key has not been defined for the appointee object in the read Appointment, "
                                + "raised in Appointment::Read(case SINGLE read)";
                        throw new StoreException(message, StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                    }
                    /**
                     * 29/03/2024  code logic update
                     */
                    /*
                    if (result.getPatientNote().getKey()>0){
                        patientNote = new PatientNote(result.getPatientNote().getKey());
                        patientNote.setScope(Scope.SINGLE);
                        result.setPatientNote(patientNote.read()); 
                    }
                    */

                }catch(NullPointerException ex){
                    String message = "The patient object in an appointment "
                            + "object fetched from store is null, which should "
                            + "never be the case. Raised in Appointment::Read(case SINGLE read)";
                    throw new StoreException(message, StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                break;
            case CANCELLED:
                /*28/03/2024set(new Repository().read(this, key).get());*/
                set(((Appointment)new Repository().read(this, key)).get());
                appointments = this.get();
                for(Appointment a : this.get()){
                    if (a.getPatient().getIsKeyDefined()){
                        patient = new Patient(a.getPatient().getKey());
                        patient.setScope(Scope.SINGLE);
                        a.setPatient(patient.read()); 
                    }else{
                    String message = "A key has not been defined for the appointee object in the read Appointment, "
                            + "raised in Appointment::Read(default read)";
                    throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                    }
                }
                result = this;
                break;
            case ALL:
                /*28/03/2024set(new Repository().read(this, key).get());*/
                set(((Appointment)new Repository().read(this, key)).get());
                result = this;
                break;
            default:
                try{
                    /*28/03/2024set(new Repository().read(this, key).get());*/
                    set(((Appointment)new Repository().read(this, key)).get());
                    appointments = this.get();
                    for(Appointment a : this.get()){
                        if (a.getPatient().getIsKeyDefined()){
                            patient = new Patient(a.getPatient().getKey());
                            patient.setScope(Scope.SINGLE);
                            a.setPatient(patient.read()); 
                        }else{
                        String message = "A key has not been defined for the appointee object in the read Appointment, "
                                + "raised in Appointment::Read(default read)";
                        throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                        }
                        /**
                         * 29/03/2024  code logic update
                         */
                        /*
                        if (a.getPatientNote().getKey()>0){
                            patientNote = new PatientNote(a.getPatientNote().getKey());
                            patientNote.setScope(Scope.SINGLE);
                            a.setPatientNote(patientNote.read()); 
                        }
                        */
                        /*
                        else{
                            String message = "A zero key has been defined for the patient note object, "
                                + "raised in Appointment::Read(default read)";
                        throw new StoreException(message, StoreException.ExceptionType.NULL_KEY_EXCEPTION);
                        }
                        */
                    }
                    result = this;
                }catch(NullPointerException ex){
                    String message = "The patient object in an appointment "
                            + "object fetched from store is null, which should "
                            + "never be the case. Raised in Appointment::Read(default read)";
                    throw new StoreException(message, StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                break;
        }
        return result; 
    }
    
    protected void recover() throws StoreException{
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
                    String error = "Patient of appointment requested for recovery "
                            + "has not been defined. Recovery of "
                            + "appointment aborted \n"
                            + "Raised in Appointment.recovery()";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                
                if (!isPatientDeleted){
                    new Repository().recover(this, getKey());
                }else{
                    String error = "Patient of appointment requested for recovery "
                            + "is deleted on the system.Recovery of "
                            + "appointment is aborted \n"
                            + "Raised in Appointment.recovery()";
                    throw new StoreException(error, 
                            StoreException.ExceptionType.KEY_NOT_FOUND_EXCEPTION);
                }
                
        }else{
            String error = "Patient of the appointment requested for recovey has "
                    + "not been defined. recovery of appointmewnt aborted.\n"
                    + "Raised in Appointment.recover()";
            throw new StoreException(error, 
                    StoreException.ExceptionType.STORE_EXCEPTION);
        }
    }
    
    /**
     * Updates the current appointment field values to persistent store
     * @throws StoreException 
     */
    @Override
    public void update() throws StoreException{ 
        /**
         * 11/02/2024 code logic update
         * -- the patient note has to be updated before updated independently of the appointment update
         * -- might s well do it here as anywhere else
         */
        /**
         * 29/03/2024  code logic update
         */
        /*this.getPatientNote().update();*/
        new Repository().update(this, getKey(), getPatient().getKey()/*28/03/2024, getPatientNote().getKey()*/);
    }//</editor-fold>   
    
//<editor-fold defaultstate="collapsed" desc="Object level methods overridden in Class">
    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) 
    { 
        // if both the object references are  
        // referring to the same object. 
        if(this == obj) 
            return true; 

        // checks if the comparison involves 2 objecs of the same type 
        /**
         * issue arise if one of the objects is an entity (for example a Patient) and the other object is its delegate sub class
         */
        //if(obj == null || obj.getClass()!= this.getClass()) 
            //return false; 
        if (obj == null) return false;
        // type casting of the argument.  
        Appointment appointment = (Appointment) obj; 

        // comparing the state of argument with  
        // the state of 'this' Object. 
        return (appointment.getKey().equals(this.getKey())); 
    }
    //</editor-fold>
//</editor-fold>   

//<editor-fold defaultstate="collapsed" desc="Data migration code">
    public List<String[]> importEntityFromCSV()throws StoreException{
        return new Repository().importEntityFromCSV(this);
    }
    
    public ArrayList<Appointment> convertDBFRecordToAppointments(String[] dbfAppointmentRow)throws StoreException{
        String date;
        String year;
        String month;
        String d;
        
        date = dbfAppointmentRow[DenAppField.DATE.ordinal()];
        switch (date.length()){
            case 3: 
                date = "000" + date;
                break;
            case 4: 
                date = "00" + date;
                break;
            case 5: 
                date = "0" + date;
                break;
        }
        if (date.substring(0,1).equals("9")){
            year = "19" + date.substring(0,2);
        }
        else if (date.substring(0,2).equals("00")){
            year = "2000";
        }
        else if (date.substring(0,1).equals("0")){
            year = "200" + date.substring(1,2);
        }
        else {
            year = "20" + date.substring(0,2);
        }
        month = date.substring(2,4);
        d = date.substring(4);
        date = d + "/" + month + "/" + year;
        
        ArrayList<Appointment> appointmentsForThisDBFRecord = null;
        Patient patient = null;
        LocalDateTime start = null;
        Duration duration = null;
        String notes = null;
        Integer patientKey = null;
        Integer value = null;
        int appointmentStartTimeRowIndex = 0;
        int appointmentEndTimeRowIndex = 0;
        boolean isRowEnd = false;
        Appointment appointment = null;
        
        int rowIndex = DenAppField.A_1.ordinal();
        appointmentsForThisDBFRecord = new ArrayList<>();
        for (; rowIndex < DenAppField.A_144.ordinal(); rowIndex++){
            while(dbfAppointmentRow[rowIndex].isEmpty()){
                if (patientKey!=null){//signals end of appointment of current patient
                        appointmentEndTimeRowIndex = rowIndex;
                        appointment = getAppointmentFrom(dbfAppointmentRow,
                                                         date,
                                                         appointmentStartTimeRowIndex,
                                                         appointmentEndTimeRowIndex,
                                                         patientKey);
                        appointment.setKey(appointmentsForThisDBFRecord.size()+1);
                        appointmentsForThisDBFRecord.add(appointment);
                        patientKey = null;
                }
                rowIndex++;
                if (rowIndex > DenAppField.A_143.ordinal()){
                    isRowEnd = true;
                    break;
                }
                
            }
            if (!isRowEnd){
                if (patientKey!=null){
                    value = getPatientKey(dbfAppointmentRow[rowIndex]); 
                    if (value!=null){
                        if (!value.equals(patientKey)){//signals next appointment start slot
                            appointmentEndTimeRowIndex = rowIndex;
                            appointment = getAppointmentFrom(dbfAppointmentRow,
                                                             date,
                                                             appointmentStartTimeRowIndex,
                                                             appointmentEndTimeRowIndex,
                                                             patientKey);
                            appointment.setKey(appointmentsForThisDBFRecord.size()+1);
                            appointmentsForThisDBFRecord.add(appointment);
                            patientKey = value;
                            appointmentStartTimeRowIndex = rowIndex;
                        }
                    }
                }
                else {//first appointment after a gap
                    value = getPatientKey(dbfAppointmentRow[rowIndex]);
                    if (value!=null){
                        if (value.equals(5611) && date.equals("22/05/1992")){
                            value = 5611;
                        }
                        patientKey = value;//first appointment of the day
                        appointmentStartTimeRowIndex = rowIndex;
                    }
                }
            }
            else {
                break;
            }
        }
        return appointmentsForThisDBFRecord;
    }
    
    /**
     * converts row received (String[]) to an Appointment
     * -- from string to Appointment data types
     * -- some processing of notes field
     * Uses following methods
     * -- getAppointmentStartTime
     * @param row
     * @param date
     * @param startSlot
     * @param endSlot
     * @param patientKey
     * @return 
     */
    private static Appointment getAppointmentFrom(String[] row, 
                                                  String date, 
                                                  int startSlot, 
                                                  int endSlot, 
                                                  Integer patientKey){
        Patient patient;
        LocalDateTime start; 
        Duration duration;
        String notes = "";
        LocalTime startTime = getAppointmentStartTime(startSlot);
        LocalDate day = LocalDate.parse(date,ddMMyyyyFormat);
        start = LocalDateTime.of(day, startTime);
        patient = new Patient(patientKey);
        duration = Duration.ofMinutes((endSlot-startSlot)*5);
        int index = startSlot + 1;
        String keyString = String.valueOf(patient.getKey());
        for (; index < endSlot; index++){
            if (row[index].length() > keyString.length()){
                int code = (int)row[index].charAt(keyString.length());
                if (code==16){
                    row[index] = "----- \" -----";
                }
                if (!row[index].contains("----- \" -----")){
                    notes = notes + row[index].substring(keyString.length());
                }
            }
        }
        Appointment appointment = new Appointment();
        
        appointment.setPatient(patient);
        appointment.setStart(start);
        appointment.setDuration(duration);
        appointment.setNotes(notes);
        
        return appointment;
    }
    
    private Integer getPatientKey(String s)throws StoreException{
        int index;
        Integer result = null;
        Integer c;
        boolean includesInt16Char = false;
        s = s.strip();
        if (!(s.equals("PRIVATE TIME")||
              s.equals("EMERGENCIES")||
              s.equals("emergencies")||
              s.equals("DO NOT BOOK")||
              s.equals("LUNCH TIME")||
              s.equals("LUNCHTIME")||
              s.equals("PROV. BLOCK"))){
            for (index = 0; index < s.length(); index++){
                if (!Character.isDigit(s.charAt(index))) break;
            }
            try{
                result = Integer.parseInt(s.substring(0,index));
            }
            catch (NumberFormatException e){
                throw new StoreException(s, StoreException.ExceptionType.IO_EXCEPTION);
            }
        }
        return result;
    }
    
    private static LocalTime getAppointmentStartTime(int startRowIndex){
        int slotCountFromDayStart = startRowIndex - Appointment.DenAppField.A_1.ordinal();
        LocalTime firstSlotTimeForDay = LocalTime.of(8, 0); //= 8am
        return firstSlotTimeForDay.plusMinutes(slotCountFromDayStart * 5);
    }
    
    private  enum DenAppField {DATE,A_1,A_2,A_3,A_4,A_5,A_6,A_7,A_8,A_9,
                                A_10,A_11,A_12,A_13,A_14,A_15,A_16,A_17,A_18,A_19,
                                A_20,A_21,A_22,A_23,A_24,A_25,A_26,A_27,A_28,A_29,
                                A_30,A_31,A_32,A_33,A_34,A_35,A_36,A_37,A_38,A_39,
                                A_40,A_41,A_42,A_43,A_44,A_45,A_46,A_47,A_48,A_49,
                                A_50,A_51,A_52,A_53,A_54,A_55,A_56,A_57,A_58,A_59,
                                A_60,A_61,A_62,A_63,A_64,A_65,A_66,A_67,A_68,A_69,
                                A_70,A_71,A_72,A_73,A_74,A_75,A_76,A_77,A_78,A_79,
                                A_80,A_81,A_82,A_83,A_84,A_85,A_86,A_87,A_88,A_89,
                                A_90,A_91,A_92,A_93,A_94,A_95,A_96,A_97,A_98,A_99,
                                A_100,A_101,A_102,A_103,A_104,A_105,A_106,A_107,A_108,A_109,
                                A_110,A_111,A_112,A_113,A_114,A_115,A_116,A_117,A_118,A_119,
                                A_120,A_121,A_122,A_123,A_124,A_125,A_126,A_127,A_128,A_129,
                                A_130,A_131,_132,A_133,A_134,A_135,A_136,A_137,A_138,A_139,
                                A_140,A_141,A_142,A_143,A_144}
    //</editor-fold>
    

    

    
    
}
