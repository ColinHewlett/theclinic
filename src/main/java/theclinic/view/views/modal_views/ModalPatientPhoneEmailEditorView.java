/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.PatientViewController;
import theclinic.controller.SystemDefinition;
import theclinic.controller.ViewController;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import theclinic.model.entity.Patient;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class ModalPatientPhoneEmailEditorView extends ModalView{
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientPhoneEmailEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient recall editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setTitle("Patient phone & email editor");
        setVisible(true);
        addListeners();
        Patient patient = (Patient)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        
        txtPhone1.setText(patient.getPhone1());
        txtPhone2.setText(patient.getPhone2());
        txtEmail.setText(patient.getEmail());
                
    }
    
    /**
     * Sends an action event to the view controller to save changes
     * Only called if changes have been made to patients data
     */
    private void doSaveEditorChanges(){
        Patient patient = (Patient)getMyController().getDescriptor().getControllerDescription()
                .getProperty(SystemDefinition.Properties.PATIENT);
        patient.setPhone1(txtPhone1.getText());
        patient.setPhone2(txtPhone2.getText());
        patient.setEmail(txtEmail.getText());
        getMyController()
                .getDescriptor()
                .getViewDescription()
                .setProperty(SystemDefinition.Properties.PATIENT, patient);
        ActionEvent actionEvent = new ActionEvent(
            ModalPatientPhoneEmailEditorView.this,ActionEvent.ACTION_PERFORMED, 
            PatientViewController.Actions.PATIENT_EDITOR_VIEW_CHANGE
                    .toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private void addListeners(){
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveEditorChanges();
                try{
                    ModalPatientPhoneEmailEditorView.this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
            }
        });
        
        btnSavePhoneEmail.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doSaveEditorChanges();
                try{
                    ModalPatientPhoneEmailEditorView.this.setClosed(true);
                }
                catch (PropertyVetoException ex){

                }
            }
        });
    }
    
    private void initComponents() {

        pnlEmail = new javax.swing.JPanel();
        txtEmail = new javax.swing.JTextField();
        pnlPhones = new javax.swing.JPanel();
        txtPhone1 = new javax.swing.JTextField();
        lbl1 = new javax.swing.JLabel();
        txtPhone2 = new javax.swing.JTextField();
        lbl2 = new javax.swing.JLabel();
        btnSavePhoneEmail = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        pnlEmail.setBorder(javax.swing.BorderFactory.createTitledBorder("Email"));
        
        pnlPhones.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                "Phone(s)", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));
        
        pnlEmail.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                        javax.swing.BorderFactory.createEtchedBorder(), 
                "Email", 
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                getBorderTitleFont(), 
                getBorderTitleColor()));

        javax.swing.GroupLayout pnlEmailLayout = new javax.swing.GroupLayout(pnlEmail);
        pnlEmail.setLayout(pnlEmailLayout);
        pnlEmailLayout.setHorizontalGroup(
            pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEmailLayout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlEmailLayout.setVerticalGroup(
            pnlEmailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmailLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pnlPhones.setBorder(javax.swing.BorderFactory.createTitledBorder("Phone(s)"));

        lbl1.setText("[1]");

        lbl2.setText("[2]");

        javax.swing.GroupLayout pnlPhonesLayout = new javax.swing.GroupLayout(pnlPhones);
        pnlPhones.setLayout(pnlPhonesLayout);
        pnlPhonesLayout.setHorizontalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhonesLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lbl1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPhone2)
                .addContainerGap())
        );
        pnlPhonesLayout.setVerticalGroup(
            pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPhonesLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlPhonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl1)
                    .addComponent(txtPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl2))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        btnSavePhoneEmail.setText("Save");

        btnClose.setText("Close");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlPhones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPhones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    //.addComponent(btnSavePhoneEmail)
                    .addComponent(btnClose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

                                         


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSavePhoneEmail;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JPanel pnlEmail;
    private javax.swing.JPanel pnlPhones;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtPhone1;
    private javax.swing.JTextField txtPhone2;
    // End of variables declaration  
}
