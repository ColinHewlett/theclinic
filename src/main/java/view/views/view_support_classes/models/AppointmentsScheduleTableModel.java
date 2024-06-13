/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.view_support_classes.models;

import model.entity.Appointment;
import model.entity.Patient;
/*28/03/2024import model.PatientNote;*/
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author colin
 * update:
 * -- strategy remove the statically defined data structure in the table model
 * -- client uses addElement method to add each appointment to table model
 * -- extend from AbstractTableModel instead of DefaultTableModel
 * -- replaces -> public static ArrayList<Descriptor.Appointment> appointments = null;
 -- with -> public ArrayList<Descriptor.Appointment> appointments = new ArrayList<>();
 */
//public class AppointmentsScheduleTableModel extends AbstractTableModel{
public class AppointmentsScheduleTableModel extends DefaultTableModel{
    //public ArrayList<Appointment> appointments = new ArrayList<>();
    private ArrayList<Appointment> appointments = null;
    private enum COLUMN{Patient, From,To,Duration,Treatment,Reminded};
    private final Class[] columnClass = new Class[] {
        Patient.class,
        LocalTime.class, 
        LocalTime.class, 
        Duration.class, 
        /*28/03/2024PatientNote*/String.class,
        Boolean.class};

    public AppointmentsScheduleTableModel(){
        appointments = new ArrayList<>();
        
    }
    
    public ArrayList<Appointment> getAppointments(){
        return this.appointments;
    }
    
    public void addElement(Appointment a){
        appointments.add(a);
    }
    
    public void removeAllElements(){
        appointments.clear();
        //this.fireTableDataChanged();
    }
    
    public Appointment getElementAt(int row){
        return appointments.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (appointments!=null) result = appointments.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                if (result.equals("Reminded"))
                    result = result +" ?";
                break;
            }
        }
        return result;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col==5){
            Appointment appointment = appointments.get(row);
            //checkslot is booked out to a patient
            if (appointment.getPatient()!=null){
                if (!appointment.getIsUnbookableSlot()){
                    if (appointment.getHasPatientBeenContacted()){
                        appointment.setHasPatientBeenContacted(Boolean.FALSE);
                    }
                    else{
                        appointment.setHasPatientBeenContacted(Boolean.TRUE);
                    }
                    fireTableCellUpdated(row, col);
                }
            }
        }    
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Appointment appointment = getAppointments().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (appointment == null){
                    return null;
                }
                else{
                    //LocalDateTime start = appointment.getData().getStart();
                    //long minutes = appointment.getData().getDuration().toMinutes();
                    //Duration duration = appointment.getData().getDuration();
                    LocalDateTime start = appointment.getStart();
                    long minutes = appointment.getDuration().toMinutes();
                    Duration duration = appointment.getDuration();
                    Boolean contactStatus = appointment.getHasPatientBeenContacted();
                    switch (column){
                        case Patient:
                            result = appointment.getPatient();
                            break;
                        case From:
                            result = start.toLocalTime();
                            break;
                        case To:
                            result = start.plusMinutes(duration.toMinutes()).toLocalTime();
                            break;
                        case Duration:
                            result = duration;
                            break;
                        case Treatment:
                            result = /*28/03/2024appointment.getPatientNote()*/""; 
                            result = appointment.getNotes();
                            break;
                        case Reminded:
                            result = contactStatus;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }
    
}
