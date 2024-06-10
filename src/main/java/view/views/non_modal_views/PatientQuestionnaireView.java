/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;

import controller.ViewController;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_ANSWER_UPDATE_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_CREATE_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_DELETE_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_READ_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.PATIENT_QUESTION_ANSWER_UPDATE_REQUEST;
import static controller.ViewController.PatientQuestionnaireViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION;
import static controller.ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_CHANGE_NOTIFICATION;
import static controller.ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.PATIENT_QUESTIONNAIRE_VIEW_CONTROLLER_ERROR_RECEIVED;
import static controller.ViewController.PatientQuestionnaireViewControllerPropertyChangeEvent.QUESTION_WITH_STATE_RECEIVED;
import model.*;
import view.View;
import view.views.view_support_classes.models.PatientQuestionnaireTableModel;
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
import model.QuestionWithState;
import model.Patient;
import model.non_entity.SystemDefinition;

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
    
    public enum Action{
        REQUEST_EDIT_PATIENT_REPLY,
        REQUEST_DELETE_PATIENT_REPLY,
        REQUEST_SHOW_SELECTED_QUESTION,
        REQUEST_CLOSE_VIEW;
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
        PatientQuestionnaireTableModel model =  
                (PatientQuestionnaireTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        qws = (QuestionWithState)model.getElementAt(row);
        tblQuestionnaire.clearSelection();
        setQuestionWithState(qws);
        if (!qws.getQuestion().getOrder().equals(2)){
            if (value) //if state == true add new PatietQuestion to storage
                doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_CREATE_REQUEST);
            else // else delete this patient question from storage
                doSendActionEvent(ViewController.PatientQuestionnaireViewControllerActionEvent
                        .PATIENT_QUESTION_DELETE_REQUEST);
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
                txaSelectedQuestion.setText(qws.getQuestion().getDescription());
                if (qws.getState() && (!qws.getQuestion().getOrder().equals(2))){
                    this.btnDeletePatientReply.setEnabled(true);
                    this.btnEditPatientReply.setEnabled(true);
                }else{ 
                    this.btnDeletePatientReply.setEnabled(false);
                    this.btnEditPatientReply.setEnabled(false);
                }
            }else{
                this.btnDeletePatientReply.setEnabled(false);
                this.btnEditPatientReply.setEnabled(false);
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
                
            case REQUEST_DELETE_PATIENT_REPLY:
                qws = getSelectedCondition();     
                if (qws!=null){
                    qws.setAnswer(null);
                    setQuestionWithState(qws);
                    doSendActionEvent(PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                    if (getIsViewControllerErrorReceived()) 
                        doSendViewControllerError(action);
                    else doSendActionEvent(PATIENT_QUESTION_READ_REQUEST);
                }else{
                    isError = true;
                    error = "Patient reply has not been defined;/n"
                                + "Action to delete patient reply aborted";
                }
                break;

            case REQUEST_EDIT_PATIENT_REPLY:
                qws = getSelectedCondition();     
                if (qws!=null){
                    if (doRequestQuestionPatientReplyUpdateEditor(qws)!=null){
                        setQuestionWithState(qws);
                        doSendActionEvent(PATIENT_QUESTION_ANSWER_UPDATE_REQUEST);
                        if (getIsViewControllerErrorReceived()) 
                            doSendViewControllerError(action);
                        else doSendActionEvent(PATIENT_QUESTION_READ_REQUEST);
                    }
                }
                break;
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
        btnEditPatientReply.setActionCommand(Action.REQUEST_EDIT_PATIENT_REPLY.toString());
        btnDeletePatientReply.setActionCommand(Action.REQUEST_DELETE_PATIENT_REPLY.toString());
        //btnShowSelectedQuestion.setActionCommand(Action.REQUEST_SHOW_SELECTED_QUESTION.toString());
        btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnEditPatientReply.addActionListener(this);
        btnDeletePatientReply.addActionListener(this);
        //btnShowSelectedQuestion.addActionListener(this);
        btnCloseView.addActionListener(this);
        this.btnDeletePatientReply.setEnabled(false);
        this.btnEditPatientReply.setEnabled(false);
        
        
        PatientQuestionnaireTableModel model = 
                (PatientQuestionnaireTableModel)tblQuestionnaire.getModel();
        model.addTableModelListener(this);
        
        doSendActionEvent(ViewController
                .PatientQuestionnaireViewControllerActionEvent
                .PATIENT_QUESTION_READ_REQUEST);
        
    }
    
    private PatientQuestionnaireTableModel initialiseTable(){
        
        tblQuestionnaire = new JTable(new PatientQuestionnaireTableModel());
        scrQuestionnaireTable.setViewportView(tblQuestionnaire);
        ViewController.setJTableColumnProperties(
                tblQuestionnaire, scrQuestionnaireTable.getPreferredSize().width, 5, 10,85);
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
    
    private void doSendActionEvent(ViewController.
        PatientQuestionnaireViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private QuestionWithState getSelectedCondition(){
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
    
    private void setQuestionWithState(QuestionWithState qws){
        getMyController().getDescriptor()
                .getViewDescription().setQuestionWithState(qws);
    }
    private QuestionWithState getQuestionWithState(){
        return getMyController().getDescriptor()
                .getControllerDescription().getQuestionWithState();
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

        pnlOperations = new javax.swing.JPanel();
        btnEditPatientReply = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnDeletePatientReply = new javax.swing.JButton();
        scrQuestionnaireTable = new javax.swing.JScrollPane();
        tblQuestionnaire = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaSelectedQuestion = new javax.swing.JTextArea();

        pnlOperations.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnEditPatientReply.setText("<html><center>Edit</center><center>patient reply</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        btnDeletePatientReply.setText("<html><center>Delete</center><center>patient reply</center></html>");
        btnDeletePatientReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePatientReplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEditPatientReply, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDeletePatientReply))
                .addContainerGap())
        );
        pnlOperationsLayout.setVerticalGroup(
            pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnEditPatientReply, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(btnDeletePatientReply, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tblQuestionnaire.setModel(new PatientQuestionnaireTableModel(
        ));
        scrQuestionnaireTable.setViewportView(tblQuestionnaire);
        ViewController.setJTableColumnProperties(
            tblQuestionnaire, scrQuestionnaireTable.getPreferredSize().width, 5, 10,85);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("<html><center>Select above</center<center> a question</center>to display its</center<center>content</center></html>");

        txaSelectedQuestion.setColumns(20);
        txaSelectedQuestion.setLineWrap(true);
        txaSelectedQuestion.setRows(5);
        txaSelectedQuestion.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaSelectedQuestion);
        txaSelectedQuestion.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrQuestionnaireTable, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrQuestionnaireTable, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeletePatientReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletePatientReplyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeletePatientReplyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDeletePatientReply;
    private javax.swing.JButton btnEditPatientReply;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JScrollPane scrQuestionnaireTable;
    private javax.swing.JTable tblQuestionnaire;
    private javax.swing.JTextArea txaSelectedQuestion;
    // End of variables declaration//GEN-END:variables
}
