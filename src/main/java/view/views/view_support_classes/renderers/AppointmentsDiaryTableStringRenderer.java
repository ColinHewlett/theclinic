/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.renderers;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Font;
import view.views.view_support_classes.models.ScheduleDiaryTableModel;
import model.non_entity.Slot;
import model.non_entity.SystemDefinition;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;

/**
 *
 * @author colin
 */
public class AppointmentsDiaryTableStringRenderer extends DefaultTableCellRenderer{
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    private SystemDefinition.ScheduleSlotType slotMarker = null;
    private SystemDefinition.ScheduleSlotType getSlotMarker(){
        return slotMarker;
    }
    private void setSlotMarker(SystemDefinition.ScheduleSlotType value){
        slotMarker = value;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        model = (ScheduleDiaryTableModel)table.getModel();
        slot = model.getElementAt(row);
        
        if (slot.getAppointment().getStart().equals(slot.getStart())){
            // Custom rendering logic for string values
            if (value instanceof String) {
                String treatment = (String) value;
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }
        }
        
        if (slot.getAppointment().getIsEmergency()){ 
            setSlotMarker(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT);
        }else if (slot.getAppointment().getPatient() == null) {
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT);
        }
        else if (slot.getAppointment().getPatient().toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            setSlotMarker(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT);
        }
        else {
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT); 
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            switch(getSlotMarker()){
                case UNBOOKABLE_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(Color.BLUE);
                    break;
                case BOOKED_SCHEDULE_SLOT:
                    setForeground(table.getForeground());
                    if (slot.getStart().equals(slot.getAppointment().getStart()))
                        setBackground(SystemDefinition.BOOKED_SLOT_HEADER_COLOR);
                    else setBackground(SystemDefinition.BOOKED_SLOT_BLOCK_COLOR);
                    break;
                case EMERGENCY_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(Color.RED);
                    break;
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                    break;

            }
        }
        return c;
    }
}
