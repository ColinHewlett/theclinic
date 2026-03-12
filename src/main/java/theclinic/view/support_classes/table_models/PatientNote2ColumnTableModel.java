/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
/*28/03/2024import model.PatientNote;*/

/**
 *
 * @author colin
 */
public class PatientNote2ColumnTableModel /*28/03/2024extends AbstractTableModel*/{
    /*28/03/2024
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
        return 0;//return COLUMN.values().length;
        
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
        //return columnClass[columnIndex];
        return null;
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

                break; 
        }
        return (String)result;

    }*/
    
    
}
