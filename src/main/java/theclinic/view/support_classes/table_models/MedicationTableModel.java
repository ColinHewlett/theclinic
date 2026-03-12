/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import theclinic.model.entity.Medication;

/**
 *
 * @author colin
 */
public class MedicationTableModel extends DefaultTableModel{

    private ArrayList<Medication> medications = null;
    private enum COLUMN{PrescribedMedication};
    private final Class[] columnClass = new Class[] {
        String.class};

    public MedicationTableModel(){
        medications = new ArrayList<>();
        
    }
    
    public ArrayList<Medication> getMedications(){
        return this.medications;
    }
    
    public void addElement(Medication m){
        medications.add(m);
    }
    
    public void removeAllElements(){
        medications.clear();
    }
    
    public Medication getElementAt(int row){
        return medications.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (medications!=null) result = medications.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        COLUMN.values();
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                if (result.equals("PrescribedMedication"))
                    result = "Prescribed medication";
                break;
            }
        }
        return result;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }
    
    /**
     * 
     * @param value the update the user made to the "Contact?" column content
     * -- the method determines the value of the cell before the update (true or false)
     * -- this is got by reading the source of this value that was read from persistent store
     * -- depending the value read from persistent store the cell value in the table is toggled
     * -- lastly an event is fired to inform listeners of the update; thus fireTableCellUpdated(row, col);
     * @param row defines the row which includes the cell
     * @param col defines the column which includes the cell
     */
    /*
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col==0){
            Condition condition = conditions.get(row);
            //checkslot is booked out to a patient
            if (condition.getState()){
                condition.setState(Boolean.FALSE);
            }
            else{
                condition.setState(Boolean.TRUE);
            }
            fireTableCellUpdated(row, col);
        }    
    }
    */
    
    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Medication medication = getMedications().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (medication == null){
                    return null;
                }
                else{
                    String med = medication.getDescription();
                    switch (column){
                        case PrescribedMedication:
                            result = med;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }
    
}
