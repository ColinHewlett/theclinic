/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import theclinic.model.entity.Treatment;

/**
 *
 * @author colin
 */
public class TreatmentTableModel extends DefaultTableModel{

    private ArrayList<Treatment> treatment = null;
    private enum COLUMN{Treatment};
    private final Class[] columnClass = new Class[] {
        String.class};

    public TreatmentTableModel(){
        treatment = new ArrayList<>();
        
    }
    
    public ArrayList<Treatment> getTreatments(){
        return this.treatment;
    }
    
    public void addElement(Treatment c){
        treatment.add(c);
    }
    
    public void removeAllElements(){
        treatment.clear();
    }
    
    public Treatment getElementAt(int row){
        return treatment.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (treatment!=null) result = treatment.size();
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
            TreatmentWithState treatmentWithState = treatment.get(row);
            if (treatmentWithState.getState()){
                treatmentWithState.setState(Boolean.FALSE);
            }
            else{
                treatmentWithState.setState(Boolean.TRUE);
            }
            fireTableCellUpdated(row, col);
        }    
    }
*/
    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Treatment treatment = getTreatments().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (treatment == null){
                    return null;
                }
                else{
                    String description = treatment.getDescription();
                    switch (column){
                        case Treatment:
                            result = description;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }
    
}
