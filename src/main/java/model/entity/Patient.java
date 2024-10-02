/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entity;
//<editor-fold defaultstate="collapsed" desc="Imports">
import model.entity.Notification;
import model.entity.Medication;
import model.entity.Entity;
import model.entity.Doctor;
import model.entity.Appointment;
import model.non_entity.SystemDefinition;
import repository.Repository;
import repository.StoreException;//01/03/2023
import java.awt.Point;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.IEntityStoreActions;
import repository.IStoreActions;
//</editor-fold>

/**
 *
 * @author colin.hewlett.solutions@gmail.com
 */
public class Patient extends Entity implements IEntityStoreActions {
    
//<editor-fold defaultstate="collapsed" desc="Private and protected state">    
    //private Boolean isPatientKeyDefined = null;
    private LocalDate dob = null;
    private Patient guardian = null;
    private String gender = null;
    private Boolean isGuardianAPatient = null;
    //private Integer key  = null;
    private String notes = null;
    private String phone1 = null;
    private String phone2 = null;
    private String email = null;
    private Patient.Address address = null;
    private Patient.Name name = null;
    private Patient.Recall recall = null;
    private Patient.MedicalHistory medicalHistory= null;
    private ArrayList<Patient> collection = null;
    
    
    private static final DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
//</editor-fold>
    
 //<editor-fold defaultstate="collapsed" desc="Public interface">
    
 //<editor-fold defaultstate="collapsed" desc="Persistent store-related operations">
    
    /**
     * Counts the number of patients stored on the system
     * @return Integer, total number of patients on the system
     * @throws StoreException 
     */
    @Override
    public Point count()throws StoreException{
        return new Repository().count(this);
    } 
    
    
    @Override
    public void create()throws StoreException{
        new Repository().create(this);
    }

    @Override
    public void delete() throws StoreException{    
        new Repository().delete(this,getKey());    
    }
    
    public void recover() throws StoreException{
        new Repository().recover(this, getKey());
        /*
        for(var a : getDeletedAppointmentHistory()){
            a.recover();
        }
        for(var n : getDeletedNotificationHistory()){
            n.recover();
        }
        */
    }

    /** 
     * Not currently implemented
     * @throws StoreException 
     */
    @Override
    public void drop() throws StoreException{
        //IStoreActions store = Store.FACTORY((Entity)this);
        //store.drop(this);        
    }
    
    public ArrayList<Appointment> getDeletedAppointmentHistory()throws StoreException{
        Appointment appointment = new Appointment();
        appointment.setPatient(this);
        appointment.setScope(Scope.DELETED_FOR_PATIENT);
        appointment.read();
        return appointment.get();
    }
    
    public ArrayList<Notification> getDeletedNotificationHistory()throws StoreException{
        Notification notification = new Notification();
        notification.setPatient(this);
        notification.setScope(Scope.DELETED_FOR_PATIENT);
        notification.read();
        return notification.get();
    }
    
    
    public ArrayList<Appointment> getAppointmentHistory()throws StoreException{
        Appointment appointment = new Appointment();
        appointment.setPatient(this);
        appointment.setScope(Scope.FOR_PATIENT);
        appointment.read();
        return appointment.get();
    }

    /**
     * method inserts this Patient as a record in persistent store
     * -- not key already in itialised is this is part of a data migration operation
     * -- else if this is a new patient record being created the new key value is returned from persistent store and used to initialised this Patient object
     * @throws StoreException 
     */
    @Override
    public Integer insert() throws StoreException{
        Integer pid = null;
        
        if (getIsKeyDefined()){//option followed if data migration is happening
            pid = new Repository().insert(this,getKey(), null);
        }
        else {
            if (getIsGuardianAPatient()){//this option taken if a new patient record is being created
                pid = new Repository().insert(this, getKey(),getGuardian().getKey());
                if (pid==null){
                    throw new StoreException("StoreException raised in Patient.insert() because expected guardian key value to be defined, and was null.",
                    StoreException.ExceptionType.UNEXPECTED_NULL_GUARDIAN_KEY);
                }
                else{
                   pid = new Repository().insert(this,getKey(), pid);
                }
            }
            else pid = new Repository().insert(this,getKey(), null);
        }
        setKey(pid); 
        setIsKeyDefined(true);
        return getKey();
    }
    
