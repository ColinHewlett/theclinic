/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.SystemDefinition;
import theclinic.controller.PatientViewController;
import theclinic.controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import theclinic.model.entity.Doctor;
import theclinic.model.entity.Patient;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;

/**
 *
 * @author colin
 */
public class ModalPatientDoctorEditorView extends ModalView 
        implements ActionListener, PropertyChangeListener {

    enum Action {
        REQUEST_DOCTOR_CREATE_OR_UPDATE,
        REQUEST_DOCTOR_REMOVAL,
        REQUEST_CLOSE_VIEW
    }
    
    enum ViewMode {
        CREATE,
        UPDATE
    }
    /**
     * Creates new form ModalPatientMedicationEditorView
     */
    public ModalPatientDoctorEditorView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    /**
     * on receipt of DOCTOR_RECEIVED property change event
     * -- received doctor object's collection is either empty or not
     * ---- if empty 
     * ------ ViewMode.CREATE entered, and doctor details cleared
     * ---- else 
     * ------ ViewMode.UPDATE entered, and doctor details initialised
     */
    public void propertyChange(PropertyChangeEvent e){
        Doctor doctor = (Doctor)getMyController().getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.DOCTOR);
        Patient patient = (Patient)getMyController().getDescriptor()
                        .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        PatientViewController.Properties propertyName =
                PatientViewController.Properties.valueOf(e.getPropertyName());
        switch (propertyName){  
            case DOCTOR_RECEIVED:
                if (doctor.get().isEmpty()){ 
                    doctor = new Doctor(patient);
                    setViewDataForDoctor(doctor);
                    setViewMode(ViewMode.CREATE);
                }else{
                    doctor = doctor.get().get(0);
                    setViewDataForDoctor(doctor);
                    setViewMode(ViewMode.UPDATE);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      txtTitle.requestFocus();
                    }
                });
                break;
                
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Doctor doctor = (Doctor)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.DOCTOR);
        String reply = null;
        ActionEvent actionEvent = null;
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            case REQUEST_DOCTOR_CREATE_OR_UPDATE:
                switch(getViewMode()){
                    case CREATE:
                        doctor = new Doctor();
                        getViewDataForDoctor(doctor);
                        if (getIsAnyDataEnteredFor(doctor)){
                            getMyController().getDescriptor().
                                            getViewDescription().setProperty(SystemDefinition.Properties.DOCTOR, doctor);
                            actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                PatientViewController.Actions.
                                        PATIENT_DOCTOR_CREATE_REQUEST.toString());
                            this.getMyController().actionPerformed(actionEvent);
                        }else{
                            String message = "No data has been entered for the "
                                    + "doctor, so attempt to create new doctor data is aborted";
                            JOptionPane.showInternalConfirmDialog(this, message); 
                        }
                        break;
                    case UPDATE:
                        boolean isAnyDataEntered = false;
                        getViewDataForDoctor(doctor);
                        if (getIsAnyDataEnteredFor(doctor)){
                            getMyController().getDescriptor().
                                        getViewDescription().setProperty(SystemDefinition.Properties.DOCTOR, doctor);
                            actionEvent = new ActionEvent(
                                this,ActionEvent.ACTION_PERFORMED,
                                PatientViewController.Actions.
                                        PATIENT_DOCTOR_UPDATE_REQUEST.toString());
                            this.getMyController().actionPerformed(actionEvent);
                        }else{
                            String message = "No data has been entered for the "
                                    + "doctor, so attempt to update the doctor data is aborted";
                            JOptionPane.showInternalConfirmDialog(this, message);
                        }
                        break;
                }
                break;        
            case REQUEST_DOCTOR_REMOVAL: { 
                getViewDataForDoctor(doctor);
                if (getIsAnyDataEnteredFor(doctor)){
                    actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.
                                PATIENT_DOCTOR_DELETE_REQUEST.toString());
                    this.getMyController().actionPerformed(actionEvent);
                }else{
                    String message = "No data has been entered for the "
                            + "doctor, so attempt to delete the doctor data is aborted";
                    JOptionPane.showInternalConfirmDialog(this, message);
                }
                break;
            }
        }
    }
     
    private boolean getIsAnyDataEnteredFor(Doctor doctor){
        boolean isAnyDataEntered = false;
        if (!doctor.getTitle().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getLine1().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getLine2().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getTown().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getCounty().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getPostcode().isEmpty()) isAnyDataEntered = true;
        if (!doctor.getPhone().isEmpty()) isAnyDataEntered = true;
        //if (!doctor.getEmail().isEmpty()) isAnyDataEntered = true;
        return isAnyDataEntered;
    }
    
    private void setViewDataForDoctor(Doctor doctor){
        txtTitle.setText(doctor.getTitle());
        txtLine1.setText(doctor.getLine1());
        txtLine2.setText(doctor.getLine2());
        txtTown.setText(doctor.getTown());
        txtCounty.setText(doctor.getCounty());
        txtPostcode.setText(doctor.getPostcode());
        txtPhone.setText(doctor.getPhone());
        //txtEmail.setText(doctor.getEmail());
    }
    
    private Doctor getViewDataForDoctor(Doctor doctor){
        doctor.setTitle(this.txtTitle.getText());
        doctor.setLine1(txtLine1.getText());
        doctor.setLine2(txtLine2.getText());
        doctor.setTown(txtTown.getText());
        doctor.setCounty(txtCounty.getText());
        doctor.setPostcode(txtPostcode.getText());
        doctor.setPhone(txtPhone.getText());
        //doctor.setEmail(txtEmail.getText());
        return doctor;
    }
            
    @Override
    public void initialiseView(){
        initComponents();
        /**
         * if on entry the Doctor's collection is empty
         */
        Doctor doctor = (Doctor)getMyController()
                .getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.DOCTOR);
        if (doctor.get().isEmpty()) setViewMode(ViewMode.CREATE);
        else {
            setViewMode(ViewMode.UPDATE);
            setViewDataForDoctor(doctor.get().get(0));
        }
        
        setTitle((String)((Patient)getMyController().getDescriptor().getControllerDescription()
                .getProperty(SystemDefinition.Properties.PATIENT)).toString());
        
        this.btnCloseView.setActionCommand(
                Action.REQUEST_CLOSE_VIEW.toString());
        this.btnCreateUpdateDoctor.setActionCommand(
                Action.REQUEST_DOCTOR_CREATE_OR_UPDATE.toString());
        this.btnDeleteDoctor.setActionCommand(
                Action.REQUEST_DOCTOR_REMOVAL.toString());
        this.btnCloseView.addActionListener(this);
        this.btnCreateUpdateDoctor.addActionListener(this);
        this.btnDeleteDoctor.addActionListener(this);
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
                s = btnCreateUpdateDoctor.getText();
                s = s.replace("Update", "Add");
                btnCreateUpdateDoctor.setText(s);
                break;
            case UPDATE:
                s = btnCreateUpdateDoctor.getText();
                s = s.replace("Add", "Update");
                btnCreateUpdateDoctor.setText(s);
                break;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        txtLine1 = new javax.swing.JTextField();
        txtLine2 = new javax.swing.JTextField();
        txtTown = new javax.swing.JTextField();
        txtCounty = new javax.swing.JTextField();
        txtPostcode = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        btnCreateUpdateDoctor = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnDeleteDoctor = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 204, 255)));

        jLabel1.setText("Doctor");
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel2.setText("Line 1");
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel3.setText("Line 2");
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel4.setText("Town");
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel5.setText("County");
        jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel6.setText("Postcode");
        jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);

        jLabel7.setText("Phone");
        jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtLine2)
                            .addComponent(txtLine1)
                            .addComponent(txtTitle)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTown, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCounty, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLine2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtCounty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        btnCreateUpdateDoctor.setText("<html><center>Update</center><center>doctor</center><center>details</center></html>");
        btnCreateUpdateDoctor.setMaximumSize(new java.awt.Dimension(2147483647, 65));
        btnCreateUpdateDoctor.setMinimumSize(new java.awt.Dimension(71, 65));
        btnCreateUpdateDoctor.setPreferredSize(new java.awt.Dimension(71, 65));
        //btnCreateUpdateDoctor.setSize(btnCreateUpdateDoctor.getWidth(), 94);

        btnCloseView.setText("<html><center>Close</center><center>view</cen ter></html>");
        //btnCloseView.setSize(btnCloseView.getWidth(),94);
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseViewActionPerformed(evt);
            }
        });

        btnDeleteDoctor.setText("<html><center>Delete</center><center>doctor</center></html>");
        btnDeleteDoctor.setMaximumSize(new java.awt.Dimension(2147483647, 65));
        btnDeleteDoctor.setMinimumSize(new java.awt.Dimension(67, 65));
        btnDeleteDoctor.setPreferredSize(new java.awt.Dimension(67, 65));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCreateUpdateDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView)
                    .addComponent(btnDeleteDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCreateUpdateDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnDeleteDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCloseViewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnCreateUpdateDoctor;
    private javax.swing.JButton btnDeleteDoctor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCounty;
    private javax.swing.JTextField txtLine1;
    private javax.swing.JTextField txtLine2;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtPostcode;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextField txtTown;
    // End of variables declaration//GEN-END:variables
}
