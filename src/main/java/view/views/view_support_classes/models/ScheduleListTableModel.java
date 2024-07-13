/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.Appointment;
import model.entity.Patient;


/**
 *
 * @author colin
 */
public class ScheduleListTableModel extends DefaultTableModel{
    private ArrayList<Appointment> appointments = null;
    private enum COLUMN{Confirmed, Patient, From,To,Duration,Treatment};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        Patient.class,
        LocalTime.class, 
        LocalTime.class, 
        Duration.class, 
        String.class,
        };

    public ScheduleListTableModel(){
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
                if (result.equals("Confirmed"))
                    result = result + "?";
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
        if (col==0){
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
                        case Confirmed:
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
