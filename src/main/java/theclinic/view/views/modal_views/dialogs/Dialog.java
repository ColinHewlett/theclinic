/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package theclinic.view.views.modal_views.dialogs;

/**
 *
 * @author colin
 */
import theclinic.controller.SystemDefinition;
import theclinic.controller.Descriptor;
import theclinic.model.entity.SecondaryCondition;
import theclinic.model.entity.PrimaryCondition;
import theclinic.model.entity.Patient;
import theclinic.controller.ViewController;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util. ArrayList;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;


public class Dialog extends javax.swing.JDialog {
    private ViewController myController = null;
    
    private ViewController getMyController(){
        return myController;
    }
    private void setMyController(ViewController value){
        myController = value;
    }
    
    /**
     * 
     * @param parent
     * @param modal
     * @param optionsValue
     * @param controller 
     */
    public Dialog(java.awt.Frame parent, 
            boolean modal, 
            ArrayList<JCheckBox> optionsValue,
            ViewController controller) {
        super(parent, modal);
        options = optionsValue;
        myController = controller;
        initComponents();
        initialiseView();
    }
    
    private ArrayList<JCheckBox> options = null;
    private ArrayList<JCheckBox> getOptions(){
        return options;
    }
    
    private void initialiseView(){
        Iterator<JCheckBox> iterator = getOptions().iterator();
        while(iterator.hasNext()){
            JCheckBox checkBox = (JCheckBox)iterator.next();
            //addItemListenerFor(checkBox);
            lstCheckBoxs.add(iterator.next());
        }
    }
    
    private String dialogReply = null;
    private void addItemListenerFor(JMenuItem menuItem){
        dialogReply = null;
        Patient patient = (Patient)getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getProperty(SystemDefinition.Properties.PATIENT);
        PrimaryCondition pCondition = new PrimaryCondition();
        SecondaryCondition sCondition = new SecondaryCondition();
        
        menuItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean value = e.getStateChange()==ItemEvent.SELECTED;
                switch(menuItem.getText()){
                    case "Arthritus":
                        pCondition.setDescription("Arthritus");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value);              
                        break;
                    case "Chorea (St Vitus dance)":
                        pCondition.setDescription("Chorea (St Vitus dance)");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value); 
                        break;
                    case "Diabetes":
                        pCondition.setDescription("Diabetes");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value)
                        break;
                    case "Epilepsy":
                        pCondition.setDescription("Diabetes");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value)
                        break;  
                    case "Fainting attacks, blackouts, giddiness":
                        pCondition.setDescription("Fainting attacks, blackouts, giddiness");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value)
                        break;
                    case "Rheumatic fever":
                        pCondition.setDescription("Rheumatic fever");
                        //pCondition.setOrder(1);
                        //pCondition.setValue1(value)
                        break;
                    case "Pacemaker user":
                        break;
                    case "Previous heart surgery":
                        break;
                    case "Heart murmur":
                        break;
                    case "Angina":
                        break;
                    case "High blood pressure":
                        break;
                    case "bruise easily/bleed abnormally post injury/surgery":
                        break;  
                    case "Kidney-related":
                        break;
                    case "Jaundice":
                        break;
                    case "Hepatitus":
                        //dialogReply = doComboboxDialog("Select hepatitus type");
                        break;
                    case "Other liver-related ondition":
                        //dialogReply = doTextEntryDialog("Enter other liver-reated condition");
                        break; 
                    case "Asthma":
                        break;
                    case "Bronchitus":
                        break;
                    case "Other lung-related condition":
                        //dialogReply = doTextEntryDialog("Enter other liver-reated condition");
                        break;
                    case "Mouth sores (herpes)":
                        break;  
                    case "HIV/AIDS":
                        break;
                    case "Other infectious disease":
                        //dialogReply = doTextEntryDialog("Enter any other infectious disease");
                        break;
                    case "to latex":
                        break;
                    case "to adrenalin":
                        break;
                    case "to penecillen":
                        break;
                    case "Hay fever":
                        break;
                    case "Eczema":
                        break;
                    case "Other allergies":
                        //dialogReply = doTextEntryDialog("Enter other allergy");
                        break;  
                    case "Smoke tobacco products":
                        break;
                    case "Alcohol units consumed per week":
                        //dialogReply = doTextEntryDialog("Enter number of alcohol units consumed per week");
                        break;
                    case "Currently prescribed medication?":
                        //dialogReply = doTextEntryDialog("Enter prescribed medication");
                        break;
                    case "Any other information relevant to dental treatment at the Clinic":
                        //dialogReply = doTextEntryDialog("Enter other information relevant to treatment at the Clinic");
                        break; 
                    case "Do any other family members attend The Clinic?":
                        break;
                    case "Who recommended The Clinic to you?":
                        //dialogReply = doTextEntryDialog("How did you hear about the Clinic");
                        break; 
                    default:
                        break;
                }
                
            }
        });
    }

    /**
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lstCheckBoxs = new javax.swing.JList<JCheckBox>();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lstCheckBoxs.setModel(new DefaultListModel<JCheckBox>());
        jScrollPane1.setViewportView(lstCheckBoxs);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(btnClose))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<JCheckBox> lstCheckBoxs;
    // End of variables declaration//GEN-END:variables
}
