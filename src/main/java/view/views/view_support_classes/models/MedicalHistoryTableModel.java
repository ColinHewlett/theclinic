/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import model.entity.Condition;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.*;

/**
 *
 * @author colin
 */
public class MedicalHistoryTableModel extends DefaultTableModel{

    private ArrayList<Condition> conditions = null;
    private enum COLUMN{YesNo, Description};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        String.class};

    public MedicalHistoryTableModel(){
        conditions = new ArrayList<>();
        
    }
    
    public ArrayList<Condition> getConditions(){
        return this.conditions;
    }
    
    public void addElement(Condition c){
        conditions.add(c);
    }
    
    public void removeAllElements(){
        conditions.clear();
    }
    
    public Condition getElementAt(int row){
        return conditions.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (conditions!=null) result = conditions.size();
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
            Condition condition = conditions.get(row);
            //checkslot is booked out to a patient
            /*
            if (condition.getState()){
                condition.setState(Boolean.FALSE);
            }
            else{
                condition.setState(Boolean.TRUE);
            }
*/
            fireTableCellUpdated(row, col);
        }    
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Condition condition = getConditions().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (condition == null){
                    return null;
                }
                else{
                    Boolean state = /*condition.getState();*/null;
                    String description = condition.getDescription();
                    switch (column){
                        case YesNo:
                            result = state;
                            break;
                        case Description:
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
