/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.ToDo;

/**
 *
 * @author colin
 */
public class ToDoViewTableModel extends DefaultTableModel{
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ArrayList<ToDo> toDoActions = null;
    private enum COLUMN{Actioned, Date, Description};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        LocalDate.class,
        String.class};
    
    public ToDoViewTableModel(){
        toDoActions = new ArrayList<>();
    }
        
    public ArrayList<ToDo> getToDo(){
        return toDoActions;
    }
    
    public void addElement(ToDo toDo){
        toDoActions.add(toDo);
    }
    
    public ToDo getElementAt(int row){
        return toDoActions.get(row);
    }
    
    public void removeAllElements(){
        toDoActions.clear();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount(){
        int result;
        if (toDoActions!=null) result = toDoActions.size();
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
                if (result.equals("Actioned"))
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
            if ((Boolean)value)toDoActions.get(row).setIsActioned(Boolean.TRUE);
            else toDoActions.get(row).setIsActioned(Boolean.FALSE);
            fireTableCellUpdated(row, col);
        }   
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        ToDo toDo = (ToDo)getToDo().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (toDo == null){
                    return null;
                }
                else{
                    LocalDate date = toDo.getDate();
                    String description = toDo.getDescription();
                    Boolean actionedStatus = toDo.getIsActioned();
                    
                    switch (column){
                        case Actioned:
                            result = actionedStatus;
                            break;
                        case Date:
                            //result = date.format(ddmmyy);
                            result = date;
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
