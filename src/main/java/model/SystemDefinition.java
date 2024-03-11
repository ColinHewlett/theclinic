/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
    private static String pmsNotesTemplate = null;
    private static String pmsNotesTemplateMode = null;
    private static String pmsOperationMode = null;
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
    public static String getPMSNotesTemplateMode(){
        return pmsNotesTemplateMode;
    }
    public static String getPMSNotesTemplate(){
        return pmsNotesTemplate;
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
                case "PMS_NOTES_TEMPLATE":
                    pmsNotesTemplate = entry.getValue();
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
