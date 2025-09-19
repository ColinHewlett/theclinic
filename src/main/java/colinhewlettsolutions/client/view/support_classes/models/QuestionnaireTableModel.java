/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.models;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import colinhewlettsolutions.client.model.entity.Question;

/**
 *
 * @author colin
 */
public class QuestionnaireTableModel extends DefaultTableModel{

    private ArrayList<Question> question = null;
    private enum COLUMN{Question};
    private final Class[] columnClass = new Class[] {
        String.class};

    public QuestionnaireTableModel(){
        question = new ArrayList<>();
        
    }
    
    public ArrayList<Question> getQuestions(){
        return this.question;
    }
    
    public void addElement(Question q){
        question.add(q);
    }
    
    public void removeAllElements(){
        question.clear();
    }
    
    public Question getElementAt(int row){
        return question.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (question!=null) result = question.size();
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
        Question question = getQuestions().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (question == null){
                    return null;
                }
                else{
                    String description = question.getDescription();
                    switch (column){
                        case Question:
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
