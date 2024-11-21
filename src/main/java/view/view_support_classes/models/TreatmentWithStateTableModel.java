/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.non_entity.TreatmentWithState;

/**
 *
 * @author colin
 */
public class TreatmentWithStateTableModel extends DefaultTableModel{

    private ArrayList<TreatmentWithState> treatmentWithStates = null;
    private enum COLUMN{YesNo, Treatment};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        String.class};

    public TreatmentWithStateTableModel(){
        treatmentWithStates = new ArrayList<>();
        
    }
    
    public ArrayList<TreatmentWithState> getTreatmentWithStates(){
        return this.treatmentWithStates;
    }
    
    public void addElement(TreatmentWithState c){
        treatmentWithStates.add(c);
    }
    
    public void removeAllElements(){
        treatmentWithStates.clear();
    }
    
    public TreatmentWithState getElementAt(int row){
        return treatmentWithStates.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (treatmentWithStates!=null) result = treatmentWithStates.size();
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
                if (result.equals("YesNo"))
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
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col==0){
            TreatmentWithState treatmentWithState = treatmentWithStates.get(row);
            if (treatmentWithState.getState()){
                treatmentWithState.setState(Boolean.FALSE);
            }
            else{
                treatmentWithState.setState(Boolean.TRUE);
            }
            fireTableCellUpdated(row, col);
        }    
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        TreatmentWithState treatmentWithState = getTreatmentWithStates().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (treatmentWithState == null){
                    return null;
                }
                else{
                    Boolean state = treatmentWithState.getState();
                    String description = treatmentWithState.getTreatment().getDescription();
                    switch (column){
                        case YesNo:
                            result = state;
                            break;
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
