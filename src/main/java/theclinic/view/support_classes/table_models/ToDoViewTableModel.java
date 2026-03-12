/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import theclinic.model.entity.ToDo;

/**
 *
 * @author colin
 */
public class ToDoViewTableModel extends DefaultTableModel{
    public enum ViewMode{
        WITH_DATE,
        WITHOUT_DATE
    }
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ViewMode viewMode = null;
    private ArrayList<ToDo> toDoActions = null;
    private enum COLUMN_WITH_DATE{Actioned, Date, Description};
    private enum COLUMN_WITHOUT_DATE{Actioned, Description};
    private final Class[] withDateColumnClass = new Class[] {
        Boolean.class,
        LocalDate.class,
        String.class};
    private final Class[] withoutDateColumnClass = new Class[] {
        Boolean.class,
        String.class};

    public ToDoViewTableModel(){
        toDoActions = new ArrayList<>();
    }
    
    public ToDoViewTableModel(ViewMode value){
        viewMode = value;
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
        int result = 0;
        switch (viewMode){
            case WITH_DATE ->{
                result = COLUMN_WITH_DATE.values().length;
                break;
            }
            case WITHOUT_DATE ->{
                result = COLUMN_WITHOUT_DATE.values().length;
                break;
            }
        }
        return result;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        switch(viewMode){
            case WITH_DATE ->{
                for (COLUMN_WITH_DATE column: COLUMN_WITH_DATE.values()){
                    if (column.ordinal() == columnIndex){
                        result = column.toString();
                        if (result.equals("Actioned"))
                            result = result + "?";
                    }
                }
                break;
            }
            case WITHOUT_DATE ->{
                for (COLUMN_WITHOUT_DATE column: COLUMN_WITHOUT_DATE.values()){
                    if (column.ordinal() == columnIndex){
                        result = column.toString();
                        if (result.equals("Actioned"))
                            result = result + "?";  
                    }
                }
                break;
            }
        }
        return result;
    }
            
    @Override
    public Class<?> getColumnClass(int columnIndex){
        Class<?> result = null;
        switch(viewMode){
            case WITH_DATE ->{
                result = withDateColumnClass[columnIndex];
                break;
            }
            case WITHOUT_DATE ->{
                result = withoutDateColumnClass[columnIndex];
                break;
            }
        }
        return result;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col==0){
            if ((Boolean)value){
                toDoActions.get(row).setIsActioned(Boolean.TRUE);
            }
            else {
                toDoActions.get(row).setIsActioned(Boolean.FALSE);
            }
            fireTableCellUpdated(row, col);
        }   
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        LocalDate date = null;
        String description = null;
        Boolean actionedStatus = null;
        ToDo toDo = (ToDo)getToDo().get(row);
        if (toDo == null){
            result = null;
        }
        else{
            date = toDo.getDate();
            description = toDo.getDescription();
            actionedStatus = toDo.getIsActioned();
        }
        if (toDo!=null){
            switch(viewMode){
                case WITH_DATE ->{
                    for (COLUMN_WITH_DATE column: COLUMN_WITH_DATE.values()){
                        if (column.ordinal() == columnIndex){ 
                            switch (column){
                                case Actioned ->{
                                    result = actionedStatus;
                                    break;
                                }
                                case Date ->{
                                    result = date;
                                    break;
                                }
                                case Description ->{
                                    result = description;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case WITHOUT_DATE ->{
                    for (COLUMN_WITHOUT_DATE column: COLUMN_WITHOUT_DATE.values()){
                        if (column.ordinal() == columnIndex){ 
                            switch (column){
                                case Actioned ->{
                                    result = actionedStatus;
                                    break;
                                }
                                case Description ->{
                                    result = description;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }
}
