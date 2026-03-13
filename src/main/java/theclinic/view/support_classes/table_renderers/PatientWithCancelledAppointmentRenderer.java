/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_renderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import theclinic.model.entity.Appointment;
import theclinic.model.entity.Patient;
import theclinic.model.entity.PatientAppointmentData;
import theclinic.controller.SystemDefinition;
import theclinic.view.support_classes.table_models.PatientAppointmentDataTableModel;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author colin
 */
public class PatientWithCancelledAppointmentRenderer extends JLabel implements TableCellRenderer{
    private PatientAppointmentDataTableModel model = null;
    
    public PatientWithCancelledAppointmentRenderer()
    {
        //Font f = super.getFont();
        // bold
        //this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        model = (PatientAppointmentDataTableModel)table.getModel();
        PatientAppointmentData pad = (PatientAppointmentData)model.getElementAt(row);

        
        if (value != null){
            Patient patient = (Patient)value;
            super.setText(patient.toString());
            super.setFont(getFont().deriveFont(Font.BOLD));
            super.setHorizontalAlignment(JLabel.LEFT);
            if (pad.getAppointment().getIsCancelled()){
                setColor(Color.red);
                super.setForeground(getColor());
            }
            else {
                setColor(Color.black);
                super.setForeground(getColor());
            }
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else{
            setBackground(table.getBackground());
            setForeground(getColor());
        }
            
        
        setOpaque(true);
        return this;

    }
   
    private Color color = null;
    private void setColor(Color value){
        color = value;
    }
    private Color getColor(){
        return color;
    }
}
