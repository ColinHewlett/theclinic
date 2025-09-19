/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.model.entity.SecondaryCondition;
import colinhewlettsolutions.client.model.entity.PrimaryCondition;
import colinhewlettsolutions.client.model.entity.Condition;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.border.TitledBorder;

/**
 *
 * @author colin
 */
public class ModalNoteTaker extends ModalView implements ActionListener{
    
    enum Action{
        REQUEST_TO_SAVE_NOTES,
        REQUEST_TO_CANCEL
    }
    /**
     * Creates new form ModalNoteTaker
     */
    public ModalNoteTaker(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient medical history primary view");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    public void actionPerformed(ActionEvent e){
        switch(Action.valueOf(e.getActionCommand())){
            case REQUEST_TO_CANCEL:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            case REQUEST_TO_SAVE_NOTES:
                //Condition condition = getMyController().getDescriptor().getControllerDescription().getCondition();
                //PrimaryCondition pc = (PrimaryCondition)condition;
                //PrimaryCondition pc = ((SecondaryCondition)getConditionOnEntry()).getPrimaryCondition();
                
                //getConditionOnEntry().setNotes(txaNotepad.getText());
                getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.CONDITION, getConditionOnEntry());
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.PatientViewControllerActionEvent.
                            PATIENT_MEDICAL_HISTORY_NOTES_TAKEN_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){

                }
                break;
            
        }
    }
    
    public void initialiseView(){
        initComponentsx();
        this.btnSaveAndQuit.setActionCommand(Action.REQUEST_TO_SAVE_NOTES.toString());
        this.btnQuit.setActionCommand(Action.REQUEST_TO_CANCEL.toString());
        btnSaveAndQuit.addActionListener(this);
        btnQuit.addActionListener(this); 
        txaNotepad.setLineWrap(true);
        
        Patient patient = (Patient)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.PATIENT);
        setTitle(patient.toString() + ": medical history notes editor");
        
        conditionOnEntry = (Condition)getMyController().getDescriptor()
                .getControllerDescription().getProperty(SystemDefinition.Properties.CONDITION);
        
        PrimaryCondition pc = null;
        if (getConditionOnEntry().getIsPrimaryCondition()){
            TitledBorder titledBorder = (TitledBorder)this.pnlNotepad.getBorder();
            titledBorder.setTitle("'" + getConditionOnEntry().getDescription() + "' notes" );
            pc = (PrimaryCondition)getConditionOnEntry();
            //txaNotepad.setText(pc.getNotes());
            
        }else if(getConditionOnEntry().getIsSecondaryCondition()){
            TitledBorder titledBorder = (TitledBorder)this.pnlNotepad.getBorder();
            SecondaryCondition sc = (SecondaryCondition)getConditionOnEntry();
            pc = sc.getPrimaryCondition();
            titledBorder.setTitle("'" + pc.getDescription() 
                    + " (" +  sc.getDescription() + ")' notes" );
           // txaNotepad.setText(sc.getNotes());
        }
    }
    
    private Condition conditionOnEntry = null;
    private Condition getConditionOnEntry(){
        return conditionOnEntry;
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
        btnSaveAndQuit = new javax.swing.JButton();
        btnQuit = new javax.swing.JButton();
        pnlNotepad = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaNotepad = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        btnSaveAndQuit.setText("Save");

        btnQuit.setText("Cancel");
        btnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSaveAndQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnSaveAndQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnlNotepad.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txaNotepad.setColumns(20);
        txaNotepad.setRows(5);
        jScrollPane1.setViewportView(txaNotepad);

        jLabel1.setText("Heart-related notes");

        javax.swing.GroupLayout pnlNotepadLayout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(pnlNotepadLayout);
        pnlNotepadLayout.setHorizontalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlNotepadLayout.setVerticalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnQuitActionPerformed

    private void initComponentsx() {

        jPanel1 = new javax.swing.JPanel();
        btnSaveAndQuit = new javax.swing.JButton();
        btnQuit = new javax.swing.JButton();
        pnlNotepad = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaNotepad = new javax.swing.JTextArea();

       // jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnSaveAndQuit.setText("Save");

        btnQuit.setText("Cancel");
        btnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSaveAndQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnSaveAndQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGap(10)
                .addComponent(btnQuit, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                /*.addContainerGap()*/)
        );

        pnlNotepad.setBorder(javax.swing.BorderFactory.createTitledBorder("Heart-related notes"));

        txaNotepad.setColumns(20);
        txaNotepad.setRows(5);
        jScrollPane1.setViewportView(txaNotepad);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>  
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btnSaveAndQuit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlNotepad;
    private javax.swing.JTextArea txaNotepad;
    // End of variables declaration//GEN-END:variables
}
