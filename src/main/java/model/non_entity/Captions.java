/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.non_entity;

/**
 *
 * @author colin
 */
public class Captions {
    public static String CLOSE_VIEW = "<html><center>Close</center><center>view</center></html>";
    public enum PatientAppointmentDataView{
        PATIENT_ARCHIVE_RESTORE_REQUEST_CAPTION("<html><center>Archive</center>selected</center><center>patient(s)</center></html>",
                                                "<html><center>Restore</center><center>from archive</center><center>selected patient(s)</center></html>"),
        PATIENT_APPOINTMENT_DATA_SORT_REQUEST_CAPTION("<html><center>Sort</center><center>table by</center><center>patient</center></html>",
                                                      "<html><center>Sort</center><center>table by</center><center>appointment date</center></html>"),
        PATIENT_TIME_FRAME_REQUEST_CAPTION("<html><center>Set</center>time frame</center></html>");

        
        private String _1;
        private String _2;
        
        PatientAppointmentDataView(String value){
            _1 = value;
        }
        
        PatientAppointmentDataView(String value_1, String value_2){
            _1 = value_1;
            _2 = value_2;
        }
        
        public String _1(){
            return _1;
        }
        
        public String _2(){
            return _2;
        }
    }
    
    public enum PatientView{
        CREATE_RECOVER_PATIENT("<html><center>Create</center><center>new</center><center>patient</center></html>",
                               "<html><center>Update</center><center>selected</center><center>patient</center></html>"),
        UPDATE_RECOVER_PATIENT("<html><center>Update</center><center>selected</center><center>patient</center></html>",
                               "<html><center>Cancel</center><center>patient</center><center>recovery</center>"),
        PATIENT_CLINICAL_NOTES("<html><center>Clinical</center><center>note for</center><center>appointment</center></html"),
        SCHEDULE_FOR_APPOINTMENT("<html><center>Show</center><center>schedule for</center><center>selection</center></html>"),
        CLEAR_SELECTION("Clear selection"),
        CLOSE_VIEW("<html><center>Close</center<center>view</center></html>");
        
        private String _1;
        private String _2;
        
        PatientView(String value1){
            _1 = value1;
        }
        
        PatientView(String value1, String value2){
            _1 = value1;
            _2 = value2;
        }
        
        public String _1(){
            return _1;
        }
        
        public String _2(){
            return _2;
        }
    }
    
    public enum ScheduleView{
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
        MARK_CANCEL_UNBOOKABLE_SLOT_OR_MOVE_TO_ANOTHER_DAY_OR_FETCH_PATIENT_DETAILS("<html><center>Make slot</center<center>unbookable</center></html>",
                                    "<html><center>Cancel</center<center>unbookable</center><center>slot</center></html>",
                                    "<html><center>Move booking</center><center>to another</center><center>day</center></html>",
                                    "<html><center>Fetch</center><center>selected patient</center><center>view</center></html>"),
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
        private String fourth = "";
        
        ScheduleView(String value1){
            first = value1;
        }
        
        ScheduleView(String value1, String value2){
            first = value1;
            second = value2;
        }
        
        ScheduleView(String value1, String value2, String value3){
            first = value1;
            second = value2;
            third = value3;
        }
        
        ScheduleView(String value1, String value2, String value3, String value4){
            first = value1;
            second = value2;
            third = value3;
            fourth = value4;
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
        
        public String _4(){
            return fourth;
        }
    }
}
