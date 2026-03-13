/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.entity;

import theclinic.model.entity.Patient;
import theclinic.model.entity.Entity;
import java.awt.Point;
import java.util.ArrayList;
import theclinic.model.repository.StoreException;
import theclinic.model.entity.interfaces.IEntityRepositoryActions;

/**
 *
 * @author colin
 */
public class PatientQuestion extends Entity implements IEntityRepositoryActions {
    
    public PatientQuestion(){
        this.setIsPatientQuestion(true);
    }
    
    public PatientQuestion(Patient patient){
        this.patient = patient;
        this.setIsPatientQuestion(true);
    }
    
    public PatientQuestion(Patient patient, Question question){
        this.patient = patient;
        this.question = question;
        this.setIsPatientQuestion(true);
    }
    
    private ArrayList<PatientQuestion> collection = new ArrayList<>();
    public ArrayList<PatientQuestion> get(){
        return collection;
    }
    public void set(ArrayList<PatientQuestion> value){
        collection = value;
    }
    
    private Patient patient = null;
    public Patient getPatient(){
        return patient;
    }
    public void setPatient(Patient value){
        patient = value;
    }
    
    private Question question = null;
    public Question getQuestion(){
        return question;
    }
    public void setQuestion(Question value){
        question = value;
    }
    
    private String answer = null;
    public String getAnswer(){
        return answer;
    }
    public void setAnswer(String value){
        answer = value;
    }
    
    @Override
    public Point count() throws StoreException{
        return getRepository().count(this);
    }
    
    @Override
    public void create() throws StoreException{
        getRepository().create(this);
    }
    
    @Override
    public void delete() throws StoreException{
        getRepository().delete(this);
    }
    
    @Override
    public void drop() throws StoreException{

    }
    
    @Override
    public Integer insert() throws StoreException{
        return getRepository().insert(this);
    }
    
    @Override
    public PatientQuestion read() throws StoreException{
        PatientQuestion pq = getRepository().read(this);
        return pq;
    }
    
    @Override
    public void update() throws StoreException{
        getRepository().update(this);
    }
}
