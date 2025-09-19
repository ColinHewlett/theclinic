/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.non_entity;

import java.util.ArrayList;
import colinhewlettsolutions.client.model.entity.Question;

/**
 *
 * @author colin
 */
public class QuestionWithState {

    public QuestionWithState(){
        
    }
    
    public QuestionWithState(Question question){
        this.question = question;
    }
    
    private ArrayList<QuestionWithState> collection = new ArrayList<>();
    public ArrayList<QuestionWithState> get(){
        return collection;
    }
    public void set(ArrayList<QuestionWithState> value){
        collection = value;
    }
    
    private Question question = null;
    public Question getQuestion(){
        return question;
    }
    public void setQuestion(Question value){
        question = value;
    }
    
    private Boolean state = false;
    public Boolean getState(){
        return state;
    }
    public void setState(Boolean value){
        state = value;
    }
    
    private String answer = null;
    public String getAnswer(){
        return answer;
    }
    public void setAnswer(String value){
        answer = value;
    }

}
