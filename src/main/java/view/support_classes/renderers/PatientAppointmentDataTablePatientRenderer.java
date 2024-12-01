/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.support_classes.renderers;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import model.entity.Appointment;
import model.entity.Patient;
import model.non_entity.SystemDefinition;

/**
 *
 * @author colin
 */
public class PatientAppointmentDataTablePatientRenderer extends JLabel implements TableCellRenderer{
    private boolean isUnbookable = false;
    private Appointment appointment = null;
    //ScheduleListTableModel listModel = null;
    
    public PatientAppointmentDataTablePatientRenderer()
    {
        //Font f = super.getFont();
        // bold
        //this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    private SystemDefinition.ScheduleSlotType slotMarker = null;
    private SystemDefinition.ScheduleSlotType getSlotMarker(){
        return slotMarker;
    }
    private void setSlotMarker(SystemDefinition.ScheduleSlotType value){
        slotMarker = value;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        
        Patient patient = (Patient)value;
        super.setText(patient.toString());
        super.setHorizontalAlignment(JLabel.LEFT);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
       
        setOpaque(true);
        return this;
    }
}