    /**
     * the read method interrogates the Patient object returned from store, thus
     * -- if its getIsGuardianAKey() method returns true it reads the patient's guardian object in as well
     * -- the returned patient AppointmentHistory's fetchDentalAppointments() method is called to include all appointments associated with this patient 
     * @return -- if a SINGLE read a fully initialised Patient object appropriately with or without a guardian if one exists
     *         -- else the Patient object state unchanged state on entry apart from the collection of patients returned by the read operation
     * @throws StoreException 
     */
    @Override
    public Patient read() throws StoreException{
        Patient result;
        Patient patient;
        Patient patientGuardian;
        IStoreActions store = null;
        switch (getScope()){
            case SINGLE:
            case DELETED:
                patient = new Repository().read(this, getKey()); 
                if (patient.getIsGuardianAPatient()){
                    patientGuardian = patient.getGuardian();
                    patientGuardian.setScope(Scope.SINGLE);
                    patientGuardian = new Repository().read(patientGuardian, patientGuardian.getKey());
                    patient.setGuardian(patientGuardian);
                }
                result =  patient;
                break;
            default: // means request is for a collection of patients
                result = new Repository().read(this, null);
                break;
        }
        return result;
    }

    @Override
    public void update() throws StoreException{ 
        
        if (getIsGuardianAPatient()) 
            if (getGuardian()!=null)
                new Repository().update(this, this.getKey(),
                        this.getGuardian().getKey());
            else new Repository().update(this, this.getKey(),null);
        else {
            new Repository().update(this, this.getKey(),null);
        }

    }

    //</editor-fold>
    
 //<editor-fold defaultstate="collapsed" desc="Public interface not including persistent storage related operatioms">
    public enum PatientField    {       ID,
                                        KEY,
                                        PHONE1,
                                        PHONE2,
                                        EMAIL,
                                        GENDER,
                                        DOB,
                                        IS_GUARDIAN_A_PATIENT,
                                        PATIENT_GUARDIAN,
                                        NOTES;
                    
                                }
    
    public Patient(){
    
        name = new Name();
        address = new Address();
        recall = new Recall();
        medicalHistory = new MedicalHistory();
        setIsGuardianAPatient(false);
        setIsKeyDefined(false);
        this.setIsPatient(true);
    } 
    
    public Patient(Integer key) {
            name = new Name();
            address = new Address();
            recall = new Recall();
            setKey(key);
            setIsGuardianAPatient(false);
            this.setIsPatient(true);
    } 
    
    public boolean getIsPatientMarkedUnbookable(){
        return getKey().equals(1);
    }
    
    /** 
     * @return collection of patients in persistent store less patient with a key = 1
     */
    public ArrayList<Patient> get(){
        ArrayList<Patient> patients = new ArrayList<>();
        for(Patient patient : collection){
            if (patient.getKey()!=1)
                patients.add(patient);  
        }
        return patients;
    }
    
    public MedicalHistory getMedicalHistory(){
        return medicalHistory;
    }
    
    public void setMedicalHistory(MedicalHistory value){
        medicalHistory = value;
    }

    public void set(ArrayList<Patient> value){
        collection = value;
    }
    
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDOB() {
        return dob;
    }
    public void setDOB(LocalDate dob) {
        this.dob = dob;
    }
    
    public String getNotes(){
        return notes;
    }
    public void setNotes(String notes){
        this.notes = notes;
    }
    
    public String getPhone1(){
        if (phone1==null) phone1 = "";
        return phone1;
    }
    public void setPhone1(String phone1){
        this.phone1 = phone1;
    }
    
    public String getPhone2(){
        if (phone2==null) phone2 = "";
        return phone2;
    }
    public void setPhone2(String phone2){
        this.phone2 = phone2;
    }
    
    public String getEmail(){
        if (email==null) email = "";
        return email;
    }
    
    public void setEmail(String value){
        this.email = value;
    }
    
