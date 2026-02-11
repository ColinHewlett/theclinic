/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.models;

import colinhewlettsolutions.client.view.views.non_modal_views.ScheduleView;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import colinhewlettsolutions.client.model.entity.Appointment;
import colinhewlettsolutions.client.model.entity.Patient;


/**
 *
 * @author colin
 */
public class ScheduleListTableModel extends DefaultTableModel{
    private ArrayList<Appointment> data = null;
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
        data = new ArrayList<>();
        
    }

    public ArrayList<Appointment> getAppointments(){
        return this.data;
    }

    public void setData(ArrayList<Appointment> newData) {
        this.data = newData;
        fireTableDataChanged(); // Notify JTable
    }
    
    public void removeAllElements(){
        data.clear();
        fireTableDataChanged();
    }
    
    public Appointment getElementAt(int row){
        return data.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (data!=null) result = data.size();
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
            Appointment appointment = data.get(row);
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
