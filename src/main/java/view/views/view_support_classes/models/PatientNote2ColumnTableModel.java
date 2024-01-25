/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import model.PatientNote;

/**
 *
 * @author colin
 */
public class PatientNote2ColumnTableModel extends AbstractTableModel{
    //public static final LocalTime LAST_APPOINTMENT_SLOT = LocalTime.of(17,0);
    public ArrayList<PatientNote> patientNotes = new ArrayList<>();
    private DateTimeFormatter patientNoteFormat = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm (EEE)");
    private enum COLUMN{Date, Notes};
    private final Class[] columnClass = new Class[] { 
        String.class,
        String.class};
    
    
    public ArrayList<PatientNote> getPatientNotes(){
        return this.patientNotes;
    }
    
    public void addElement(PatientNote patientNote){
        patientNotes.add(patientNote);
    }
    
    public void removeAllElements(){
        patientNotes.clear();
        this.fireTableDataChanged();
    }
    
    public PatientNote getElementAt(int row){
        return patientNotes.get(row);
    }

    @Override
    public int getRowCount(){
        return getPatientNotes().size();
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        switch (columnIndex){
            case 0:
                result = "Date";
                break;
            case 1:
                result = "Notes";
                break;
            }

        return result;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        PatientNote patientNote = getPatientNotes().get(row);
        switch (columnIndex){
            case 0:
                result = patientNote.getDatestamp().format(patientNoteFormat);  
                break;   
            case 1:
                result = patientNote.getNote();
                /*
                result = convertSlotDurationToString(
                    patientNote.getNote(), patientNote.getDatestamp().toLocalDate()); 
                */
                break; 
        }
        return (String)result;
    }
    
    /*
    @Override
    public void setValueAt(Object value, int row, int columnIndex){
        
    }
    */
    /*
    private String convertSlotDurationToString(Duration duration, LocalDate start){
        String result = null;
        if (duration.toHours() < 8) result = renderDurationLessThanSingleDay(duration);
        else result = renderDurationMoreThanOrEqualToSingleDay(duration, start); 
        
        return result;
    }
    
    private String renderDurationLessThanSingleDay(Duration duration){
        String result = null;
        if (!duration.isZero()){
            int hours = getHoursFromDuration(duration.toMinutes());
            int minutes = getMinutesFromDuration(duration.toMinutes());
            switch (hours){
                case 0:
                    result = String.valueOf(minutes) + " minutes";
                    break;
                case 1:
                    result = (minutes == 0) ? 
                        String.valueOf(hours) + " hour" : 
                        String.valueOf(hours) + " hour " + String.valueOf(minutes) + " minutes";
                    break;
                default:
                    result = (minutes == 0) ?
                        String.valueOf(hours) + " hours" :
                        String.valueOf(hours) + " hours " + String.valueOf(minutes) + " minutes";
                    break;
            }
        }
        return result;
    }
    */
    /**
     * Slightly tricky calculation involved because method includes in its counting
     * whether the interim days fall on a practice day or not. The actual duration 
     * can therefor exceed the specified duration.
     * @param duration Duration representing one or more whole days
     * @param start LocalDate represents the start date of the empty slot being processed,
     * required to calculate the "until" date
     * @return String representing the closing date and time of the empty slot when duration
     * is more than one day or "all day" if duration is a single day only
     */
    /*
    private String renderDurationMoreThanOrEqualToSingleDay(Duration duration, LocalDate start){
        String result = null;
        int index;
        LocalDate currentDate = start;
        int practiceDays = (int)duration.toHours() / 8;
        if (practiceDays == 1) result = "all day";
        else{
            int dayCount = 0;//at this point duration must be at least 2 days from start
            for (index = 0; index < practiceDays ; index ++){
                while(!isValidDay(currentDate.plusDays(dayCount++))){

                } 
            }
            currentDate = currentDate.plusDays(--dayCount);
            result = "until " + 
                    currentDate.atTime(LAST_APPOINTMENT_SLOT).format(emptySlotFormat);
        }
        return result;
    }
    
    private Integer getHoursFromDuration(long duration){
        return (int)duration / 60;
    }
    private Integer getMinutesFromDuration(long duration){
        return (int)duration % 60;
    }
    
    private boolean isValidDay(LocalDate day){
        return(day.getDayOfWeek().equals(DayOfWeek.TUESDAY) 
                            || day.getDayOfWeek().equals(DayOfWeek.THURSDAY)
                            || day.getDayOfWeek().equals(DayOfWeek.FRIDAY));
    }
    */
}
