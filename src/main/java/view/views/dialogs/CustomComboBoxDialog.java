/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.dialogs;

/**
 *
 * @author colin
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import model.entity.Patient;
import view.views.non_modal_views.ScheduleView;

public class CustomComboBoxDialog extends JDialog {

    private JComboBox<Patient> cmbPatientSelector;
    private JButton okButton;
    private JButton cancelButton;
    private Patient selectedValue;
    private boolean confirmed;
    private DefaultComboBoxModel<Patient> model = null;

    public CustomComboBoxDialog(Frame owner, String title, ScheduleView view) {
        super(owner, title, true);
        setLayout(new BorderLayout());
        
        ArrayList<Patient> patients = 
                view.getMyController().getDescriptor().
                        getControllerDescription().getPatients();
        
        // Initialize components
        cmbPatientSelector = new JComboBox<>();
        int height = cmbPatientSelector.getPreferredSize().height;
        cmbPatientSelector.setPreferredSize(new Dimension(200,height));
        model = new DefaultComboBoxModel<>();
        cmbPatientSelector.setModel(model);
        populatePatientSelector(patients);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        // Panel for comboBox
        JPanel comboPanel = new JPanel();
        comboPanel.add(new JLabel("Select an option:"));
        comboPanel.add(cmbPatientSelector);
        add(comboPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for buttons
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                selectedValue = (Patient) cmbPatientSelector.getSelectedItem();
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });

        // Set dialog properties
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void populatePatientSelector(ArrayList<Patient> patients){
        Iterator<Patient> it = patients.iterator();
        while (it.hasNext()){
            Patient patient = it.next();
            model.addElement(patient);
        }
        cmbPatientSelector.setSelectedIndex(-1);
    }

    public Patient getSelectedValue() {
        return selectedValue;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
