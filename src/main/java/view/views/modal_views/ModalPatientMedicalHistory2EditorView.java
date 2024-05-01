/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.modal_views;

import controller.ViewController;
import model.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import view.View;
import view.views.non_modal_views.DesktopView;
import view.views.non_modal_views.ScheduleView;
import view.views.view_support_classes.models.MedicalHistoryTableModel;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;

/**
 *
 * @author colin
 */
public class ModalPatientMedicalHistory2EditorView extends ModalView 
        implements ActionListener, TableModelListener, ListSelectionListener{
    private JTable tblConditions = null;
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientMedicalHistory2EditorView (
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient medical history primary view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        initComponentsx();
        Patient patient = getMyController().getDescriptor()
                .getControllerDescription().getPatient();
        PrimaryCondition primaryCondition = (PrimaryCondition)getMyController()
                .getDescriptor().getControllerDescription().getCondition();
        setParentPrimaryCondition(primaryCondition);
        
        setTitle(patient.toString() + ": " + primaryCondition.getDescription() + " questionnaire");
        setVisible(true);
        
        this.btnEditNotes.setActionCommand(Action.REQUEST_MEDICAL_HISTORY_NOTES.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnEditNotes.addActionListener(this);
        btnCloseView.addActionListener(this); 
        
        initialiseTable();
        populateConditionsTable(getParentPrimaryCondition());
    }
    
    private void populateConditionsTable(PrimaryCondition pCondition){
        MedicalHistoryTableModel model = (MedicalHistoryTableModel)tblConditions.getModel();
        model.removeAllElements();
        pCondition.getSecondaryCondition().get();
        Iterator<Condition> iterator = pCondition.getSecondaryCondition().get().iterator();
        while (iterator.hasNext()){
            model.addElement(iterator.next());
        }
    }
    
    private boolean tableValueChangedListenerActivated = false;
    private void initialiseTable(){
        tblConditions = new JTable();
        tblConditions.setModel(new MedicalHistoryTableModel());
        tblConditions.getModel().addTableModelListener(this);
        this.scrConditionsTable.setViewportView(tblConditions);
        ViewController.setJTableColumnProperties(
                tblConditions, scrConditionsTable.getPreferredSize().width, 20,70);
        ListSelectionModel lsm = this.tblConditions.getSelectionModel();
        lsm.addListSelectionListener(this);
        tblConditions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblConditions.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblConditions.isRowSelected(selectedRow))
                    tblConditions.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        Condition condition = null;
        int row = e.getFirstRow();
        int column = e.getColumn();
        MedicalHistoryTableModel model =  
                (MedicalHistoryTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        condition = (Condition)model.getElementAt(row);
        //condition.setState(value);
        getMyController().getDescriptor().getViewDescription().setCondition(condition);
        tblConditions.clearSelection();

        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.CONDITION_STATE_UPDATE_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private PrimaryCondition parentPrimaryCondition = null;
    private PrimaryCondition getParentPrimaryCondition(){
        return parentPrimaryCondition;
    }
    private void setParentPrimaryCondition(PrimaryCondition value){
        parentPrimaryCondition = value;
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblConditions.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                MedicalHistoryTableModel model = 
                        (MedicalHistoryTableModel)tblConditions.getModel();
                SecondaryCondition sc = 
                        (SecondaryCondition) model.getElementAt(selectedRow);
                
                //if(!sc.getState()) tblConditions.clearSelection();
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        MedicalHistoryTableModel model = null;
        ActionEvent actionEvent = null;
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                doSaveEditorChanges();
                /**
                 * -- check if any secondary is ticked
                 * -- if a secondary condition ticked and parent primary condition not ticked
                 * ---- set parent condition true and send CONDITION_STATE_UPDATE_REQUEST to VC
                 */ 
                boolean tickedStateFound = false;
                model = (MedicalHistoryTableModel)tblConditions.getModel();
                for(Condition condition : model.getConditions()){
                    /*
                    if (condition.getState()){
                        tickedStateFound = true;
                        break;
                    }*/
                }
                if (tickedStateFound){
                    PrimaryCondition pc = getParentPrimaryCondition();
                    /*if (!pc.getState()){
                        pc.setState(true);
                        getMyController().getDescriptor()
                                .getViewDescription().setCondition(pc);
                        actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                            ViewController.PatientViewControllerActionEvent
                                    .CONDITION_STATE_UPDATE_REQUEST.toString());
                        this.getMyController().actionPerformed(actionEvent);
                    }*/
                }
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            case REQUEST_MEDICAL_HISTORY_NOTES:
                PrimaryCondition pc = getParentPrimaryCondition();
                int selectedRow = tblConditions.getSelectedRow();
                if (selectedRow!=-1){
                    model = (MedicalHistoryTableModel)tblConditions.getModel();
                    SecondaryCondition sCondition = (SecondaryCondition)model.getElementAt(selectedRow);
                    sCondition.setPrimaryCondition(getParentPrimaryCondition());
                    getMyController().getDescriptor().getViewDescription().setCondition(sCondition);
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.
                                PATIENT_MEDICAL_HISTORY_NOTE_TAKER_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }
                break;
        }
    }
    
     @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            case MAKE_VIEW_INVISIBLE:
                this.setVisible(false);
                break;
            case MAKE_VIEW_VISIBLE:
                this.setVisible(true);
                this.tblConditions.clearSelection();
                break;
            case CLOSE_VIEW_REQUEST_RECEIVED:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
        }
    }
    
    private void doSaveEditorChanges(){
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlRoot = new javax.swing.JPanel();
        scrConditionsTable = new javax.swing.JScrollPane();
        btnEditNotes = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        btnEditNotes.setText("<html><center>Edit</center><center>notes for</center><center>selection</center></html>");
        btnEditNotes.setMaximumSize(new java.awt.Dimension(87, 25));
        btnEditNotes.setMinimumSize(new java.awt.Dimension(87, 25));
        btnEditNotes.setOpaque(false);
        btnEditNotes.setPreferredSize(new java.awt.Dimension(87, 25));

        btnCloseView.setText("Close view");
        btnCloseView.setMaximumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setMinimumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setPreferredSize(new java.awt.Dimension(87, 25));
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRootLayout = new javax.swing.GroupLayout(pnlRoot);
        pnlRoot.setLayout(pnlRootLayout);
        pnlRootLayout.setHorizontalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRootLayout.createSequentialGroup()
                .addComponent(scrConditionsTable, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        pnlRootLayout.setVerticalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addGroup(pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scrConditionsTable, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlRootLayout.createSequentialGroup()
                        .addComponent(btnEditNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRoot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseViewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnEditNotes;
    private javax.swing.JPanel pnlRoot;
    private javax.swing.JScrollPane scrConditionsTable;
    // End of variables declaration//GEN-END:variables

    enum  Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_CONDITION_STATE_UPDATE,
        REQUEST_MEDICAL_HISTORY_NOTES;
    }
    
    private void initComponentsx() {

        pnlRoot = new javax.swing.JPanel();
        scrConditionsTable = new javax.swing.JScrollPane();
        btnEditNotes = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        btnEditNotes.setText("<html><center>Edit</center><center>notes for</center><center>selection</center></html>");
        btnEditNotes.setMaximumSize(new java.awt.Dimension(87, 25));
        btnEditNotes.setMinimumSize(new java.awt.Dimension(87, 25));
        btnEditNotes.setOpaque(false);
        btnEditNotes.setPreferredSize(new java.awt.Dimension(87, 25));

        btnCloseView.setText("Close view");
        btnCloseView.setMaximumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setMinimumSize(new java.awt.Dimension(87, 25));
        btnCloseView.setPreferredSize(new java.awt.Dimension(87, 25));
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRootLayout = new javax.swing.GroupLayout(pnlRoot);
        pnlRoot.setLayout(pnlRootLayout);
        pnlRootLayout.setHorizontalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRootLayout.createSequentialGroup()
                .addComponent(scrConditionsTable, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEditNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addGap(0))
        );
        pnlRootLayout.setVerticalGroup(
            pnlRootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrConditionsTable)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addComponent(btnEditNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addGap(1)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRoot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlRoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

                                                


    // Variables declaration - do not modify                     
    //private javax.swing.JButton btnCloseView;
    //private javax.swing.JButton btnEditNotes;
    //private javax.swing.JPanel jPanel2;
    //private javax.swing.JPanel pnlOperations;
    //private javax.swing.JScrollPane scrConditionsTable;
    // End of variables declaration     
}
