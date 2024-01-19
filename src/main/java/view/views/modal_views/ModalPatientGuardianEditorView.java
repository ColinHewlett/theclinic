/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import model.Patient;
import controller.ViewController;
import java.awt.event.ActionEvent;
import view.views.non_modal_views.PatientView;
import java.beans.PropertyVetoException;
import view.View;
import view.views.non_modal_views.DesktopView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author colin
 */
public class ModalPatientGuardianEditorView extends ModalView{
    
    private Patient getGuardian(){
        if (cmbSelectGuardian.getSelectedIndex() == -1){
            return null;
        }
        else {
            return (Patient)cmbSelectGuardian.getSelectedItem();
        }
    }
    private void setGuardian(Patient guardian){
        if (guardian == null){
            this.cmbSelectGuardian.setSelectedIndex(-1);
            this.cmbSelectGuardian.setEnabled(false);
        }
    }
    
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientGuardianEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient guardian editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setTitle("Patient guardian editor");
        setVisible(true);
        addListeners();
        populatePatientSelector(); 
        if (getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getPatient()
                .getIsGuardianAPatient()){
            this.cmbSelectYesNo.setSelectedItem(PatientView.YesNoItem.Yes);
            Patient guardian = getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getPatient()
                .getGuardian();
            if (guardian != null){
                this.cmbSelectGuardian.setSelectedItem(guardian);
            }else this.cmbSelectGuardian.setSelectedItem(-1);
        }else {
            this.cmbSelectYesNo.setSelectedItem(PatientView.YesNoItem.No);
            this.cmbSelectGuardian.setSelectedItem(-1);
        }
    }
    
    private void populatePatientSelector(){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                getMyController().getDescriptor().getControllerDescription().getPatients();
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        cmbSelectGuardian.setModel(model);
        cmbSelectGuardian.setSelectedIndex(-1);
    }
    
    private void addListeners(){
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try{
                    ModalPatientGuardianEditorView.this.setClosed(true);
                }
                catch (PropertyVetoException ex){

                }
            }
        });
        btnSaveGuardianDetails.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveEditorChanges();
                try{
                    ModalPatientGuardianEditorView.this.setClosed(true);
                }
                catch (PropertyVetoException ex){

                }
            }
        });
    }
    
    private void doSaveEditorChanges(){
        Patient patient = 
                getMyController()
                        .getDescriptor()
                        .getControllerDescription()
                        .getPatient();
        if (cmbSelectYesNo.getSelectedItem().equals(PatientView.YesNoItem.Yes)){
            patient.setIsGuardianAPatient(true);
            if (cmbSelectGuardian.getSelectedIndex() != -1){
                patient.setGuardian((Patient)cmbSelectGuardian.getSelectedItem());
            }else patient.setGuardian(null);
        }else {
            patient.setIsGuardianAPatient(false);
            patient.setGuardian(null);
        }  
        getMyController()
            .getDescriptor()
            .getViewDescription()
            .setPatient(patient);
        ActionEvent actionEvent = new ActionEvent(
            ModalPatientGuardianEditorView.this,ActionEvent.ACTION_PERFORMED, 
            ViewController
                    .PatientViewControllerActionEvent
                    .PATIENT_EDITOR_VIEW_CHANGE
                    .toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void initComponents() {

        lblIsGuardianAPatient = new javax.swing.JLabel();
        cmbSelectYesNo = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        cmbSelectGuardian = new javax.swing.JComboBox<Patient>();
        btnSaveGuardianDetails = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        lblIsGuardianAPatient.setText("Guardian is a patient ?");

        cmbSelectYesNo.setModel(new javax.swing.DefaultComboBoxModel<>());
        cmbSelectYesNo.addItem(PatientView.YesNoItem.Yes);
        cmbSelectYesNo.addItem(PatientView.YesNoItem.No);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Select guardian from patients"));

        //cmbSelectGuardian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbSelectGuardian, 0, 286, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbSelectGuardian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnSaveGuardianDetails.setText("Save");

        btnCloseView.setText("Close");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblIsGuardianAPatient, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(cmbSelectYesNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(btnSaveGuardianDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseView)
                .addGap(66, 66, 66))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIsGuardianAPatient)
                    .addComponent(cmbSelectYesNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveGuardianDetails)
                    .addComponent(btnCloseView))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnSaveGuardianDetails;
    private javax.swing.JComboBox<Patient> cmbSelectGuardian;
    private javax.swing.JComboBox<PatientView.YesNoItem> cmbSelectYesNo;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblIsGuardianAPatient;
    // End of variables declaration      
}
