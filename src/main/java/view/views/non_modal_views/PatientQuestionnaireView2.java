/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_ANSWER_UPDATE_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_READ_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION;
import static controller.ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_ERROR_RECEIVED;
import static controller.ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.QUESTION_WITH_STATE_RECEIVED;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import model.entity.Patient;
import model.non_entity.QuestionWithState;
import model.non_entity.SystemDefinition;
import view.View;
import view.views.view_support_classes.models.PatientQuestionTableModel;
import view.views.view_support_classes.models.PatientAnswerTableModel;

/**
 *
 * @author colin
 */
public class PatientQuestionnaireView2 extends View 
            implements ActionListener,
                       TableModelListener, 
                       ListSelectionListener,
                       PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientQuestionnaireView2(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }
    
    private final String CANCEL_ANSWER_MODE_TITLE = 
            "<html><center>Cancel</center><center>upload</center><center>answer</center></html>";;
    private final String UPLOAD_ANSWER_MODE_TITLE = 
            "<html><center>Edit</center><center>patient</center><center>answer</center></html>";
    
    public enum Action{
        REQUEST_CANCEL_EDIT_PATIENT_ANSWER,
        REQUEST_CLOSE_VIEW,
        REQUEST_DELETE_PATIENT_ANSWER,
        //REQUEST_DISPLAY_QUESTIONNAIRE_QUESTION,
        //REQUEST_DISPLAY_PATIENT_ANSWER,
        REQUEST_UPLOAD_PATIENT_ANSWER;
    }
    
    /*
    public enum QAMode{
        QUESTION_VIEW,
        ANSWER_VIEW;
    }*/
    
    public enum PatientAnswerMode{
        CANCEL,
        EDIT;
    }

    public enum AnswerEditMode{
        ON,
        OFF;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent ex){
        ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent
                        .valueOf(ex.getPropertyName());
        switch (propertyName){
            case QUESTION_WITH_STATE_RECEIVED:
                QuestionWithState qws = getMyController().getDescriptor()
                        .getControllerDescription().getQuestionWithState();
                populateQuestionnaireTable(qws);
                break;
            case PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_ERROR_RECEIVED:
                setIsViewControllerErrorReceived(true);
                break;
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        ActionEvent actionEvent = null;
        QuestionWithState qws = null;
        int row = e.getFirstRow();
        int column = e.getColumn();
        PatientQuestionTableModel model =  
                (PatientQuestionTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        qws = (QuestionWithState)model.getElementAt(row);
        this.tblQuestions.clearSelection();
        setQuestionWithState(qws);
        if (!qws.getQuestion().getOrder().equals(2)){
            if (value) //if state == true add new PatietQuestion to storage
                doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_CREATE_REQUEST);
            else // else delete this patient question from storage
                //doPatientQuestionDelete(qws);
                doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_DELETE_REQUEST);
            doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                .PATIENT_QUESTION_READ_REQUEST);    
        }else{
            String message = "Patient meds can only be defined using the 'Medical history' radio button\n"
                    + "on the Patient view, and then selecting the 'Medication' option";
            JOptionPane.showInternalMessageDialog(this, message, "View information",JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
    private boolean tableValueChangedListenerActivated = false;
    @Override
    public void valueChanged(ListSelectionEvent e){
       if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblQuestions.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                PatientQuestionTableModel model = 
                        (PatientQuestionTableModel)tblQuestions.getModel();
                QuestionWithState qws = 
                        (QuestionWithState) model.getElementAt(selectedRow);
                if (!qws.getState()){
                    this.txaSelectedQuestionnaireItem.setText("");
                    this.btnUploadPatientAnswer.setEnabled(false);
                    if(qws.getQuestion().getOrder().equals(2)){
                        this.txaSelectedQuestionnaireItem.setEditable(false);
                        this.txaSelectedQuestionnaireItem.setText(qws.getAnswer());
                    } 
                }else if (qws.getState() && (!qws.getQuestion().getOrder().equals(2))){
                    this.btnUploadPatientAnswer.setEnabled(true);
                    this.txaSelectedQuestionnaireItem.setEditable(true);
                    this.txaSelectedQuestionnaireItem.setText(qws.getAnswer());    
                }else if(qws.getState() && (qws.getQuestion().getOrder().equals(2))){ 
                    this.btnUploadPatientAnswer.setEnabled(false);
                    this.txaSelectedQuestionnaireItem.setEditable(false);
                    this.txaSelectedQuestionnaireItem.setText(qws.getAnswer());
                }else{
                    this.btnUploadPatientAnswer.setEnabled(false);
                    this.txaSelectedQuestionnaireItem.setEditable(false);
                    this.txaSelectedQuestionnaireItem.setText("");
                }
            }else{
                this.btnUploadPatientAnswer.setEnabled(false);
                this.txaSelectedQuestionnaireItem.setEditable(false);
                this.txaSelectedQuestionnaireItem.setText("");
            }
       }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        setIsViewControllerErrorReceived(false);
        boolean isError = false;
        String error = null;
        QuestionWithState qws = null;
        PatientQuestionnaireView.Action action =
                PatientQuestionnaireView.Action.valueOf(e.getActionCommand());
        switch(action){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                doSendActionEvent(VIEW_CLOSE_NOTIFICATION);
                break;
            /*    
            case REQUEST_DELETE_PATIENT_ANSWER:
                qws = getSelectedQuestionWithState();     
                if (qws!=null){
                    qws.setAnswer(null);
                    setQuestionWithState(qws);
                    doSendActionEvent(PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                    if (getIsViewControllerErrorReceived()) 
                        doSendViewControllerError(action);
                    else {
                        doSendActionEvent(PATIENT_QUESTION_READ_REQUEST);
                        this.txaSelectedQuestionnaireItem.setText("");
                    }
                }else{
                    isError = true;
                    error = "Patient reply has not been defined;/n"
                                + "Action to delete patient reply aborted";
                }
                break;
                */
            /*
            case REQUEST_DISPLAY_PATIENT_ANSWER:
                setQAMode(QAMode.ANSWER_VIEW);               
                break;
            case REQUEST_DISPLAY_QUESTIONNAIRE_QUESTION:
                setQAMode(QAMode.QUESTION_VIEW);
                break;*/
                /*
            case REQUEST_CANCEL_EDIT_PATIENT_ANSWER:
                switch(getAnswerEditMode()){
                    case ON:
                        setAnswerEditMode(AnswerEditMode.OFF);
                        break;
                    case OFF:
                        setAnswerEditMode(AnswerEditMode.ON);
                        break;
                }
                break;*/
            case REQUEST_UPLOAD_PATIENT_ANSWER:
                qws = getSelectedQuestionWithState();     
                if (qws!=null){//shouldn't be null at this stage
                    qws.setAnswer(this.txaSelectedQuestionnaireItem.getText());
                    setQuestionWithState(qws);
                    doSendActionEvent(PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                    if (getIsViewControllerErrorReceived()) 
                        doSendViewControllerError(action);
                    else doSendActionEvent(PATIENT_QUESTION_READ_REQUEST);
                    setAnswerEditMode(AnswerEditMode.OFF);
                }
                break;
               
                    /*
                    if (doRequestQuestionPatientReplyUpdateEditor(qws)!=null){
                        setQuestionWithState(qws);
                        doSendActionEvent(PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                        if (getIsViewControllerErrorReceived()) 
                            doSendViewControllerError(action);
                        else doSendActionEvent(PATIENT_QUESTION_READ_REQUEST);
                    }*/              
            
        }
        if (isError){
            JOptionPane.showInternalMessageDialog(
                    this,error,"View error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        Patient patient = getMyController().getDescriptor()
                .getControllerDescription().getPatient();
        setTitle(patient.toString() + " patient questionnaire");
        btnUploadPatientAnswer.setActionCommand(Action.REQUEST_UPLOAD_PATIENT_ANSWER.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnUploadPatientAnswer.addActionListener(this);
        btnCloseView.addActionListener(this);

        setAnswerEditMode(AnswerEditMode.OFF);
        
        PatientQuestionTableModel model = 
                (PatientQuestionTableModel)tblQuestions.getModel();
        model.addTableModelListener(this);
        
        
        this.pnlQuestionAnswerViewer.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Patient answer editor", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        SystemDefinition.TITLED_BORDER_FONT, 
                        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        doSendActionEvent(ViewController
                .PatientQuestionnaireViewControllerActionEvent
                .PATIENT_QUESTION_READ_REQUEST);
        
    }
    
    private void doSendActionEvent(ViewController.
        PatientQuestionnaireViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private QuestionWithState getSelectedQuestionWithState(){
        QuestionWithState result = null;
        int selectedRow = 0;
        selectedRow = this.tblQuestions.getSelectedRow();
        if (selectedRow!=-1){
            PatientQuestionTableModel model = 
                    (PatientQuestionTableModel)tblQuestions.getModel();
            result = model.getElementAt(selectedRow);
        }
        return result;
    }
    
    private AnswerEditMode answerEditMode = null;
    private void setAnswerEditMode(AnswerEditMode value){
        answerEditMode = value;
        if (value.equals(AnswerEditMode.ON)) {
            this.btnUploadPatientAnswer.setEnabled(true);
            this.txaSelectedQuestionnaireItem.setEditable(true);
            //if (this.rdbShowSelectedQuestion.isSelected()) this.rdbShowSelectedAnswer.doClick();
        }else{
            this.btnUploadPatientAnswer.setEnabled(false);
            this.txaSelectedQuestionnaireItem.setEditable(true);
        }    
    }
    private AnswerEditMode getAnswerEditMode(){
        return answerEditMode;
    }
    
    private void setQuestionWithState(QuestionWithState qws){
        getMyController().getDescriptor()
                .getViewDescription().setQuestionWithState(qws);
    }
    private QuestionWithState getQuestionWithState(){
        return getMyController().getDescriptor()
                .getControllerDescription().getQuestionWithState();
    }
    
    private void populateQuestionsTable(QuestionWithState qws){
        PatientQuestionTableModel model = initialiseQuestionsTable(); 
        tblQuestions.getModel().addTableModelListener(this);
        Iterator it = qws.get().iterator();
        while(it.hasNext()){
            QuestionWithState questionWithState = (QuestionWithState)it.next();
            model.addElement(questionWithState);
        }
        tblQuestions.clearSelection();
    }
    /*
    private void populateAnswersTable(QuestionWithState qws){
        PatientAnswerTableModel model = initialiseAnswersTable(); 
        tblAnswers.getModel().addTableModelListener(this);
        Iterator it = qws.get().iterator();
        while(it.hasNext()){
            QuestionWithState questionWithState = (QuestionWithState)it.next();
            model.addElement(questionWithState);
        }
        tblAnswers.clearSelection();
    }*/
    
    private void populateQuestionnaireTable(QuestionWithState qws){
        /*
        PatientQuestionnaireTableModel model = initialiseTable();
        tblQuestionnaire.getModel().addTableModelListener(this);
        Iterator it = qws.get().iterator();
        while(it.hasNext()){
            QuestionWithState questionWithState = (QuestionWithState)it.next();
            model.addElement(questionWithState);
        }
        tblQuestionnaire.clearSelection();*/
        populateQuestionsTable(qws);
        //populateAnswersTable(qws);
    }
    
    private PatientQuestionTableModel initialiseQuestionsTable(){
        tblQuestions = new JTable(new PatientQuestionTableModel());
        //scrQuestions.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrQuestions.setViewportView(tblQuestions);
        ViewController.setJTableColumnProperties(
                tblQuestions, scrQuestions.getPreferredSize().width, 15, 1, 75);
        TableColumn column = tblQuestions.getColumnModel().getColumn(1);
        column.setPreferredWidth(20);
        column.setMaxWidth(20);
        column.setMinWidth(20);
        ListSelectionModel lsm = this.tblQuestions.getSelectionModel();
        lsm.addListSelectionListener(this);
        tblQuestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblQuestions.rowAtPoint(e.getPoint());
                    if (/*selectedRow!=-1 && */tblQuestions.isRowSelected(selectedRow))
                    tblQuestions.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        return (PatientQuestionTableModel)tblQuestions.getModel();
    }
    
    /*
    private PatientAnswerTableModel initialiseAnswersTable(){
        tblAnswers = new JTable(new PatientAnswerTableModel());
        scrAnswers.setViewportView(tblAnswers);
        ViewController.setJTableColumnProperties(
                tblAnswers, scrAnswers.getPreferredSize().width, 100);
        ListSelectionModel lsm = this.tblAnswers.getSelectionModel();
        lsm.addListSelectionListener(this);
        tblAnswers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblAnswers.rowAtPoint(e.getPoint());
                    if (tblAnswers.isRowSelected(selectedRow))
                    tblAnswers.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        return (PatientAnswerTableModel)tblAnswers.getModel();
    }*/
    
/*
    private QuestionWithState doRequestQuestionPatientReplyUpdateEditor(QuestionWithState qws){
        QuestionWithState result = null;
        String patientReply = "";
        patientReply = JOptionPane.showInternalInputDialog(
                this,"Enter patient reply for question '" + qws.getQuestion().getOrder().toString() + "'. Only the first 100 characters of the reply will be saved.");
        if (patientReply!=null){
            patientReply = patientReply.trim();
            if (!patientReply.isEmpty()){
                if (patientReply.length() > SystemDefinition.QUESTIONNAIRE_REPLY_MAX_LENGTH)
                    patientReply = patientReply.substring(0, SystemDefinition.QUESTIONNAIRE_REPLY_MAX_LENGTH);
                qws.setAnswer(patientReply);
                result = qws;
                this.tblAnswers.clearSelection();  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Update of patient reply cannot be blank",
                    "View error", JOptionPane.WARNING_MESSAGE); 
        }

        return result;
    }*/
    
/*
    private void doPatientQuestionDelete(QuestionWithState qws){
        String[] options = {"Yes", "No"};
        int reply;
        if (qws.getAnswer()!=null){
            if (!(qws.getAnswer().trim().isEmpty())){
                reply = JOptionPane.showOptionDialog(this,
                        "Unticking this question will delete the patient answer to the question\nDo you want to continue anyway?",null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,null);
                if (reply==JOptionPane.YES_OPTION)
                    doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_DELETE_REQUEST);
            }else doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_DELETE_REQUEST);
        }else doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_DELETE_REQUEST);
    }*/

    private boolean isViewControllerErrorReceived = false;
    private void setIsViewControllerErrorReceived(boolean value){
        isViewControllerErrorReceived = value;
    }
    private boolean getIsViewControllerErrorReceived(){
        return isViewControllerErrorReceived;
    }
    
    private void doSendViewControllerError(PatientQuestionnaireView.Action action){
        String message = getMyController().getDescriptor()
                .getControllerDescription().getError();
        message = message +"\n" + "Raised in PatientQuestionnaireView.ActionPerformed(case = " + action.toString() + ")";
        JOptionPane.showInternalMessageDialog(this, message, 
                "View controller error", JOptionPane.WARNING_MESSAGE);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrQuestions = new javax.swing.JScrollPane();
        tblQuestions = new javax.swing.JTable();
        pnlOperations = new javax.swing.JPanel();
        btnCloseView = new javax.swing.JButton();
        btnUploadPatientAnswer = new javax.swing.JButton();
        pnlQuestionAnswerViewer = new javax.swing.JPanel();
        pnlQustionAnswerViewer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaSelectedQuestionnaireItem = new javax.swing.JTextArea();

        tblQuestions.setModel(new PatientQuestionTableModel(
        ));
        scrQuestions.setViewportView(tblQuestions);
        ViewController.setJTableColumnProperties(
            tblQuestions, scrQuestions.getPreferredSize().width, 10, 10,80);

        pnlOperations.setBackground(new java.awt.Color(220, 220, 220));
        pnlOperations.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        btnUploadPatientAnswer.setText("<html><center>Upload</center><center>patient</center><center>answer</center></html>");

        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlOperationsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnUploadPatientAnswer))
                .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(btnUploadPatientAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pnlQuestionAnswerViewer.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient answer editor"));

        txaSelectedQuestionnaireItem.setColumns(20);
        txaSelectedQuestionnaireItem.setLineWrap(true);
        txaSelectedQuestionnaireItem.setRows(5);
        txaSelectedQuestionnaireItem.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaSelectedQuestionnaireItem);
        txaSelectedQuestionnaireItem.setEditable(true);

        javax.swing.GroupLayout pnlQustionAnswerViewerLayout = new javax.swing.GroupLayout(pnlQustionAnswerViewer);
        pnlQustionAnswerViewer.setLayout(pnlQustionAnswerViewerLayout);
        pnlQustionAnswerViewerLayout.setHorizontalGroup(
            pnlQustionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQustionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        pnlQustionAnswerViewerLayout.setVerticalGroup(
            pnlQustionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQustionAnswerViewerLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout pnlQuestionAnswerViewerLayout = new javax.swing.GroupLayout(pnlQuestionAnswerViewer);
        pnlQuestionAnswerViewer.setLayout(pnlQuestionAnswerViewerLayout);
        pnlQuestionAnswerViewerLayout.setHorizontalGroup(
            pnlQuestionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuestionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlQustionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlQuestionAnswerViewerLayout.setVerticalGroup(
            pnlQuestionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuestionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(pnlQustionAnswerViewer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrQuestions, javax.swing.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
                    .addComponent(pnlQuestionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(scrQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(pnlQuestionAnswerViewer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );

        scrQuestions.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnUploadPatientAnswer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlQuestionAnswerViewer;
    private javax.swing.JPanel pnlQustionAnswerViewer;
    private javax.swing.JScrollPane scrQuestions;
    private javax.swing.JTable tblQuestions;
    private javax.swing.JTextArea txaSelectedQuestionnaireItem;
    // End of variables declaration//GEN-END:variables
}
