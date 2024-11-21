/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.non_entity.QuestionWithState;

/**
 *
 * @author colin
 */
public class PatientQuestionnaireTableModel extends DefaultTableModel {
    
    private ArrayList<QuestionWithState> questionWithStates = null;
    private enum COLUMN{Question, YesNo, PatientReply};
    private final Class[] columnClass = new Class[] {
        Integer.class,
        Boolean.class,
        String.class};

    public PatientQuestionnaireTableModel(){
        questionWithStates = new ArrayList<>();
        
    }
    
    public ArrayList<QuestionWithState> getQuestionWithStates(){
        return this.questionWithStates;
    }
    
    public void addElement(QuestionWithState c){
        questionWithStates.add(c); 
    }
    
    public void removeAllElements(){
        questionWithStates.clear();
    }
    
    public QuestionWithState getElementAt(int row){
        return questionWithStates.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (questionWithStates!=null) result = questionWithStates.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    
    public static String questionsColumnName = null;

    public String getQuestionsColumnName(){
        return questionsColumnName;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                if (result.equals("YesNo"))
                    result = result + " ?";  
                else if (result.equals("PatientReply"))
                    result = "Patient reply";
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
        if (col==1){
            QuestionWithState questionWithState = questionWithStates.get(row);
                if (!questionWithState.getQuestion().getOrder().equals(2)){
                    if (questionWithState.getState()){
                        questionWithState.setState(Boolean.FALSE);
                    }
                    else{
                        questionWithState.setState(Boolean.TRUE);
                    }
                }
                fireTableCellUpdated(row, col);
        }   
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        QuestionWithState questionWithState = getQuestionWithStates().get(row);
        
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (questionWithState == null){
                    return null;
                }
                else{
                    Boolean state = questionWithState.getState();
                    //String description = questionWithState.getQuestion().getDescription();
                    Integer order = questionWithState.getQuestion().getOrder();
                    String answer = questionWithState.getAnswer();
                    switch (column){
                        case YesNo:
                            result = state;
                            break;
                        case Question:
                            result = order;
                            break;
                        case PatientReply:
                            if (!state) result = "";
                            else result = answer;
                            
                    }
                    break;
                }
            }
        }
        return result;
    }
}
