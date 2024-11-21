/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.models;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.Appointment;
import model.entity.Patient;
import model.non_entity.Slot;

/**
 *
 * @author colin
 */
public class ScheduleDiaryEmergencyTableModel extends DefaultTableModel{
    private ArrayList<Appointment> appointments = null;
    private enum COLUMN{Slot, Duration, Patient};
    private final Class[] columnClass = new Class[] {
        LocalDateTime.class,
        Duration.class, 
        Patient.class,
        };

    /**
     * need to know the current patient (if any)
     */
    private Patient patient = null;
    private Patient getCurrentPatient(){
        return patient;
    }
    private void setCurrentPatient(Patient value){
        patient = value;
    }
    
    public ScheduleDiaryEmergencyTableModel(){
        appointments = new ArrayList<>();  
    }
    
    public ArrayList<Appointment> getAppointments(){
        return this.appointments;
    }
    
    public void addElement(Appointment appointment){
        appointments.add(appointment);
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
                    Duration duration = appointment.getDuration();
                    Patient patient = appointment.getPatient();
                    
                    switch (column){
                        case Slot:
                            result = start;
                            break;
                        case Patient:
                            /*
                            if(slot.getAppointment()!=null){ 
                                if(slot.getAppointment().getStart().toLocalTime().equals(slot.getStart().toLocalTime())){
                                    result = slot.getAppointment().getPatient();
                                }
                            }*/
                            result = patient;
                            break;
                    }

                }
            }
        }
        return result;
    }
}
