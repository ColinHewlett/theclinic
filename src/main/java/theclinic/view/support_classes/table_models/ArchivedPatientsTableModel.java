/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import theclinic.model.entity.Patient;
import theclinic.model.entity.PatientAppointmentData;
/**
 *
 * @author colin
 */
public class ArchivedPatientsTableModel extends DefaultTableModel{
    private ArrayList<PatientAppointmentData> data = null;

    private enum COLUMN{Patient,LastBooking};
    private final Class[] columnClass = new Class[] {
        Patient.class,
        LocalDate.class
        };

    public ArchivedPatientsTableModel(){
        data = new ArrayList<>();  
    }
    
    public ArrayList<PatientAppointmentData> getArchivedPatients(){
        return this.data;
    }
    
    public void setData(ArrayList<PatientAppointmentData> newData) {
        this.data = newData;
        fireTableDataChanged(); // Notify JTable
    }
    
    public void removeAllElements(){
        data.clear();
        fireTableDataChanged();
    }
    
    public PatientAppointmentData getElementAt(int row){
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
                if (result.equals("LastBooking"))
                    result = "<html><center>Last</center><center>booking</center></html>";
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
        PatientAppointmentData pad = getArchivedPatients().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (pad == null){
                    return null;
                }
                else{
                    switch (column){
                        case Patient -> {
                            result = pad.getPatient();
                            break;
                        }
                        case LastBooking -> {
                            if (pad.getAppointment() != null){
                                if (pad.getAppointment().getStart() != null){
                                    result = pad.getAppointment().getStart().toLocalDate();
                                }
                            }
                            
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }

}
