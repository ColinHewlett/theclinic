/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.controller.PatientQuestionnaireViewController;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.support_classes.models.PatientQuestionnaireTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.JTable;
import colinhewlettsolutions.client.model.non_entity.QuestionWithState;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.controller.SystemDefinition;

/**
 *
 * @author colin
 */
public class PatientQuestionnaireView extends View 
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
    public PatientQuestionnaireView(
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
        REQUEST_DISPLAY_QUESTIONNAIRE_QUESTION,
        REQUEST_DISPLAY_PATIENT_ANSWER,
        REQUEST_UPLOAD_PATIENT_ANSWER;
    }
    
    public enum QAMode{
        QUESTION_VIEW,
        ANSWER_VIEW;
    }
    
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
        PatientQuestionnaireViewController.Properties propertyName =
                PatientQuestionnaireViewController.Properties.valueOf(ex.getPropertyName());
        switch (propertyName){
            case QUESTION_WITH_STATE_RECEIVED:
                QuestionWithState qws = (QuestionWithState)getMyController().getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.QUESTION_WITH_STATE);
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
        PatientQuestionnaireTableModel model =  
                (PatientQuestionnaireTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        qws = (QuestionWithState)model.getElementAt(row);
        tblQuestionnaire.clearSelection();
        setQuestionWithState(qws);
        if (!qws.getQuestion().getOrder().equals(2)){
            if (value) //if state == true add new PatietQuestion to storage
                doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_CREATE_REQUEST);
            else // else delete this patient question from storage
                doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_DELETE_REQUEST);
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
            int selectedRow = tblQuestionnaire.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                PatientQuestionnaireTableModel model = 
                        (PatientQuestionnaireTableModel)tblQuestionnaire.getModel();
                QuestionWithState qws = 
                        (QuestionWithState) model.getElementAt(selectedRow);
                switch(getQAMode()){
                    case ANSWER_VIEW:
                        txaSelectedQuestionnaireItem.setText(qws.getAnswer());
                        break;
                    case QUESTION_VIEW:
                        txaSelectedQuestionnaireItem.setText(qws.getQuestion().getDescription());
                        break;
                }
                
                if (qws.getState() && (!qws.getQuestion().getOrder().equals(2))){
                    this.btnDeletePatientAnswer.setEnabled(true);
                    this.btnCancelEditPatientAnswer.setEnabled(true);
                }else{ 
                    this.btnDeletePatientAnswer.setEnabled(false);
                    this.btnCancelEditPatientAnswer.setEnabled(false);
                }
            }else{
                this.btnDeletePatientAnswer.setEnabled(false);
                this.btnCancelEditPatientAnswer.setEnabled(false);
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
                doSendActionEvent(PatientQuestionnaireViewController.Actions.VIEW_CLOSE_NOTIFICATION);
                break;
                
            case REQUEST_DELETE_PATIENT_ANSWER:
                qws = getSelectedQuestionWithState();     
                if (qws!=null){
                    qws.setAnswer(null);
                    setQuestionWithState(qws);
                    doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                    if (getIsViewControllerErrorReceived()) 
                        doSendViewControllerError(action);
                    else doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_READ_REQUEST);
                }else{
                    isError = true;
                    error = "Patient reply has not been defined;/n"
                                + "Action to delete patient reply aborted";
                }
                break;
            case REQUEST_DISPLAY_PATIENT_ANSWER:
                setQAMode(QAMode.ANSWER_VIEW);               
                break;
            case REQUEST_DISPLAY_QUESTIONNAIRE_QUESTION:
                setQAMode(QAMode.QUESTION_VIEW);
                break;
            case REQUEST_CANCEL_EDIT_PATIENT_ANSWER:
                switch(getAnswerEditMode()){
                    case ON:
                        setAnswerEditMode(AnswerEditMode.OFF);
                        break;
                    case OFF:
                        setAnswerEditMode(AnswerEditMode.ON);
                        break;
                }
                break;
            case REQUEST_UPLOAD_PATIENT_ANSWER:
                qws = getSelectedQuestionWithState();     
                if (qws!=null){//shouldn't be null at this stage
                    qws.setAnswer(this.txaSelectedQuestionnaireItem.getText());
                    setQuestionWithState(qws);
                    doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                    if (getIsViewControllerErrorReceived()) 
                        doSendViewControllerError(action);
                    else doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_READ_REQUEST);
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
        Patient patient = (Patient)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        setTitle(patient.toString() + " patient questionnaire");
        btnCancelEditPatientAnswer.setActionCommand(Action.REQUEST_CANCEL_EDIT_PATIENT_ANSWER.toString());
        btnDeletePatientAnswer.setActionCommand(Action.REQUEST_DELETE_PATIENT_ANSWER.toString());
        btnUploadPatientAnswer.setActionCommand(Action.REQUEST_UPLOAD_PATIENT_ANSWER.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnCancelEditPatientAnswer.addActionListener(this);
        btnDeletePatientAnswer.addActionListener(this);
        this.btnUploadPatientAnswer.addActionListener(this);
        btnCloseView.addActionListener(this);
        
        this.btnDeletePatientAnswer.setEnabled(false);
        this.btnCancelEditPatientAnswer.setEnabled(false);
        this.btnCancelEditPatientAnswer.setText(this.UPLOAD_ANSWER_MODE_TITLE);
        
        
        rdbGroup.add(rdbShowSelectedAnswer);
        rdbGroup.add(this.rdbShowSelectedQuestion);
        rdbShowSelectedAnswer.setActionCommand(Action.REQUEST_DISPLAY_PATIENT_ANSWER.toString());
        rdbShowSelectedQuestion.setActionCommand(Action.REQUEST_DISPLAY_QUESTIONNAIRE_QUESTION.toString());
        rdbShowSelectedAnswer.addActionListener(this);
        rdbShowSelectedQuestion.addActionListener(this);
        rdbShowSelectedQuestion.doClick();
        
        setAnswerEditMode(AnswerEditMode.OFF);
        
        PatientQuestionnaireTableModel model = 
                (PatientQuestionnaireTableModel)tblQuestionnaire.getModel();
        model.addTableModelListener(this);
        
        
        this.pnlQuestionAnswerViewer.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                        "Question & answer viewer", 
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                        getBorderTitleFont(), 
                        getBorderTitleColor())); // NOI18N
        
        doSendActionEvent(PatientQuestionnaireViewController.Actions.PATIENT_QUESTION_READ_REQUEST);
        
    }
    
    private PatientQuestionnaireTableModel initialiseTable(){
        
        tblQuestionnaire = new JTable(new PatientQuestionnaireTableModel());
        scrQuestionnaireTable.setViewportView(tblQuestionnaire);
        ViewController.setJTableColumnProperties(
                tblQuestionnaire, scrQuestionnaireTable.getPreferredSize().width, 10, 10,80);
        ListSelectionModel lsm = this.tblQuestionnaire.getSelectionModel();
        lsm.addListSelectionListener(this);
        tblQuestionnaire.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblQuestionnaire.rowAtPoint(e.getPoint());
                    if (/*selectedRow!=-1 && */tblQuestionnaire.isRowSelected(selectedRow))
                    tblQuestionnaire.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        return (PatientQuestionnaireTableModel)tblQuestionnaire.getModel();
    }
    
    private void doSendActionEvent(PatientQuestionnaireViewController.Actions actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private QuestionWithState getSelectedQuestionWithState(){
        QuestionWithState result = null;
        int selectedRow = 0;
        selectedRow = this.tblQuestionnaire.getSelectedRow();
        if (selectedRow!=-1){
            PatientQuestionnaireTableModel model = 
                    (PatientQuestionnaireTableModel)tblQuestionnaire.getModel();
            result = model.getElementAt(selectedRow);
        }
        return result;
    }
    
    private AnswerEditMode answerEditMode = null;
    private void setAnswerEditMode(AnswerEditMode value){
        answerEditMode = value;
        if (value.equals(AnswerEditMode.ON)) {
            this.btnUploadPatientAnswer.setEnabled(true);
            this.btnCancelEditPatientAnswer.setText(CANCEL_ANSWER_MODE_TITLE);
            this.txaSelectedQuestionnaireItem.setEditable(true);
            if (this.rdbShowSelectedQuestion.isSelected()) this.rdbShowSelectedAnswer.doClick();
        }else{
            this.btnUploadPatientAnswer.setEnabled(false);
            this.btnCancelEditPatientAnswer.setText(this.UPLOAD_ANSWER_MODE_TITLE);
            this.txaSelectedQuestionnaireItem.setEditable(false);
            //next code re-displays original answer content
     
            //this.rdbShowSelectedQuestion.doClick();
            //this.rdbShowSelectedAnswer.doClick();
            //this.rdbShowSelectedQuestion.doClick();
        }    
    }
    private AnswerEditMode getAnswerEditMode(){
        return answerEditMode;
    }
    
    private QAMode qaMode = null;
    private void setQAMode(QAMode value){
        qaMode = value;
        this.txaSelectedQuestionnaireItem.setText("");
        QuestionWithState qws = getSelectedQuestionWithState();
        if (qws!=null) {
            switch(value){
                case ANSWER_VIEW:
                    txaSelectedQuestionnaireItem.setText(qws.getAnswer());
                    break;
                case QUESTION_VIEW:
                    txaSelectedQuestionnaireItem.setText(qws.getQuestion().getDescription());
                    break;
            }
        } 
    }
    private QAMode getQAMode(){
        return qaMode;
    }
    
    private void setQuestionWithState(QuestionWithState qws){
        getMyController().getDescriptor()
                .getViewDescription().setProperty(SystemDefinition.Properties.QUESTION_WITH_STATE, qws);
    }
    private QuestionWithState getQuestionWithState(){
        return (QuestionWithState)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.QUESTION_WITH_STATE);
    }
    
    private void populateQuestionnaireTable(QuestionWithState qws){
        PatientQuestionnaireTableModel model = initialiseTable();
        tblQuestionnaire.getModel().addTableModelListener(this);
        Iterator it = qws.get().iterator();
        while(it.hasNext()){
            QuestionWithState questionWithState = (QuestionWithState)it.next();
            model.addElement(questionWithState);
        }
        tblQuestionnaire.clearSelection();
    }
    
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
                this.tblQuestionnaire.clearSelection();  
            }else JOptionPane.showInternalMessageDialog(
                            this, "Update of patient reply cannot be blank",
                    "View error", JOptionPane.WARNING_MESSAGE); 
        }

        return result;
    }

    private boolean isViewControllerErrorReceived = false;
    private void setIsViewControllerErrorReceived(boolean value){
        isViewControllerErrorReceived = value;
    }
    private boolean getIsViewControllerErrorReceived(){
        return isViewControllerErrorReceived;
    }
    
    private void doSendViewControllerError(PatientQuestionnaireView.Action action){
        String message = (String)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.ERROR);
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

        rdbGroup = new javax.swing.ButtonGroup();
        pnlOperations = new javax.swing.JPanel();
        btnCancelEditPatientAnswer = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnDeletePatientAnswer = new javax.swing.JButton();
        scrQuestionnaireTable = new javax.swing.JScrollPane();
        tblQuestionnaire = new javax.swing.JTable();
        pnlQuestionAnswerViewer = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        rdbShowSelectedQuestion = new javax.swing.JRadioButton();
        rdbShowSelectedAnswer = new javax.swing.JRadioButton();
        pnlQustionAnswerViewer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaSelectedQuestionnaireItem = new javax.swing.JTextArea();
        btnUploadPatientAnswer = new javax.swing.JButton();

        pnlOperations.setBackground(new java.awt.Color(220, 220, 220));
        pnlOperations.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCancelEditPatientAnswer.setText("<html><center>Edit</center><center>patient</center><center>reply</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        btnDeletePatientAnswer.setText("<html><center>Delete</center><center>patient </center><center>reply</center></html>");
        btnDeletePatientAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePatientAnswerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCloseView)
                    .addComponent(btnCancelEditPatientAnswer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(btnDeletePatientAnswer, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnCancelEditPatientAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnDeletePatientAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblQuestionnaire.setModel(new PatientQuestionnaireTableModel(
        ));
        scrQuestionnaireTable.setViewportView(tblQuestionnaire);
        ViewController.setJTableColumnProperties(
            tblQuestionnaire, scrQuestionnaireTable.getPreferredSize().width, 10, 10,80);

        pnlQuestionAnswerViewer.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Questio & Answer viewer"), "Question & answer viewer"));

        rdbShowSelectedQuestion.setText("show selected question");

        rdbShowSelectedAnswer.setText("show selected answer");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rdbShowSelectedQuestion)
                .addGap(65, 65, 65)
                .addComponent(rdbShowSelectedAnswer)
                .addGap(63, 63, 63))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbShowSelectedQuestion)
                    .addComponent(rdbShowSelectedAnswer))
                .addContainerGap())
        );

        txaSelectedQuestionnaireItem.setColumns(20);
        txaSelectedQuestionnaireItem.setLineWrap(true);
        txaSelectedQuestionnaireItem.setRows(5);
        txaSelectedQuestionnaireItem.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaSelectedQuestionnaireItem);
        txaSelectedQuestionnaireItem.setEditable(false);

        btnUploadPatientAnswer.setText("<html><center>Upload</center><center>patient</center><center>answer</center></html>");

        javax.swing.GroupLayout pnlQustionAnswerViewerLayout = new javax.swing.GroupLayout(pnlQustionAnswerViewer);
        pnlQustionAnswerViewer.setLayout(pnlQustionAnswerViewerLayout);
        pnlQustionAnswerViewerLayout.setHorizontalGroup(
            pnlQustionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQustionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUploadPatientAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlQustionAnswerViewerLayout.setVerticalGroup(
            pnlQustionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQustionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlQustionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUploadPatientAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlQuestionAnswerViewerLayout = new javax.swing.GroupLayout(pnlQuestionAnswerViewer);
        pnlQuestionAnswerViewer.setLayout(pnlQuestionAnswerViewerLayout);
        pnlQuestionAnswerViewerLayout.setHorizontalGroup(
            pnlQuestionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuestionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQuestionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlQustionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlQuestionAnswerViewerLayout.setVerticalGroup(
            pnlQuestionAnswerViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuestionAnswerViewerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlQustionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlQuestionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrQuestionnaireTable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrQuestionnaireTable, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlQuestionAnswerViewer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeletePatientAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePatientAnswerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeletePatientAnswerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEditPatientAnswer;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeletePatientAnswer;
    private javax.swing.JButton btnUploadPatientAnswer;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlQuestionAnswerViewer;
    private javax.swing.JPanel pnlQustionAnswerViewer;
    private javax.swing.ButtonGroup rdbGroup;
    private javax.swing.JRadioButton rdbShowSelectedAnswer;
    private javax.swing.JRadioButton rdbShowSelectedQuestion;
    private javax.swing.JScrollPane scrQuestionnaireTable;
    private javax.swing.JTable tblQuestionnaire;
    private javax.swing.JTextArea txaSelectedQuestionnaireItem;
    // End of variables declaration//GEN-END:variables
}
