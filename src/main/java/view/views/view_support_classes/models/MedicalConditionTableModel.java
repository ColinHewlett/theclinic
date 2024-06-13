/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.Condition;

/**
 *
 * @author colin
 */
public class MedicalConditionTableModel extends DefaultTableModel{

    private ArrayList<Condition> condition = null;
    private enum COLUMN{Condition};
    private final Class[] columnClass = new Class[] {
        String.class};

    public MedicalConditionTableModel(){
        condition = new ArrayList<>();
        
    }
    
    public ArrayList<Condition> getConditions(){
        return this.condition;
    }
    
    public void addElement(Condition c){
        condition.add(c);
    }
    
    public void removeAllElements(){
        condition.clear();
    }
    
    public Condition getElementAt(int row){
        return condition.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (condition!=null) result = condition.size();
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
                if (!getConditions().isEmpty()){
                    if (getElementAt(0).getIsPrimaryCondition())
                        result = "Condition (primary)";
                    else result = "Condition (secondary)";
                }
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
        Condition condition = getConditions().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (condition == null){
                    return null;
                }
                else{
                    String description = condition.getDescription();
                    switch (column){
                        case Condition:
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
