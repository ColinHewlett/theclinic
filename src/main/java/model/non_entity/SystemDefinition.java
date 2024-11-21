/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

import model.non_entity.ID;
import model.entity.User;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import model.entity.Repository;
import model.repository.StoreException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author colin
 */
public class SystemDefinition {
    public static String test = null;
    //public static String BOOKABLE_SCHEDULE_SLOT_MARKER = "AVAILABLE SLOT";
    //public static String UNBOOKABLE_SCHEDULE_SLOT_MARKER = "UNBOOKABLE SLOT";
    public static Integer UNBOOKABLE_SCHEDULE_SLOT_APPOINTMENT_KEY = 1;
    
    
    public static Color UNBOOKABLE_HEADER_SLOT_FOREGROUND = new Color(255,255,255);
    public static Color UNBOOKABLE_HEADER_SLOT_BACKGROUND = new Color(0,0,0);
    public static Color UNBOOKABLE_BLOCK_SLOT_FOREGROUND = new Color(255,255,255);
    public static Color UNBOOKABLE_BLOCK_SLOT_BACKGROUND = new Color(0,0,0);
    
    public static Color EMERGENCY_HEADER_SLOT_FOREGROUND = new Color(255,255,255);
    public static Color EMERGENCY_HEADER_SLOT_BACKGROUND = new Color(255,0,0);
    public static Color EMERGENCY_BLOCK_SLOT_FOREGROUND = new Color(255,255,255);
    public static Color EMERGENCY_BLOCK_SLOT_BACKGROUND = new Color(255,0,0);
    
    public static Color BOOKED_HEADER_SLOT_FOREGROUND = new Color(0,0,0);
    public static Color BOOKED_HEADER_SLOT_BACKGROUND = new Color(255,118,29);
    public static Color BOOKED_BLOCK_SLOT_FOREGROUND = new Color(0,0,0);
    public static Color BOOKED_BLOCK_SLOT_BACKGROUND = new Color(245,227,167);
    
    public static Color BOOKABLE_SLOT_FOREGROUND = new Color(0,0,0);
    public static Color BOOKABLE_SLOT_BACKGROUND = new Color(240,240,240);
    
    public static Color BOOKABLE_BLOCK_SLOT_FOREGROUND = new Color(255,255,255);
    public static Color BOOKABLE_BLOCK_SLOT_BACKGROUND = new Color(0,0,0);
    
    public static Color BOOKABLE_SLOT_COLOR = new Color(240,240,240);
    //public static Color BOOKED_SLOT_HEADER_COLOR = new Color(245,247,208); /* need color picker up and rnning*/u
    public static Color BOOKED_SLOT_HEADER_COLOR = new Color(255,118,29);
    public static Color BOOKED_SLOT_BLOCK_COLOR = new Color(245,227,167);
    
    public static Color TITLED_BORDER_COLOR = new Color(0,0,153);
    public static Font TITLED_BORDER_FONT = new Font("Segoe UI", 1, 12);
    public static boolean DEBUG = true;
    public static int QUESTIONNAIRE_REPLY_MAX_LENGTH = 255;
    public static String TICK ="P";
    public static int EXACT_TABLE_CELL_HEIGHT_IN_TWIPS = 400;
    public static int SCHEDULE_TABLE_CELL_HEIGHT = 600;
    public static int QUESTIONNAIRE_TABLE_DOUBLE_HEIGHT = 500;
    public static int QUESTIONNAIRE_TABLE_SINGLE_HEIGHT = 300;
    public static int QUESTIONNAIRE_TABLE_MEDICATION_ROW_COUNT = 5;
    public static String FILENAME_FOR_SCHEDULE = "AppointmentSchedule.docx";
    public static String PATIENT_QUESTIONNAIRE_MEDICAL_HISTORY_FILENAME = "PatientMedicalHistory.docx";
    //public static String EMERGENCY_APPOINTMENT = "Emergency appointment";
    public static String userP;
    public static String userS;
    