    private int getAge(LocalDate dob){
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
    public Boolean getIsGuardianAPatient(){
        if (getDOB()!=null){
            if (getAge(getDOB()) >= 18) return false;
            else return isGuardianAPatient;
        }
        else return isGuardianAPatient;
    }
    
    public void setIsGuardianAPatient(Boolean isGuardianAPatient){
        this.isGuardianAPatient = isGuardianAPatient;
    }
    
    public Patient getGuardian(){
        return guardian;
    }
    public void setGuardian(Patient guardian){
        this.guardian = guardian;
    }
    
    public Patient.Name getName(){
        return name;
    }
    public void setName(Patient.Name name){
        this.name = name;
    }
    
    public Patient.Address getAddress(){
        return address;
    }
    public void setAddress(Patient.Address address){
        this.address = address;
    }
    
    public Patient.Recall getRecall(){
        return recall;
    }
    public void setRecall(Patient.Recall recall){
        this.recall = recall;
    }
    
    public enum PhoneStatus{
        NO_PHONE,
        PHONE_1,
        PHONE_2,
        TWO_PHONES;
    }
    public enum EmailStatus{
        NO_EMAIL,
        HAS_EMAIL;
    }
    
    private PhoneStatus phoneStatus = null;
    public PhoneStatus getPhoneStatus(){
        PhoneStatus result = null;
        if ((getPhone1().trim().isEmpty()) && (getPhone2().trim().isEmpty()))result = PhoneStatus.NO_PHONE;
        else if (!(getPhone1().trim().isEmpty()) && !(getPhone2().trim().isEmpty()))result = PhoneStatus.TWO_PHONES;
        else if (getPhone1().trim().isEmpty())result = PhoneStatus.PHONE_2;
        else result = PhoneStatus.PHONE_1;
        return result;
    }
    
    private EmailStatus emailStatus = null;
    public EmailStatus getEmailStatus(){
        EmailStatus result = null;
        if(getEmail().trim().isEmpty()) result = EmailStatus.NO_EMAIL;
        else result = EmailStatus.HAS_EMAIL;
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Patient Name inner class">
    public class Name {

        private String forenames = null;
        private String surname = null;
        private String title = null;

        public String getForenames() {
            if (forenames==null) forenames = "";
            return forenames;
        }

        public void setForenames(String forenames) {
            this.forenames = forenames;
        }

        public String getSurname() {
            if (surname==null) surname = "";
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getTitle() {
            if (title ==null) title = "";
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Patient Address inner class">
    public class Address {

        private String line1 = null;
        private String line2 = null;
        private String town = null;
        private String county = null;
        private String postcode = null;

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

    }
    //</editor-fold>
    
    public class MedicalHistory{
        private ArrayList<PrimaryCondition> collection = null;
        private Doctor doctor;
        private Medication medication = null;
        
        public MedicalHistory(){

        } 
        
        public ArrayList<PrimaryCondition> get(){
            return collection;
        }
        public void set(ArrayList<PrimaryCondition> value){
            collection = value;
        }
        
        /**
         * 
         * @return Doctor; if null it means no doctor on the system for this patient
         */
        public Doctor getDoctor()throws StoreException{
            doctor = new Doctor(Patient.this);
            doctor.setScope(Scope.FOR_PATIENT);
            doctor = doctor.read();
            return doctor;
        }
        
        public void setDoctor(Doctor dr)throws StoreException{
            dr.setPatientKey(Patient.this.getKey());
            dr.insert();
        }
        
        /**
         * 
         * @return Medication; if null it means no medication on the system for this patient
         */
        public Medication getMedication()throws StoreException{
            medication = new Medication(Patient.this);
            medication.setScope(Scope.FOR_PATIENT);
            return medication.read();
        }
        
        public void setMedication(Medication dr)throws StoreException{
            dr.setPatientKey(Patient.this.getKey());
            dr.insert();
        }
        
        public PrimaryCondition getPrimaryCondition()throws StoreException{
           
            /*
            PrimaryCondition pc = new PrimaryCondition(Patient.this);
            pc.setScope(Scope.FOR_PATIENT);
            for(Condition condition : pc.read().get()){
                PrimaryCondition p = (PrimaryCondition)condition;
                SecondaryCondition s = new SecondaryCondition(p);
                s.setScope(Scope.FOR_PRIMARY_CONDITION);
                s.read();
                p.setSecondaryCondition(s);
            }*/
            return null;
        }
        
        /*
        public void insert()throws StoreException{
            
            for(PrimaryCondition pc : get()){
                pc.setPatientKey(Patient.this.getKey());
                new Repository().insert(pc);
            }
        }
        */
        public void update(){
            
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Patient Recall inner class">
    public class Recall {

        private Integer hygieneFrequency = null;

        private LocalDate dentalDate = null;
        public LocalDate getDentalDate() {
            return dentalDate;
        }
        public void setDentalDate(LocalDate dentalDate) {
            this.dentalDate = dentalDate;
        }
        
        private Integer dentalFrequency = null;
        public Integer getDentalFrequency() {
            return dentalFrequency;
        }
        public void setDentalFrequency(Integer dentalFrequency) {
            this.dentalFrequency = dentalFrequency;
        }

        private LocalDate gbtDate = null;
        public LocalDate getGBTDate() {
            return gbtDate;
        }
        public void setGBTDate(LocalDate gbtDate) {
            this.gbtDate = gbtDate;
        }

        private Integer gbtFrequency = null;
        public Integer getGBTFrequency() {
            return gbtFrequency;
        }
        public void setGBTFrequency(Integer gbtFrequency) {
            this.gbtFrequency = gbtFrequency;
        }
    }
    //</editor-fold>
    
    
    //</editor-fold>

 //<editor-fold defaultstate="collapsed" desc="Object level methods overriden in Patient class">
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
        Patient patient = (Patient) obj; 

        // comparing the state of argument with  
        // the state of 'this' Object. 
        return (patient.getKey().equals(this.getKey())); 
    }
    
    @Override
    /**
     * re-defines default format patient name display
     * -- basically: "surname, forename"
     * -- first letter of surname and any subsequent part is capitalised
     * -- first letter of forename and any subsequent part is capitalised 
     * 
     */
    public String toString(){
        String cappedName = null;
        if (getKey() == 1){
            return SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark();
        }
        if (!getName().getSurname().isEmpty()){
            //if (getData().getSurname().strip().contains("-")) 
            if (getName().getSurname().contains("-"))
                cappedName = capitaliseFirstLetter(getName().getSurname(), "-");
            //else if (getData().getSurname().strip().contains(" "))
            else if (getName().getSurname().contains(" "))
                cappedName = capitaliseFirstLetter(getName().getSurname(), "\\s+");
            else
                cappedName = capitaliseFirstLetter(getName().getSurname(), "");
        }
        if (!getName().getForenames().isEmpty()){
            if (cappedName!=null){
                //if (getData().getForenames().strip().contains("-")) 
                if (getName().getForenames().contains("-"))
                    cappedName = cappedName + ", " + capitaliseFirstLetter(getName().getForenames(), "-");
                //else if (getData().getForenames().strip().contains(" ")) 
                else if (getName().getForenames().contains(" "))
                    cappedName = cappedName + ", " + capitaliseFirstLetter(getName().getForenames(), "\\s+");
                else cappedName = cappedName + ", " + capitaliseFirstLetter(getName().getForenames(), "");
            }
            else{
                //if (getData().getForenames().strip().contains("-")) 
                if (getName().getForenames().contains("-")) 
                    cappedName = ", " + capitaliseFirstLetter(getName().getForenames(), "-");
                //else if (getData().getForenames().strip().contains(" ")) 
                else if (getName().getForenames().contains(" "))
                    cappedName = ", " + capitaliseFirstLetter(getName().getForenames(), "\\s+");
                else cappedName = ", " + capitaliseFirstLetter(getName().getForenames(), "");
            }
        }
        if (!getName().getTitle().isEmpty()){
            if (cappedName!=null)
                cappedName = cappedName + " (" + capitaliseFirstLetter(getName().getTitle(), "") + ")";
            else cappedName = "(" + capitaliseFirstLetter(getName().getTitle(), "") + ")";
        }
        return cappedName;
    }
    //</editor-fold>
    
    //</editor-fold>

 //<editor-fold defaultstate="collapsed" desc="Data migration operations">
    public void reformat(){
        String cappedForenames = "";
        String cappedSurname = "";
        String cappedTitle = "";
        String cappedLine1 = "";
        String cappedLine2 = "";
        String cappedTown = "";
        String cappedCounty = "";
        
        /**
         * 11/01/2023 10:05 update
         * -- replaces "M" and "F" gender values with "Male" and "Female"
         */
        switch (getGender()){
            case "M":
                setGender("Male");
                break;
            case "F":
                setGender("Female");
                break;  
        }

        if (getAddress().getLine1() == null) getAddress().setLine1("");
        if (getAddress().getLine1().length()>0){
            //cappedLine1 = getAddress().getLine1().strip();
            cappedLine1 = getAddress().getLine1();
            if (cappedLine1.contains("-")){ 
                cappedLine1 = capitaliseFirstLetter(cappedLine1, "-");
                if (cappedLine1.contains(" ")){
                   cappedLine1 = capitaliseFirstLetter(cappedLine1, "\\s+"); 
                }
            }
            else if (cappedLine1.contains(" ")) 
                cappedLine1 = capitaliseFirstLetter(cappedLine1, "\\s+");
            else
                cappedLine1 = capitaliseFirstLetter(cappedLine1, "");
        }
        
        if (getAddress().getLine2() == null) getAddress().setLine2("");
        if (getAddress().getLine2().length()>0){
            //cappedLine2 = getAddress().getLine2().strip();
            cappedLine2 = getAddress().getLine2();
            if (cappedLine2.contains("-")){ 
                cappedLine2 = capitaliseFirstLetter(cappedLine2, "-");
                if (cappedLine2.contains(" ")){
                   cappedLine2 = capitaliseFirstLetter(cappedLine2, "\\s+"); 
                }
            }
            else if (cappedLine2.contains(" ")) 
                cappedLine2 = capitaliseFirstLetter(cappedLine2, "\\s+");
            else
                cappedLine2 = capitaliseFirstLetter(cappedLine2, "");
        }
        
        if (getAddress().getTown() == null) getAddress().setTown("");
        if (getAddress().getTown().length()>0){
            //cappedTown = getAddress().getTown().strip();
            cappedTown = getAddress().getTown();
            if (cappedTown.contains("-")){ 
                cappedTown = capitaliseFirstLetter(cappedTown, "-");
                if (cappedTown.contains(" ")){
                   cappedTown = capitaliseFirstLetter(cappedTown, "\\s+"); 
                }
            }
            else if (cappedTown.contains(" ")) 
                cappedTown = capitaliseFirstLetter(cappedTown, "\\s+");
            else
                cappedTown = capitaliseFirstLetter(cappedTown, "");
        }
        
        if (getAddress().getCounty() == null) getAddress().setCounty("");
        if (getAddress().getCounty().length()>0){
            //cappedCounty = getAddress().getCounty().strip();
            cappedCounty = getAddress().getCounty();
            if (cappedCounty.contains("-")){ 
                cappedCounty = capitaliseFirstLetter(cappedCounty, "-");
                if (cappedCounty.contains(" ")){
                   cappedCounty = capitaliseFirstLetter(cappedCounty, "\\s+"); 
                }
            }
            else if (cappedCounty.contains(" ")) 
                cappedCounty = capitaliseFirstLetter(cappedCounty, "\\s+");
            else
                cappedCounty = capitaliseFirstLetter(cappedCounty, "");
        }
        
        if (getName().getSurname() == null) getName().setSurname("");
        if (getName().getSurname().length()>0){
            //cappedSurname = getName().getSurname().strip();
            cappedSurname = getName().getSurname();
            if (cappedSurname.contains("-")){ 
                cappedSurname = capitaliseFirstLetter(cappedSurname, "-");
                if (cappedSurname.contains(" ")){
                   cappedSurname = capitaliseFirstLetter(cappedSurname, "\\s+"); 
                }
            }
            else if (cappedSurname.contains(" ")) 
                cappedSurname = capitaliseFirstLetter(cappedSurname, "\\s+");
            else
                cappedSurname = capitaliseFirstLetter(cappedSurname, "");
        }
        if (getName().getForenames() == null) getName().setForenames("");
        if (getName().getForenames().length()>0){
            //cappedForenames = getName().getForenames().strip();
            cappedForenames = getName().getForenames();
            if (cappedForenames.contains("-")){ 
                cappedForenames = capitaliseFirstLetter(cappedForenames, "-");
                if (cappedForenames.contains(" ")){
                   cappedForenames = capitaliseFirstLetter(cappedForenames, "\\s+"); 
                }
            }
            else if (cappedForenames.contains(" ")) 
                cappedForenames = capitaliseFirstLetter(cappedForenames, "\\s+");
            else
                cappedForenames = capitaliseFirstLetter(cappedForenames, "");
        }
        if (getName().getTitle() == null) getName().setTitle("");
        if (getName().getTitle().length()>0){
            //cappedTitle = getName().getTitle().strip();
            cappedTitle = getName().getTitle();
            cappedTitle = capitaliseFirstLetter(cappedTitle, "");
        }
        getName().setSurname(cappedSurname);
        getName().setForenames(cappedForenames);
        getName().setTitle(cappedTitle);
        getAddress().setLine1(cappedLine1);
        getAddress().setLine2(cappedLine2);
        getAddress().setTown(cappedTown);
        getAddress().setCounty(cappedCounty);
    }
   
    public List<String[]> importEntityFromCSV()throws StoreException{
        return new Repository().importEntityFromCSV(this);
    }
    
    public Patient convertDBFToPatient(String[] dbfPatientRow){
        Patient patient = new Patient();
        for (Patient.DenPatField pf: Patient.DenPatField.values()){
            switch (pf){
                case KEY:
                    patient.setKey(Integer.parseInt(dbfPatientRow[pf.ordinal()]));
                    break;
                case TITLE:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getName().setTitle(dbfPatientRow[pf.ordinal()]);   
                    }
                    else patient.getName().setTitle("");
                    break;
                case FORENAMES:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getName().setForenames(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.getName().setForenames("");
                    break;
                case SURNAME: 
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getName().setSurname(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.getName().setSurname("");
                    break;
                case LINE1:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getAddress().setLine1(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.getAddress().setLine1("");
                    break;
                case LINE2:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getAddress().setLine2(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.getAddress().setLine2("");
                    break;
                case TOWN:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.getAddress().setTown(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.getAddress().setTown("");
                    break;
                case COUNTY:
                    patient.getAddress().setCounty(dbfPatientRow[pf.ordinal()]);
                    break;
                case POSTCODE:
                    patient.getAddress().setPostcode(dbfPatientRow[pf.ordinal()]);
                    break;
                case PHONE1:
                    patient.setPhone1(dbfPatientRow[pf.ordinal()]);
                    break;
                case PHONE2:
                    patient.setPhone2(dbfPatientRow[pf.ordinal()]);
                    break;
                case GENDER:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.setGender(dbfPatientRow[pf.ordinal()]);
                    }
                    else patient.setGender("");
                    break;
                case DOB:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.setDOB(LocalDate.parse(dbfPatientRow[pf.ordinal()],ddMMyyyyFormat));
                    }
                    else patient.setDOB(null);
                    break;
                case IS_GUARDIAN_A_PATIENT:
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        patient.setIsGuardianAPatient(Boolean.valueOf(dbfPatientRow[pf.ordinal()]));
                    }
                    else patient.setIsGuardianAPatient(Boolean.valueOf(false));
                    break;
                case DENTAL_RECALL_FREQUENCY:
                    boolean isDigit = true;
                    Integer value = 0;
                    String s = dbfPatientRow[pf.ordinal()];
                    if (!s.isEmpty()){
                        s = s.strip();
                        char[] c = s.toCharArray(); 
                        for (int index = 0; index < c.length; index++){
                            if (!Character.isDigit(c[index])){
                                isDigit = false;
                                break;
                            }
                        }
                        if (isDigit){
                            value = Integer.parseInt(s);
                        }
                        else value = 0;
                    }
                    else value = 0;
                    patient.getRecall().setDentalFrequency(value);
                    break;
                /**
                 * huge issue here
                 * -- Excel file saved as CSV renders recall date as (for example) "May-07"
                 * -- in fact actual data is "MAY/02"!!
                 * -- also isolated occurrence of "APR/ 9" which logic couldn't handle
                 * -- eventual logic if 1st char after "/" is blank, remove blank with call to String strip()
                 * -- also logic overlooked checking for a date in the 80's, now corrected
                 */
                case DENTAL_RECALL_DATE: 
                    boolean isInvalidMonth = false;
                    String $value = "";
                    String[] values;
                    int mm = 0;
                    int yyyy = 0;
                    LocalDate recallDate = null;
                    $value = dbfPatientRow[pf.ordinal()];
                    /*
                    if (isSelectedKey){
                        int test = 0;
                        test++;
                    }
                    */
                    isInvalidMonth = false;
                    if ($value.length()>1){
                        $value = $value.strip();
                        values = $value.split("/");
                        if (values.length > 0){
                            switch (values[0]){
                                case "Jan": 
                                case "JAN":
                                    mm = 1;
                                    break;
                                case "Feb":
                                case "FEB":
                                    mm = 2;
                                    break;
                                case "Mar":
                                case "MAR":
                                    mm = 3;
                                    break;
                                case "Apr":
                                case "APR":
                                    mm = 4;
                                    break;
                                case "May":
                                case "MAY":
                                    mm = 5;
                                    break;
                                case "Jun":
                                case "JUN":
                                    mm = 6;
                                    break;
                                case "Jul":
                                case "JUL":
                                    mm = 7;
                                    break;
                                case "Aug":
                                case "AUG":
                                    mm = 8;
                                    break;
                                case "Sep":
                                case "SEP":
                                    mm = 9;
                                    break;
                                case "Oct":
                                case "OCT":
                                    mm = 10;
                                    break;
                                case "Nov":
                                case "NOV":
                                    mm = 11;
                                    break;
                                case "Dec":
                                case "DEC":
                                    mm = 12;
                                    break;
                                default:
                                    isInvalidMonth = true;
                                    break;
                            }
                        }
                        if (!isInvalidMonth){
                            if ((values[1].substring(0,1).equals("9")) || (values[1].substring(0,1).equals("8"))){
                                yyyy = 1900 + Integer.parseInt(values[1]); 
                                //break;
                            }
                            else if(values[1].substring(0,1).equals(" ")){
                                String v = values[1].strip();
                                yyyy = 2000 + Integer.parseInt(v);
                            }
                            else{
                                yyyy = 2000 + Integer.parseInt(values[1]);
                            }
                            recallDate = LocalDate.of(yyyy, mm, 1);
                            patient.getRecall().setDentalDate(recallDate);
                        }
                        else patient.getRecall().setDentalDate(null);
                    }
                    break;
                case NOTES:   
                    String notes = "";
                    if (!dbfPatientRow[pf.ordinal()].isEmpty()){
                        notes = notes + dbfPatientRow[pf.ordinal()];
                    }

                    if (!dbfPatientRow[pf.ordinal()+1].isEmpty()){
                        if (!notes.isEmpty()){
                            notes = notes + "; ";
                        }
                        notes = notes + dbfPatientRow[pf.ordinal()+1];
                    }
                    patient.setNotes(notes);

                //case GUARDIAN -> patient.setGuardian(null); 
            }
        }
        patient.setIsGuardianAPatient(Boolean.FALSE);
        patient.setGuardian(null);
        return patient;
    }
    
    /**
     * Utility method involved in the "tidy up" of the imported patient's contact details
     * @param value:String
     * @param delimiter:String representing the character used to delimit in the context of the patient's name
     * @return Sting; the processed patient's contact details
     */
    private String capitaliseFirstLetter(String value, String delimiter){
        ArrayList<String> parts = new ArrayList<>();
        String result = null;
        //value = value.strip();
        if (!delimiter.equals("")){
            String[] values = value.split(delimiter);
            for (int index = 0; index < values.length; index++){
                parts.add(capitalisePart(values[index]));
            }
            for (int index = 0;index < parts.size();index++){
                if (index == 0){
                    result = parts.get(index);
                }
                else if (delimiter.equals("\\s+")){
                    result = result + " " + parts.get(index);
                }
                else{
                    result = result + delimiter + parts.get(index);
                }
            }
        }
        else{
            result = capitalisePart(value);
        }
        return result;
    }
    
    /**
     * Part of the convenience process used for tidying up the imported patient's contact details
     * @param part:String; part of the string required to be processed
     * @return String; processed part
     */
    private String capitalisePart(String part){
        String firstLetter = part.substring(0,1).toUpperCase();
        String otherLetters = part.substring(1).toLowerCase();
        String result =  firstLetter + otherLetters;
        return result;
    }

    enum DenPatField {  KEY,
                        TITLE,
                        FORENAMES,
                        SURNAME,
                        LINE1,
                        LINE2,
                        TOWN,
                        COUNTY,
                        POSTCODE,
                        PHONE1,
                        PHONE2,
                        GENDER,
                        DOB,
                        IS_GUARDIAN_A_PATIENT,
                        DENTAL_RECALL_FREQUENCY,
                        DENTAL_RECALL_DATE,
                        NOTES,
                        GUARDIAN}
    //</editor-fold>   
}

