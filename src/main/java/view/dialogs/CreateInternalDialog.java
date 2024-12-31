/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.dialogs;

import view.views.non_modal_views.DesktopView;
import javax.swing.JInternalFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 *
 * @author colin
 */
public class CreateInternalDialog {
    
    public CreateInternalDialog(DesktopView desktopView){
        // Create an internal frame as the dialog
        JInternalFrame dialog = new JInternalFrame("Custom Dialog", true, true, true, true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        
        // Create a JSpinner control
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 100, 1); // min: 1, max: 100, step: 1
        JSpinner spinner = new JSpinner(model);

        // Add components to the dialog
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Select a value:"));
        panel.add(spinner);
    }
    
}
