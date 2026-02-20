/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.PatientViewController;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.controller.ViewController;
import java.awt.event.ActionEvent;
import colinhewlettsolutions.client.view.views.non_modal_views.PatientView;
import java.beans.PropertyVetoException;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;

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
        initialiseViewOnOpening();
    }
    
    private Patient currentlySelectedPatient = null;
    private Patient getCurrentlySelectedPatient(){
        return currentlySelectedPatient;
    }
    private void setCurrentlySelectedPatient(Patient  value){
        currentlySelectedPatient = value;
    }
    
    private void initialiseViewOnOpening(){
        Patient guardian = null;
        setCurrentlySelectedPatient((Patient)getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getProperty(SystemDefinition.Properties.PATIENT));
        populatePatientSelector(); 
        //does this patient have a guardian who is a patient 
        if (getCurrentlySelectedPatient().getIsGuardianAPatient()){
            this.cmbSelectYesNo.setSelectedItem(PatientView.YesNoItem.Yes);
            guardian = getCurrentlySelectedPatient().getGuardian();
            if (guardian != null){
                this.cmbSelectGuardian.setSelectedItem(guardian);
            }else this.cmbSelectGuardian.setSelectedIndex(-1);
        }else this.cmbSelectYesNo.setSelectedItem(PatientView.YesNoItem.No);
    }
    
    private void populatePatientSelector(){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                (ArrayList<Patient>)getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.PATIENTS);
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        cmbSelectGuardian.setModel(model);
        cmbSelectGuardian.setSelectedIndex(-1);
    }
    
    private void addListeners(){
        /*
        this.cmbSelectGuardian.addActionListener(new java.awt.event.ActionListener(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                Patient patient = getCurrentlySelectedPatient();
                patient.setGuardian((Patient)cmbSelectGuardian.getSelectedItem());
                setCurrentlySelectedPatient(patient);    

            }
        });
        */
        this.cmbSelectYesNo.addActionListener(new java.awt.event.ActionListener(){
             @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (cmbSelectYesNo.getSelectedItem()
                        .equals(PatientView.YesNoItem.No)){
                    cmbSelectGuardian.setSelectedIndex(-1);
                    cmbSelectGuardian.setEnabled(false);
                    //getCurrentlySelectedPatient().setIsGuardianAPatient(false);
                }
                else if (cmbSelectYesNo.getSelectedItem()
                        .equals(PatientView.YesNoItem.Yes)){
                    Patient guardian = getCurrentlySelectedPatient().getGuardian();
                    if (guardian != null){
                        cmbSelectGuardian.setSelectedItem(guardian);
                    }else cmbSelectGuardian.setSelectedItem(-1);
                    cmbSelectGuardian.setEnabled(true);
                    //getCurrentlySelectedPatient().setIsGuardianAPatient(true);

                }
            }
        });

        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
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
        if (this.cmbSelectYesNo.getSelectedItem()
                .equals(PatientView.YesNoItem.Yes)){
            getCurrentlySelectedPatient().setIsGuardianAPatient(true);
            if (this.cmbSelectGuardian.getSelectedIndex()!=-1)
                getCurrentlySelectedPatient()
                        .setGuardian((Patient)cmbSelectGuardian.getSelectedItem());
            else {
                getCurrentlySelectedPatient().setGuardian(null);
                getCurrentlySelectedPatient().setIsGuardianAPatient(false);
            }
        }
        else {
            getCurrentlySelectedPatient().setGuardian(null);
            getCurrentlySelectedPatient().setIsGuardianAPatient(false);
        }
        getMyController()
            .getDescriptor()
            .getViewDescription()
            .setProperty(SystemDefinition.Properties.PATIENT, getCurrentlySelectedPatient());
        ActionEvent actionEvent = new ActionEvent(
            ModalPatientGuardianEditorView.this,ActionEvent.ACTION_PERFORMED, 
            PatientViewController.Actions.PATIENT_EDITOR_VIEW_CHANGE
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseView)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
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
                    //.addComponent(btnSaveGuardianDetails)
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