    public enum LoginAction {
        REQUEST_CLOSE_LAUNCH_APP,
        REQUEST_LOGIN,
        REQUEST_ADD_NEW_CREDENTIAL,
        REQUEST_UPDATE_CREDENTIAL
    };
    
    public enum LoginViewMode{
        LOGIN,
        NEW_USER_OR_PASSWORD
    }
//<editor-fold defaultstate="collapsed" desc="System wide captions">
/*
    public enum ScheduleViewActionCaption{
        CANCEL_APPOINTMENT("<html><center>Cancel</center<center>appointment</center></html>"),
        CLINICAL_NOTES("<html><center>Clinical</center<center>notes for</center><center>appointment</center></html>"),
        CLOSE_VIEW("<html><center>Close</center<center>view</center></html>"),
        CREATE_UPDATE_APPOINTMENT("<html><center>Create</center<center>appointment</center></html>",
                                  "<html><center>Update</center<center>appointment</center></html>"),
        EXTEND_BOOKING("<html><center>Extend</center><center>appointment</center><center>earlier</center></html>",
                        "<html><center>Extend</center><center>appointment</center><center>later</center></html>",
                        "<html><center>Extend</center><center>appointment</center><center>earlier & later</center></html>"),
        MAKE_DELETE_EMERGENCY_APPOINTMENT_UNDO("<html><center>Make</center><center>emergency</center><center>appointment</center></html>",
                                          "<html><center>Delete</center><center>emergency</center><center>appointment</center></html>",
                                          "<html><center>Undo</center><center>current</center><center>selection</center></html>"),
        MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY("<html><center>Make slot</center<center>unbookable</center></html>",
                                    "<html><center>Cancel</center<center>unbookable</center><center>slot</center></html>",
                                    "<html><center>Move booking</center><center>to another</center><center>day</center></html>"),
        MOVE_BOOKING("<html><center>Move</center><center>appointment</center></html>"),
        NEXT_DAY(">>"),
        PREVIOUS_DAY("<<"),
        SEARCH_AVAILABLE_SLOTS("<html><center>Search</center><center>for</center><center>empty</center><center>schedule</center><center>slots</center</html>"),
        SELECT_TREATMENT("<html><center>Select</center<center>treatment</center></html>"),
        SHIFT_BOOKING("<html><center>Shift</center><center>appointment</center><center>earlier</center></html>",
                        "<html><center>Shift</center><center>appointment</center><center>later</center></html>"),
        SHORTEN_BOOKING("<html><center>Shorten</center><center>appointment</center></html>"),
        TODAY("Today");

        private String first = "";
        private String second = "";
        private String third = "";
        
        ScheduleViewActionCaption(String value1){
            first = value1;
        }
        
        ScheduleViewActionCaption(String value1, String value2){
            first = value1;
            second = value2;
        }
        
        ScheduleViewActionCaption(String value1, String value2, String value3){
            first = value1;
            second = value2;
            third = value3;
        }
        
        public String _1(){
            return first;
        }
        
        public String _2(){
            return second;
        }
        
        public String _3(){
            return third;
        }
    }
*/
//</editor-fold>
    
    public enum ScheduleSlotType{
        BOOKED_SCHEDULE_SLOT(null),
        BOOKABLE_SCHEDULE_SLOT("AVAILABLE SLOT"),
        UNBOOKABLE_SCHEDULE_SLOT("UNBOOKABLE SLOT"),
        EMERGENCY_SCHEDULE_SLOT("Emergency appointment");
        
        private String mark;
        ScheduleSlotType(String value){
            mark = value;
        }
        
        public String mark(){
            return mark;
        }
    }

    public enum QuestionnaireTableMetric {
        CELL_1(5100),
        CELL_2(550),
        TABLE(10300);
        
        private int width;
        QuestionnaireTableMetric(int value){
            width = value;
        }
        
