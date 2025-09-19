/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.controller;

import colinhewlettsolutions.client.model.entity.Question;
import colinhewlettsolutions.client.model.entity.PatientQuestion;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.model.entity.Entity;
import colinhewlettsolutions.client.model.non_entity.QuestionWithState;
import static colinhewlettsolutions.client.controller.ViewController.displayErrorMessage;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import colinhewlettsolutions.client.model.repository.StoreException;
import colinhewlettsolutions.client.view.View;


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
        Patient patient = (Patient)getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        
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
                qws = (QuestionWithState)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.QUESTION_WITH_STATE);
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
                qws = (QuestionWithState)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.QUESTION_WITH_STATE);
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
                qws = (QuestionWithState)getDescriptor().getViewDescription().getProperty(SystemDefinition.Properties.QUESTION_WITH_STATE);
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
                            .setProperty(SystemDefinition.Properties.QUESTION_WITH_STATE, qws);
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
