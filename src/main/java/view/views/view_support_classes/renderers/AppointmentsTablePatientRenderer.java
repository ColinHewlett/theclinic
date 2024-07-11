/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.view_support_classes.renderers;

import model.entity.Patient;
import model.entity.Appointment;
import model.non_entity.SystemDefinition.ScheduleSlotType;
import view.views.view_support_classes.models.ScheduleTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class AppointmentsTablePatientRenderer  extends JLabel implements TableCellRenderer{
    private boolean isUnbookable = false;
    private Appointment appointment = null;
    
    public AppointmentsTablePatientRenderer()
    {
        Font f = super.getFont();
        // bold
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    private ScheduleSlotType slotMarker = null;
    private ScheduleSlotType getSlotMarker(){
        return slotMarker;
    }
    private void setSlotMarker(ScheduleSlotType value){
        slotMarker = value;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        ScheduleTableModel model = (ScheduleTableModel)table.getModel();
        appointment = model.getElementAt(row);
        Patient patient = (Patient)value;
        
        if (appointment.getIsEmergency()){ 
            super.setText(patient.toString());
            super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT);
        }else if (patient == null) {
            super.setText(ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT.mark());
            super.setHorizontalAlignment(JLabel.CENTER);
            setSlotMarker(ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT);
        }
        else if (patient.toString().equals(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            super.setText(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark());
            super.setHorizontalAlignment(JLabel.CENTER);
            setSlotMarker(ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT);
        }
        else {
            super.setText(patient.toString());
            super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(ScheduleSlotType.BOOKED_SCHEDULE_SLOT); 
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            switch(getSlotMarker()){
                case UNBOOKABLE_SCHEDULE_SLOT:
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(Color.BLUE);
                    break;
                case BOOKED_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                    break;
                case EMERGENCY_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(Color.RED);
                    break;
            }
        }
       
        setOpaque(true);
        return this;
    }
}