        public int width(){
            return width;
        }
    }
    public enum FONT{
        SCHEDULE_HEADER(new ID("FUTURA Light", 14, false, "000000")),
        DYNAMIC(new ID("Arial Narrow", 10, false, "000000")),
        DEFAULT(new ID("Arial Narrow", 10, false, "000000")),
        DEFAULT_HEADER(new ID("Ariel Narrow", 11, true, "000000")),
        DEFAULT_BOLD(new ID("Arial Narrow", 10, true, "000000")),
        DEFAULT_BLUE(new ID("Arial Narrow", 10, true, "0000FF")),
        DEFAULT_RED(new ID("Arial Narrow", 10, true, "FF0000")),
        TICK(new ID("WingDings 2", 10, true, null));
        
        private ID value;
        FONT(ID value){
            this.value = value;
        }
        
        public String fontName(){
            return value.Name();
        }
        
        public Integer fontSize(){
            return value.Size();
        }
        
        public boolean IsFontBold(){
            return value.IsBold();
        } 
        
        public String fontColor(){
            return value.Color();
        }
    }
    
    public enum TABLE_1{
        TITLE(new Point(1,0)),
        FORENAMES(new Point(1,1)),
        SURNAME(new Point(1,2)),
        GENDER(new Point(1,3)),
        PHONE_1(new Point(3,0)),
        PHONE_2(new Point(3,1)),
        EMAIL(new Point(3,2)),
        DOB(new Point(5,0)),
        ADDRESS(new Point(5,1)),
        POSTCODE(new Point(5,2)),
        GP(new Point(7,0)),
        GP_PHONE(new Point(7,1));
        
        private final Point value;

        TABLE_1(Point value) {
            this.value = value;
        }

        public int row() {
            return value.x;
        }
        
        public int column() {
            return value.y;
        }       
    }
    
    public enum TABLE_2{
        _1(new Point(1,2)),
        MEDICATION_FIRST_ROW(new Point(2,2)), //first row of medication
        MEDICATION_LAST_ROW(new Point(6,2)), //2nd row of medication
        _3(new Point(7,2)),
        //blank row for header
        _4(new Point(9,2)),
        _5(new Point(10,2)),
        _6(new Point(11,2)),
        _7(new Point(12,2)),
        _8(new Point(13,2)),
        _9(new Point(14,2)),
        _10(new Point(15,2)),
        //blank row for header
        _11(new Point(17,2)),
        _12(new Point(18,2)),
        _13(new Point(19,2));
        
        
        TABLE_2(Point value) {
            this.value = value;
        }

        private final Point value;
        
        public int row() {
            return value.x;
        }
        
        public int column() {
            return value.y;
        }
    }
    
    public enum ScheduleTable{
        HEADER(0),
        PATIENT(0),
        FROM(1),
        TO(2),
        TREATMENT(3),
        CONFIRMED(4);

        ScheduleTable(int value) {
            this.value = value;
        }

        private final int value;
        
        public int column() {
            return value;
        }
    }
    
    public enum ScheduleTableWithContactDetails{
        HEADER(0),
        PATIENT(0),
        FROM(1),
        TO(2),
        TREATMENT(3),
        PHONE(4),
        EMAIL(5);

        ScheduleTableWithContactDetails(int value) {
            this.value = value;
        }

        private final int value;
        
        public int column() {
            return value;
        }
    }
    
    public enum QuestionnaireTable{
        CATEGORY(0),      //selects font properties for sub heading (col 0 option)
        //NUMBERED(0),    //selects auto-numbering of questions (col 0 option)
        //OTHER(0),       //neither of the above with default font setting (col 0 option)
        QUESTION(0),    //question column
        TICK(1),        //tick box column
        ANSWER(2);      //answer column
        
        QuestionnaireTable(int value) {
            this.value = value;
        }

        private final int value;
        
