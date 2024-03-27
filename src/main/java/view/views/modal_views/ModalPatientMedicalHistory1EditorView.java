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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JLabel;
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
public class ModalPatientMedicalHistory1EditorView extends ModalView 
        implements ActionListener, TableModelListener, ListSelectionListener{
    private JTable tblConditions = null;
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientMedicalHistory1EditorView (
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
        Patient patient = getMyController()
                .getDescriptor()
                .getControllerDescription().getPatient();
        setTitle(patient.toString() + ": medical history questionnaire");
        setVisible(true);
        
        this.btnEditNotes.setActionCommand(Action.REQUEST_MEDICAL_HISTORY_NOTES.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnEditNotes.addActionListener(this);
        btnCloseView.addActionListener(this); 
        
        initialiseTable();
        
        populateConditionsTable(getMyController()
                .getDescriptor().getControllerDescription().getCondition());
    }
    
    private void populateConditionsTable(Condition condition){
        MedicalHistoryTableModel model = (MedicalHistoryTableModel)tblConditions.getModel();
        model.removeAllElements();
        Iterator<Condition> iterator = condition.get().iterator();
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
        this.tblConditions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblConditions.getSelectionModel();
        lsm.addListSelectionListener(this);
        this.btnEditNotes.setText("<html><center>Edit</center><center>notes for</center><center>selection</center></html>");
        
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
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblConditions.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                MedicalHistoryTableModel model = 
                        (MedicalHistoryTableModel)tblConditions.getModel();
                PrimaryCondition pc = 
                        (PrimaryCondition) model.getElementAt(selectedRow);

                if (!pc.getSecondaryCondition().get().isEmpty()){
                    getMyController().getDescriptor().getViewDescription().setCondition(pc);
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.
                                PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }else if(!pc.getState()) tblConditions.clearSelection();
            }
        }
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        ActionEvent actionEvent = null;
        Condition condition = null;
        int row = e.getFirstRow();
        int column = e.getColumn();
        MedicalHistoryTableModel model =  
                (MedicalHistoryTableModel)e.getSource();
        Boolean value = (Boolean)model.getValueAt(row, column);
        condition = (Condition)model.getElementAt(row);
        condition.setState(value);
        getMyController().getDescriptor().getViewDescription().setCondition(condition);
        tblConditions.clearSelection();

        actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.PatientViewControllerActionEvent.CONDITION_STATE_UPDATE_REQUEST.toString());
        getMyController().actionPerformed(actionEvent);
        
        if (condition.getState()){
            PrimaryCondition pCondition = (PrimaryCondition)condition;
            if (!pCondition.getSecondaryCondition().get().isEmpty()){
                actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.PATIENT_MEDICAL_HISTORY_2_EDITOR_VIEW_REQUEST.toString());
                getMyController().actionPerformed(actionEvent);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ActionEvent actionEvent = null;
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                doSaveEditorChanges();
                try{
                    ModalPatientMedicalHistory1EditorView.this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            
            case REQUEST_MEDICAL_HISTORY_SECONDARY_VIEW:
                break;
            case REQUEST_MEDICAL_HISTORY_NOTES:
                int selectedRow = tblConditions.getSelectedRow();
                if(selectedRow!=-1){
                    MedicalHistoryTableModel model = 
                            (MedicalHistoryTableModel)tblConditions.getModel(); 
                    PrimaryCondition pc = 
                            (PrimaryCondition)model.getElementAt(selectedRow);
                    getMyController().getDescriptor().getViewDescription().setCondition(pc);
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
            case PATIENT_MEDICAL_HISTORY_RECEIVED:
                populateConditionsTable(getMyController()
                .getDescriptor().getControllerDescription().getCondition());
                break;
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

        jLabel1 = new javax.swing.JLabel();
        pnlRoot = new javax.swing.JPanel();
        scrConditionsTable = new javax.swing.JScrollPane();
        btnEditNotes = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

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
            .addComponent(scrConditionsTable)
            .addGroup(pnlRootLayout.createSequentialGroup()
                .addComponent(btnEditNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel pnlRoot;
    private javax.swing.JScrollPane scrConditionsTable;
    // End of variables declaration//GEN-END:variables

    enum  Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_CONDITION_STATE_UPDATE,
        REQUEST_MEDICAL_HISTORY_NOTES,
        REQUEST_MEDICAL_HISTORY_SECONDARY_VIEW,;
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
                .addComponent(btnEditNotes, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addGap(1)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
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
