/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.*;
/**
 *
 * @author colin
 */
public class MedicalConditionWithStateTableModel extends DefaultTableModel {
    
    private ArrayList<ConditionWithState> conditionWithStates = null;
    private enum COLUMN{YesNo, Condition};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        String.class};

    public MedicalConditionWithStateTableModel(){
        conditionWithStates = new ArrayList<>();
        
    }
    
    public ArrayList<ConditionWithState> getConditionWithStates(){
        return this.conditionWithStates;
    }
    
    public void addElement(ConditionWithState c){
        conditionWithStates.add(c); 
    }
    
    public void removeAllElements(){
        conditionWithStates.clear();
    }
    
    public ConditionWithState getElementAt(int row){
        return conditionWithStates.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (conditionWithStates!=null) result = conditionWithStates.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    
    public static String conditionsColumnName = null;

    public String getConditionsColumnName(){
        return conditionsColumnName;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                if (result.equals("YesNo"))
                    result = result + " ?";  
                else if (result.equals("Condition")){
                    result = conditionsColumnName;
                }
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
            ConditionWithState conditionWithState = conditionWithStates.get(row);
            if (conditionWithState.getState()){
                conditionWithState.setState(Boolean.FALSE);
            }
            else{
                conditionWithState.setState(Boolean.TRUE);
            }
            fireTableCellUpdated(row, col);
        }   
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        PrimaryCondition pc = null;
        ConditionWithState conditionWithState = getConditionWithStates().get(row);
        
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (conditionWithState == null){
                    return null;
                }
                else{
                    Boolean state = conditionWithState.getState();
                    String description = conditionWithState.getCondition().getDescription();
                    switch (column){
                        case YesNo:
                            result = state;
                            break;
                        case Condition:
                            if (conditionWithState.getCondition().getIsPrimaryCondition()){
                                pc = (PrimaryCondition)conditionWithState.getCondition();
                                if (!pc.getSecondaryCondition().get().isEmpty())
                                    result = description + " ...";
                                else result = description;
                            }else result = description;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }
}