        public int column() {
            return value;
        }
    }
 
//<editor-fold defaultstate="collapsed" desc="Template PMS variables">
    private static HashMap<String,String> systemDefinitions = null;
    private static String pmsDebug = null;
    private static String pmsImportAppointmentData = null;
    private static String pmsImportPatientData = null;
    private static String pmsLookAndFeel = null;
    private static String pmsMasterDocument = null;
    private static String pmsSystemDefinition = null;
    private static String pmsNotesTemplateMode = null;
    private static String pmsOperationMode = null;
    private static String pmsPrintFolder = null;
    private static String pmsSMTPBody = null;
    private static String pmsSMTPServer = null;
    private static String pmsSMTPUser = null;
    private static String pmsStoreAccessURL = null;
    private static String pmsStorePostgresSQL = null;
    private static String pmsStoreType = null;
      
    public static String getPMSStoreType(){
        return pmsStoreType;
    }
    public static String getPMSStorePostgresSQLURL(){
        return pmsStorePostgresSQL;
    }
    public static String getPMSStoreAccessURL(){
        return pmsStoreAccessURL;
    }
    public static String getPMSSMTPServer(){
        return pmsSMTPServer;
    }
    public static String getPMSSMTPBody(){
        return pmsSMTPBody;
    }
    public static String getPMSSMTPUser(){
        return pmsSMTPUser;
    }
    public static String getPMSOperationMode(){
        return pmsOperationMode;
    }
    public static String getPMSPrintFolder(){
        return pmsPrintFolder;
    }
    public static String getPMSNotesTemplateMode(){
        return pmsNotesTemplateMode;
    }
    public static String getPMSSystemDefinition(){
        return pmsSystemDefinition;
    }
    public static String getPMSMasterDocument(){
        return pmsMasterDocument;
    }
    public static String getPMSLookAndFeel(){
        return pmsLookAndFeel;
    }
    public static String getPMSImportedPatientData(){
        return pmsImportPatientData;
    }
    public static String getPMSImportedAppointmentData(){
        return pmsImportAppointmentData;
    }
    public static String getPMSDebug(){
        return pmsDebug;
    }
    
    public static HashMap<String,String> getSystemDefinitions(){
        return systemDefinitions;
    }
    public static void setSystemDefinitions(HashMap<String,String> map){
        systemDefinitions = map;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            switch (entry.getKey()){
                case "PMS_DEBUG":
                    pmsDebug = entry.getValue();
                    break;
                case "PMS_IMPORT_APPOINTMENT_DATA":
                    pmsImportAppointmentData = entry.getValue();
                    break;
                case "PMS_IMPORT_PATIENT_DATA":
                    pmsImportPatientData = entry.getValue();
                    break;
                case "PMS_LOOK_AND_FEEL":
                    pmsLookAndFeel = entry.getValue();
                    break;
                case "PMS_MASTER_DOCUMENT":
                    pmsMasterDocument = entry.getValue();
                    break;
                case "PMS_NOTES_TEMPLATE_MODE":
                    pmsNotesTemplateMode = entry.getValue();
                    break;
                case "PMS_OPERATION_MODE":
                    pmsOperationMode = entry.getValue();
                    break;
                case "PMS_PRINT_FOLDER":
                    pmsPrintFolder = entry.getValue();
                case "PMS_SMTP_BODY":
                    pmsSMTPBody = entry.getValue();
                    break;
                case "PMS_SMTP_SERVER":
                    pmsSMTPServer = entry.getValue();
                    break;
                case "PMS_SMTP_USER":
                    pmsSMTPUser = entry.getValue();
                    break;
                case "PMS_STORE_ACCESS_URL":
                    pmsStoreAccessURL = entry.getValue();
                    break;
                case "PMS_STORE_POSTGRESQL_URL":
                    pmsStorePostgresSQL = entry.getValue();
                    break;
                case "PMS_STORE_TYPE":
                    pmsStoreType = entry.getValue();
                    break;
                case "PMS_SYSTEM_DEFINITION":
                    pmsSystemDefinition = entry.getValue();
                    break;
            }
        }
    }
//</editor-fold>
    
    public SystemDefinition(String value){
        test = value;
    }

    public SystemDefinition(HashMap<String,String> map){
        systemDefinitions = map;
    }
    
    private static User activeUser = null;
    public static void setActiveUser(User value){
        activeUser = value;
    }
    public static User getActiveUser(){
        return activeUser;
    }
}
