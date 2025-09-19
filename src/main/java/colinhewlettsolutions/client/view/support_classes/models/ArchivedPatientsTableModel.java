/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.models;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import colinhewlettsolutions.client.model.entity.Patient;
/**
 *
 * @author colin
 */
public class ArchivedPatientsTableModel extends DefaultTableModel{
    private ArrayList<Patient> data = null;

    private enum COLUMN{Patient};
    private final Class[] columnClass = new Class[] {
        Patient.class
        };

    public ArchivedPatientsTableModel(){
        data = new ArrayList<>();  
    }
    
    public ArrayList<Patient> getArchivedPatients(){
        return this.data;
    }
    
    public void setData(ArrayList<Patient> newData) {
        this.data = newData;
        fireTableDataChanged(); // Notify JTable
    }
    
    public void removeAllElements(){
        data.clear();
        fireTableDataChanged();
    }
    
    public Patient getElementAt(int row){
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
        Patient patient = getArchivedPatients().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (patient == null){
                    return null;
                }
                else{
                    switch (column){
                        case Patient:
                            result = patient;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }

}
