/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.view_support_classes.models;

import model.entity.Appointment;
import model.entity.Invoice;
/*28/03/2024import model.PatientNote;*/
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


/**
 *
 * @author colin
 * update to remove statically defined data structure
 * -- replaces extension  from DefaultTableModel to AbstractTableModel
 */
public class PatientAppointmentHistoryTableModel extends AbstractTableModel{
    public ArrayList<Appointment> appointments = new ArrayList<>();
    private enum COLUMN{Inv,From,Duration,Treatment};
    private final Class[] columnClass = new Class[] {
        Invoice.class,
        LocalDateTime.class, 
        Duration.class,
        /*28/03/2024PatientNote*/String.class};
    
    public ArrayList<Appointment> getAppointments(){
        return appointments;
    }
    
    public void addElement(Appointment a){
        appointments.add(a);
    }
    
    public Appointment getElementAt(int row){
        return appointments.get(row);
    }
    
    public void removeAllElements(){
        appointments.clear();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount(){
        return getAppointments().size();
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
                if(columnIndex == 0)result = ("Inv");
                else result = column.toString();
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
                    switch (column){
                        case Inv:
                            result = appointment.getInvoice();
                            break;
                        case Duration:
                            result = appointment.getDuration();
                            break;
                        case From:
                            result = appointment.getStart();
                            break;
                        case Treatment:
                            //result = /*28/03/2024appointment.getPatientNote()*/"";
                            result = appointment.getNotes();
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }

}
