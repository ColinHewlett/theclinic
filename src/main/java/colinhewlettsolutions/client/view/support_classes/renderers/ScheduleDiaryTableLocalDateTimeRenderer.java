/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.model.non_entity.Slot;
import colinhewlettsolutions.client.view.support_classes.models.ScheduleDiaryTableModel;
import colinhewlettsolutions.client.view.View;
/**
 *
 * @author colin
 */
public class ScheduleDiaryTableLocalDateTimeRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter timeOnlyFormat = DateTimeFormatter.ofPattern("HH:mm");
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    public ScheduleDiaryTableLocalDateTimeRenderer(View view)
    {
        Font f = super.getFont();
         //plain
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
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
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            LocalDateTime startTime = (LocalDateTime)value;
            super.setText(startTime.format(timeOnlyFormat));
            super.setFont(getFont().deriveFont(Font.BOLD));
            super.setHorizontalAlignment(JLabel.CENTER);
        }
        
        model = (ScheduleDiaryTableModel)table.getModel();
        slot = model.getElementAt(row);

        if (slot.getAppointment().getIsEmergency()){ ;
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
                    setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND));
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
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                            getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_FOREGROUND));
                    break;

            }
        }
        
        setOpaque(true);
        return this;
    }
    
    private boolean isThisSlotAppointmentHeader(Slot slot){
        return slot.getStart().equals(slot.getAppointment().getStart());
    }
}
