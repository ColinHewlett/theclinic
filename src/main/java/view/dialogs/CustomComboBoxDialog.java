/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.dialogs;

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
import view.views.non_modal_views.ScheduleDiaryView.ScheduleDiaryAction;
import view.views.non_modal_views.ScheduleDiaryView;

public class CustomComboBoxDialog extends JDialog {

    private JComboBox<Patient> cmbPatientSelector;
    private JComboBox<String> cmbScheduleDiarySelector;
    private JButton okButton;
    private JButton cancelButton;
    private Patient selectedValue;
    private ScheduleDiaryAction selectedDiaryAction;
    private boolean confirmed;
    private DefaultComboBoxModel<Patient> model = null;
    private String[] options = null;
    /*
    public CustomComboBoxDialog(Frame owner, ScheduleDiaryAction scheduleDiaryAction){
        super(owner, "", true);
        
        switch (scheduleDiaryAction){
            case EXTEND_SHIFT_APPOINTMENT_LATER:
                options = new String[]{"extend appointment to finish later", 
                                       "shift whole appointment later"};
                cmbScheduleDiarySelector = new JComboBox<>(options);
                break;
            case EXTEND_SHIFT_APPOINTMENT_EARLIER:
                options = new String[]{"extend appointment to finish earlier", 
                                       "shift whole appointment earlier"};
                cmbScheduleDiarySelector = new JComboBox<>(options);
                break;
        }
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        // Panel for comboBox
        JPanel comboPanel = new JPanel();
        comboPanel.add(new JLabel(""));
        comboPanel.add(cmbScheduleDiarySelector);
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
                String s = (String) cmbScheduleDiarySelector.getSelectedItem();
                switch (scheduleDiaryAction){
                    case EXTEND_SHIFT_APPOINTMENT_LATER:
                        if (s.contains("extend")) selectedDiaryAction = ScheduleDiaryAction.EXTEND_APPOINTMENT_LATER;
                        else selectedDiaryAction = ScheduleDiaryAction.SHIFT_APPOINTMENT_LATER;
                        break;
                    case EXTEND_SHIFT_APPOINTMENT_EARLIER:
                        s = (String) cmbScheduleDiarySelector.getSelectedItem();
                        if (s.contains("extend")) selectedDiaryAction = ScheduleDiaryAction.EXTEND_APPOINTMENT_EARLIER;
                        else selectedDiaryAction = ScheduleDiaryAction.SHIFT_APPOINTMENT_EARLIER;
                        break;
                }
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
*/
    public CustomComboBoxDialog(Frame owner, String title, ScheduleDiaryView view) {
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
        comboPanel.add(new JLabel("Select patient:"));
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
    
    public ScheduleDiaryAction getSelectedDiaryAction(){
        return selectedDiaryAction;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
