/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import view.views.non_modal_views.DesktopView;
import model.*;
import repository.StoreException;
import view.View;


/**
 *
 * @author colin
 */
public class PatientQuestionnaireViewController extends ViewController{
    
    public PatientQuestionnaireViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ArrayList<QuestionWithState> QWS = new ArrayList<>();
        ActionEvent actionEvent = null;
        boolean isError = false;
        PatientQuestion pq = null;
        QuestionWithState qws = null;
        Patient patient = getDescriptor().getControllerDescription().getPatient();
        
        ViewController.PatientQuestionnaireViewControllerActionEvent actionCommand =
            ViewController.PatientQuestionnaireViewControllerActionEvent
                    .valueOf(e.getActionCommand());
        switch(actionCommand){
            case VIEW_CLOSE_NOTIFICATION:
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.DesktopViewControllerActionEvent.
                            VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                break;
            case PATIENT_QUESTION_ANSWER_UPDATE_REQUEST:
                qws = getDescriptor().getViewDescription().getQuestionWithState();
                if (qws!=null){
                    try{
                        pq = new PatientQuestion(patient, qws.getQuestion());
                        pq.setAnswer(qws.getAnswer());
                        pq.update();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "StoreException handled in "
                                + "PatientQuestionnaireViewController::actionPerformed("
                                + actionCommand.toString() + ")";
                        displayErrorMessage(message, 
                                "Patient questionnaire view controller error",
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                }
                break;
            case PATIENT_QUESTION_ANSWER_DELETE_REQUEST:
                break;
            case PATIENT_QUESTION_CREATE_REQUEST:
                qws = getDescriptor().getViewDescription().getQuestionWithState();
                if (qws!=null){
                    try{
                        pq = new PatientQuestion(patient, qws.getQuestion());
                        pq.insert();
                    }catch(StoreException ex){
                        String message = ex.getMessage() + "\n"
                                + "StoreException handled in "
                                + "PatientQuestionnaireViewController::actionPerformed("
                                + actionCommand.toString() + ")";
                        displayErrorMessage(message, 
                                "Patient questionnaire view controller error",
                                JOptionPane.WARNING_MESSAGE);
                        isError = true;
                    }
                }
                break;
            case PATIENT_QUESTION_DELETE_REQUEST:
                qws = getDescriptor().getViewDescription().getQuestionWithState();
                if (qws!=null){
                    pq = new PatientQuestion(patient, qws.getQuestion());
                    pq.setScope(Entity.Scope.SINGLE);
                    try{
                        pq.delete();
                    }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "PatientQuestionnaireViewController::actionPerformed("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Patient questionnaire view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                }
                break;
            case PATIENT_QUESTION_READ_REQUEST:
                try{
                    Question question = new Question();
                    question.setScope(Entity.Scope.ALL);
                    question = question.read();
                    for(Question q : question.get()){
                        qws = new QuestionWithState(q);
                        pq = new PatientQuestion(patient, q);
                        pq.setScope(Entity.Scope.SINGLE);
                        pq = pq.read();
                        if (pq!=null){
                            qws.setAnswer(pq.getAnswer());
                            qws.setState(true);
                        }
                        QWS.add(qws);
                    }
                    qws = new QuestionWithState();
                    qws.set(QWS);
                    this.getDescriptor().getControllerDescription()
                            .setQuestionWithState(qws);
                    firePropertyChangeEvent(
                            ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.
                                    QUESTION_WITH_STATE_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                }catch(StoreException ex){
                    String message = ex.getMessage() + "\n"
                            + "StoreException handled in "
                            + "PatientQuestionnaireViewController::actionPerformed("
                            + actionCommand.toString() + ")";
                    displayErrorMessage(message, 
                            "Patient questionnaire view controller error",
                            JOptionPane.WARNING_MESSAGE);
                    isError = true;
                }
                break;
                
        }
    }
}
