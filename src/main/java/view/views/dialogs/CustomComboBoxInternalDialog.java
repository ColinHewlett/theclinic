/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import view.views.non_modal_views.ScheduleView;
import javax.swing.JInternalFrame;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import model.entity.Patient;
import java.awt.BorderLayout;

/**
 *
 * @author colin
 */
public class CustomComboBoxInternalDialog {

    private DefaultComboBoxModel<Patient> model = null;
    private JComboBox<Patient> cmbPatientSelector = null;
    private JInternalFrame internalFrame = null;
    private ArrayList<Patient> patients = null;
    private Patient selectedPatient = null;
    private Integer result = null;
    
    public CustomComboBoxInternalDialog(ScheduleView view){
        internalFrame = new JInternalFrame("Internal frame", true,true,true,true);
        internalFrame.setSize(300, 200);
        internalFrame.setVisible(true);
        view.getDesktopPane().add(internalFrame);
        
        patients = view.getMyController().getDescriptor().
                        getControllerDescription().getPatients();
    }

    public void initialise(){    
        // Create the JComboBox
        cmbPatientSelector = new JComboBox<>();
        model = new DefaultComboBoxModel<>();
        cmbPatientSelector.setModel(model);
        populatePatientSelector(patients);

        // Show the internal input dialog with the JComboBox
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Select an option:"), BorderLayout.NORTH);
            panel.add(cmbPatientSelector, BorderLayout.CENTER);
            
            /*
                Integer result = JOptionPane.showInternalConfirmDialog(
                        internalFrame.getContentPane(),
                        cmbPatientSelector,
                        "Select patient",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    if (cmbPatientSelector.getSelectedIndex()!=-1)
                        selectedPatient = (Patient) cmbPatientSelector.getSelectedItem();
                }
                */
        });
    }
    
    public void kill(){
        internalFrame.dispose();
    }
    
    public Patient getSelectedPatient(){
        return selectedPatient;
    }
    
    private void populatePatientSelector(ArrayList<Patient> patients){
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        cmbPatientSelector.setSelectedIndex(-1);
    }
}
