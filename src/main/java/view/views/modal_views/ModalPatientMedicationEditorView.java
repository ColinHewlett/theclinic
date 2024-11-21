/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.modal_views;

import model.entity.Patient;
import model.entity.Medication;
import model.entity.Entity;
import model.*;
import controller.ViewController;
import view.view_support_classes.models.MedicationTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Iterator;
import javax.swing.JTable;
import view.View;
import view.views.non_modal_views.DesktopView;
import view.view_support_classes.models.MedicalHistoryTableModel;

/**
 *
 * @author colin
 */
public class ModalPatientMedicationEditorView extends ModalView 
        implements ActionListener, 
                   PropertyChangeListener, 
                   ListSelectionListener{
    private JTable tblMedication = null;

    /**
     * Creates new form ModalPatientMedicationEditorView
     */
    public ModalPatientMedicationEditorView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        Patient patient = getMyController().getDescriptor()
                .getControllerDescription().getPatient();
        Medication medication = (Medication)getMyController()
                .getDescriptor().getControllerDescription().getMedication();
        setViewMode(ViewMode.CREATE);
        setTitle(patient.toString() + " medication");
        
        this.btnAddUpdateMedication.setActionCommand(Action.REQUEST_MEDICATION_ADDITION_OR_UPDATE.toString());
        this.btnRemoveMedication.setActionCommand(Action.REQUEST_MEDICATION_REMOVAL.toString());
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        btnAddUpdateMedication.addActionListener(this);
        btnRemoveMedication.addActionListener(this);
        btnCloseView.addActionListener(this); 
        
        initialiseTable();
        populateTable(medication);
    }
    
    private void populateTable(Medication medication){
        MedicationTableModel model = (MedicationTableModel)tblMedication.getModel();
        model.removeAllElements();
        Iterator<Medication> iterator = medication.get().iterator();
        while (iterator.hasNext()){
            model.addElement(iterator.next());
        }
       // tblMedication.repaint();
       setVisible(true);
    }
    
    private void initialiseTable(){
        
        tblMedication = new JTable();
        tblMedication.setModel(new MedicationTableModel());
        ListSelectionModel lsm = tblMedication.getSelectionModel();
        lsm.addListSelectionListener(this);
        this.scrMedicationTable.setViewportView(tblMedication);
        tblMedication.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblMedication.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblMedication.isRowSelected(selectedRow))
                    tblMedication.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch(propertyName){
            
            case CLOSE_VIEW_REQUEST_RECEIVED:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            
        }
    }
    
    private boolean tableValueChangedListenerActivated = false;
    /**
     * on entry either
     * -- a new table row has been selected
     * -- or a selected row has been deselected
     * @param e 
     */
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
            int selectedRow = tblMedication.getSelectedRow();
            if (selectedRow!=-1){
                tableValueChangedListenerActivated = true;
                setViewMode(ViewMode.UPDATE);
            }
            else { 
                setViewMode(ViewMode.CREATE);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        MedicationTableModel model = 
                (MedicationTableModel)tblMedication.getModel();
        Medication medication = null;
        String reply = null;
        ActionEvent actionEvent = null;
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                getMyController().getDescriptor().
                                        getViewDescription().setMedication(medication);
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            case REQUEST_MEDICATION_ADDITION_OR_UPDATE:
                switch(getViewMode()){
                    case CREATE:
                        medication = new Medication();
                        getMyController().getDescriptor().
                                        getViewDescription().setMedication(medication);
                        actionEvent = new ActionEvent(
                                    this,ActionEvent.ACTION_PERFORMED,
                                    ViewController.PatientViewControllerActionEvent.
                                            PATIENT_MEDICATION_CREATE_REQUEST.toString());
                                this.getMyController().actionPerformed(actionEvent);
                        break;
                    case UPDATE:
                        int selectedRow = tblMedication.getSelectedRow();
                        if (selectedRow != -1){
                            medication = (Medication)model.getElementAt(selectedRow);
                            getMyController().getDescriptor().
                                        getViewDescription().setMedication(medication);
                            actionEvent = new ActionEvent(
                                    this,ActionEvent.ACTION_PERFORMED,
                                    ViewController.PatientViewControllerActionEvent.
                                            PATIENT_MEDICATION_UPDATE_REQUEST.toString());
                                this.getMyController().actionPerformed(actionEvent);
                        }else{
                            String message = "No medicene selected so cannot update the selection";
                            JOptionPane.showInternalConfirmDialog(this,message, 
                                    "View error", JOptionPane.WARNING_MESSAGE);  
                        }
                        break;
                }
                
                
                break;        
            case REQUEST_MEDICATION_REMOVAL: {       
                int selectedRow = tblMedication.getSelectedRow();
                if (selectedRow!=-1){
                    medication = (Medication)model.getElementAt(selectedRow);
                    getMyController().getDescriptor().getViewDescription().setMedication(medication);
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.PatientViewControllerActionEvent.
                                PATIENT_MEDICATION_DELETE_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }
                else{
                    String message = "No medicene selected so cannot remove the selection";
                            JOptionPane.showInternalConfirmDialog(this,message, 
                                    "View error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
    }
    
    
    private Entity getChanges(){
        return null;
    }
    
    private ViewMode viewMode = null;
    private ViewMode getViewMode(){
        return viewMode;
    }
    private void setViewMode(ViewMode value){
        String s = null;
        viewMode = value;
        switch(viewMode){
            case CREATE:
                s = btnAddUpdateMedication.getText();
                s = s.replace("Update", "Add");
                s = s.replace("selection", "medication");
                btnAddUpdateMedication.setText(s);
                break;
            case UPDATE:
                s = btnAddUpdateMedication.getText();
                s = s.replace("Add", "Update");
                s = s.replace("medication", "selection");
                btnAddUpdateMedication.setText(s);
                break;
        }
    }
    
    enum Action{
        REQUEST_MEDICATION_ADDITION_OR_UPDATE,
        REQUEST_MEDICATION_REMOVAL,
        REQUEST_CLOSE_VIEW
    }
    
    enum ViewMode{ CREATE, UPDATE}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        scrMedicationTable = new javax.swing.JScrollPane();
        btnAddUpdateMedication = new javax.swing.JButton();
        btnRemoveMedication = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        btnAddUpdateMedication.setText("<html><center>Add</center><center>medication</center></html>");
        btnAddUpdateMedication.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        btnAddUpdateMedication.setMinimumSize(new java.awt.Dimension(79, 40));
        btnAddUpdateMedication.setPreferredSize(new java.awt.Dimension(79, 40));
        btnAddUpdateMedication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddUpdateMedicationActionPerformed(evt);
            }
        });

        btnRemoveMedication.setText("<html><center>Remove</center><center>selection</center></html>");
        btnRemoveMedication.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        btnRemoveMedication.setMinimumSize(new java.awt.Dimension(79, 40));
        btnRemoveMedication.setPreferredSize(new java.awt.Dimension(79, 40));

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");
        btnCloseView.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        btnCloseView.setMinimumSize(new java.awt.Dimension(79, 40));
        btnCloseView.setPreferredSize(new java.awt.Dimension(79, 40));
        btnCloseView.setSelected(true);
        btnCloseView.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrMedicationTable, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .addComponent(btnRemoveMedication, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddUpdateMedication, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrMedicationTable, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAddUpdateMedication, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnRemoveMedication, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        btnCloseView.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddUpdateMedicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUpdateMedicationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddUpdateMedicationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUpdateMedication;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnRemoveMedication;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane scrMedicationTable;
    // End of variables declaration//GEN-END:variables
}
