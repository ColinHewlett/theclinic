/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Point;
import repository.Repository;
import repository.StoreException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author colin
 */
public class SystemDefinition {
    public static String test = null;
    public static String APPOINTMENT_UNBOOKABILITY_MARKER = "UNBOOKABLE";
    public static Integer UNBOOKABLE_APPOINTMENT_SLOT = 1;
    private static HashMap<String,String> systemDefinitions = null;
    private static String pmsDebug = null;
    private static String pmsImportAppointmentData = null;
    private static String pmsImportPatientData = null;
    private static String pmsLookAndFeel = null;
    private static String pmsMasterDocument = null;
    private static String pmsSystemDefinition = null;
    private static String pmsNotesTemplateMode = null;
    private static String pmsOperationMode = null;
    private static String pmsSMTPBody = null;
    private static String pmsSMTPServer = null;
    private static String pmsSMTPUser = null;
    private static String pmsStoreAccessURL = null;
    private static String pmsStorePostgresSQL = null;
    private static String pmsStoreType = null;
    
    public static int QUESTIONNAIRE_REPLY_MAX_LENGTH = 130;
    public static String TICK ="P";
    public static int EXACT_TABLE_CELL_HEIGHT_IN_TWIPS = 400;

    public enum FONT{
        DYNAMIC(new ID("Arial Narrow", 10, false)),
        DEFAULT(new ID("Arial Narrow", 10, false)),
        DEFAULT_BOLD(new ID("Arial Narrow", 10, true)),
        TICK(new ID("WingDings 2", 10, true));
        
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
                case "PMS_SYSTEM_DEFINITION":
                    pmsSystemDefinition = entry.getValue();
                    break;
                case "PMS_NOTES_TEMPLATE_MODE":
                    pmsNotesTemplateMode = entry.getValue();
                    break;
                case "PMS_OPERATION_MODE":
                    pmsOperationMode = entry.getValue();
                    break;
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
            }
        }
    }
    
    public SystemDefinition(String value){
        test = value;
    }

    public SystemDefinition(HashMap<String,String> map){
        systemDefinitions = map;
    }
}
