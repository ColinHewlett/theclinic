/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import controller.Descriptor;
import controller.ViewController;
import model.Patient;
import view.View;
import view.views.non_modal_views.DesktopView;
import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author colin
 */
public class ModalPatientSelectionView extends ModalView{
    
    public ModalPatientSelectionView(
            View.Viewer myViewType,
            ViewController myController, 
            DesktopView desktopView){ 
        setTitle("Patient selector view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);
    }

    
    private void populatePatientSelector(){
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        ArrayList<Patient> patients = 
                getMyController().getDescriptor().
                        getControllerDescription().getPatients();
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        this.cmbPatientSelector.setModel(model);
        Patient patient = getMyController().getDescriptor().
                getControllerDescription().getPatient();
        if (patient!=null){
            if (patient.getIsKeyDefined())
                this.cmbPatientSelector.setSelectedItem(patient);
            else this.cmbPatientSelector.setSelectedIndex(-1);
        }
        else this.cmbPatientSelector.setSelectedIndex(-1);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        if (e.getPropertyName().equals(
            ViewController.ScheduleViewControllerPropertyChangeEvent.APPOINTMENT_SCHEDULE_ERROR_RECEIVED.toString())){
            Descriptor ed = (Descriptor)e.getNewValue();
            ViewController.displayErrorMessage(ed.getControllerDescription().getError(),
                                               "Appointment editor dialog error",
                                               JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents(); 
        TitledBorder titledBorder = (TitledBorder)pnlPatientSelection.getBorder();
        switch (getMyViewType()){
            case PATIENT_SELECTION_VIEW:
                titledBorder.setTitle("Select required patient's notes");
                this.setTitle("Patient selection view");
                break;
            case PATIENT_RECOVERY_SELECTION_VIEW:
                titledBorder.setTitle("Select patient to recover");
                this.setTitle("Patient recovery selection view");
                break;
        }
        
        populatePatientSelector();
        this.setVisible(true);
    }
    
    private void initComponents() {

        pnlPatientSelection = new javax.swing.JPanel();
        btnClearSelection = new javax.swing.JButton("Clear selection");
        cmbPatientSelector = new javax.swing.JComboBox<Patient>();
        pnlPatientSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Select patient"));
        DefaultComboBoxModel<Patient> model = 
                new DefaultComboBoxModel<>();
        cmbPatientSelector.setModel(model);

 
        cmbPatientSelector.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPatientSelectorActionPerformed(evt);
            }
        });
        
        btnClearSelection.addActionListener((ActionEvent e) -> btnClearSelectionActionPerformed());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(pnlPatientSelection);
        pnlPatientSelection.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(btnClearSelection)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPatientSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClearSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlPatientSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }                        

    private void cmbPatientSelectorActionPerformed(java.awt.event.ActionEvent evt) {
        ViewController.PatientViewControllerActionEvent action = null;
        if (this.cmbPatientSelector.getSelectedIndex()!=-1){
            getMyController().getDescriptor().getViewDescription().setPatient(
                    (Patient)this.cmbPatientSelector.getSelectedItem());
            switch(getMyViewType()){
                case PATIENT_SELECTION_VIEW:
                    action = ViewController.PatientViewControllerActionEvent.PATIENT_REQUEST;
                    break;
                case PATIENT_RECOVERY_SELECTION_VIEW:
                    action = ViewController.PatientViewControllerActionEvent.PATIENT_RECOVER_REQUEST;
                    break;
                default:
                    action = ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST;
                    break;
            }
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED, 
                    action.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
    }  
    
    private void btnClearSelectionActionPerformed(){
        getMyController().getDescriptor().getViewDescription().setPatient(new Patient());
        this.cmbPatientSelector.setSelectedIndex(-1);
        ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.PatientViewControllerActionEvent.NULL_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
    }


    // Variables declaration - do not modify                     
    private javax.swing.JComboBox<Patient> cmbPatientSelector;
    private javax.swing.JButton btnClearSelection;
    private javax.swing.JPanel pnlPatientSelection;
    // End of variables declaration 
    
    
}

