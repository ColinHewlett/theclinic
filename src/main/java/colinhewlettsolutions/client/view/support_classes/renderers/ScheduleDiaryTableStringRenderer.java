/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import colinhewlettsolutions.client.view.support_classes.models.ScheduleDiaryTableModel;
import colinhewlettsolutions.client.model.non_entity.Slot;
import colinhewlettsolutions.client.controller.SystemDefinition;
import static colinhewlettsolutions.client.controller.SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
import static colinhewlettsolutions.client.controller.SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
import static colinhewlettsolutions.client.controller.SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
import static colinhewlettsolutions.client.controller.SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
import colinhewlettsolutions.client.view.View;
/**
 *
 * @author colin
 */
public class ScheduleDiaryTableStringRenderer extends DefaultTableCellRenderer{
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    public ScheduleDiaryTableStringRenderer(View view){
        this.view = view;
    }
    
    private View view = null;
    private View getView(){
        return view;
    }
    
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
        String treatment = null;
        model = (ScheduleDiaryTableModel)table.getModel();
        slot = model.getElementAt(row);
        
        if (isThisSlotAppointmentHeader(slot)){
            // Custom rendering logic for string values
            if (value instanceof String) {
                treatment = (String) value;
                
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }
        }
        
        if (slot.getAppointment().getIsEmergency()){ 
            if (isThisSlotAppointmentHeader(slot)){
                super.setText("EMERGENCY APPOINTMENT");
                super.setFont(getFont().deriveFont(Font.BOLD));
                super.setHorizontalAlignment(JLabel.CENTER);
            }else{
                super.setText("----- '' -----");
                super.setFont(getFont().deriveFont(Font.BOLD));
                super.setHorizontalAlignment(JLabel.CENTER);
            }
            setSlotMarker(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT);
        }else if (slot.getAppointment().getPatient() == null) {
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT);
        }
        else if (slot.getAppointment().getPatient().toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            if (isThisSlotAppointmentHeader(slot)){
                super.setText(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark());
                super.setHorizontalAlignment(JLabel.CENTER);
                super.setFont(getFont().deriveFont(Font.BOLD));
            }else{
                super.setText("----- '' -----");
                super.setFont(getFont().deriveFont(Font.BOLD));
                super.setHorizontalAlignment(JLabel.CENTER);
            }
            setSlotMarker(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT);
        }
        else {
            if (!isThisSlotAppointmentHeader(slot)){
                super.setText("----- '' -----");
                super.setFont(getFont().deriveFont(Font.BOLD));
                super.setHorizontalAlignment(JLabel.CENTER);
            }
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT); 
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            switch(getSlotMarker()){
                case UNBOOKABLE_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND));
                    break;
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_FOREGROUND));
                    break;
                case BOOKED_SCHEDULE_SLOT:
                    if (slot.getStart().equals(slot.getAppointment().getStart())){
                        setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND));
                        setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND));
                    }else {
                        setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND));
                        setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND));
                    }
                    break;
                case EMERGENCY_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND));
                    break;
                

            }
        }
        return c;
    }
    
    private boolean isThisSlotAppointmentHeader(Slot slot){
        return slot.getStart().equals(slot.getAppointment().getStart());
    }
}
