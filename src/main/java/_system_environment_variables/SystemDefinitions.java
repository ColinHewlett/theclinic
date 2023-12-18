/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _system_environment_variables;

/**
 *
 * @author colin
 */
public class SystemDefinitions {
    public static String APPOINTMENT_UNBOOKABILITY_MARKER = "UNBOOKABLE";
    public static Integer UNBOOKABLE_APPOINTMENT_SLOT = 1;
    
    public static String getPMSLookAndFeel(){
        return System.getenv("PMS_LOOK_AND_FEEL");
    }
    
    public static String getPMSDebug(){
        return System.getenv("PMS_DEBUG");
    }
    
    public static String getPMSStoreAccessURL(){
        return System.getenv("PMS_STORE_ACCESS_URL");
    }
    
    public static String getPMSStorePostgresURL(){
        return System.getenv("PMS_STORE_POSTGRES_URL");
    }
    
    public static String PMSStoreType(){
        return System.getenv("PMS_STORE_TYPE");
    }
    
    public static String getPMSImportedAppointmentData(){
        return System.getenv("PMS_IMPORT_APPOINTMENT_DATA");
    }
    
    public static String getPMSImportedPatientData(){
        return System.getenv("PMS_IMPORT_PATIENT_DATA");
    }
    
    public static String getPMSOperationMode(){
        return System.getenv("PMS_OPERATION_MODE");
    }
}
