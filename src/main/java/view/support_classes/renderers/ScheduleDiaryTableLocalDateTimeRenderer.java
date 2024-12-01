/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.support_classes.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import model.non_entity.SystemDefinition;
import model.non_entity.Slot;

import view.support_classes.models.ScheduleDiaryTableModel;

/**
 *
 * @author colin
 */
public class ScheduleDiaryTableLocalDateTimeRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter timeOnlyFormat = DateTimeFormatter.ofPattern("HH:mm");
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    public ScheduleDiaryTableLocalDateTimeRenderer()
    {
        Font f = super.getFont();
         //plain
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
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
        
        //if (!slot.getStart().equals(slot.getAppointment().getStart())){
        if (slot.getAppointment().getIsEmergency()){ 
            //super.setText(slot.getAppointment().getPatient().toString());
            //super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT);
        }else if (slot.getAppointment().getPatient() == null) {
            //super.setText(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT.mark());
            //super.setText("");
            //super.setHorizontalAlignment(JLabel.CENTER);
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT);
        }
        else if (slot.getAppointment().getPatient().toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            //super.setText(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark());
            setSlotMarker(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT);
        }
        else {
            //super.setText(slot.getAppointment().getPatient().toString());
            //super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT); 
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            switch(getSlotMarker()){
                case UNBOOKABLE_SCHEDULE_SLOT:
                    if (isThisSlotAppointmentHeader(slot)){
                        setBackground(SystemDefinition.UNBOOKABLE_HEADER_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.UNBOOKABLE_HEADER_SLOT_FOREGROUND);
                    }else {
                        setBackground(SystemDefinition.UNBOOKABLE_BLOCK_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.UNBOOKABLE_BLOCK_SLOT_FOREGROUND);
                    }
                    break;
                case BOOKED_SCHEDULE_SLOT:
                    if (isThisSlotAppointmentHeader(slot)){
                        setBackground(SystemDefinition.BOOKED_HEADER_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.BOOKED_HEADER_SLOT_FOREGROUND);
                    }else {
                        setBackground(SystemDefinition.BOOKED_BLOCK_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.BOOKED_BLOCK_SLOT_FOREGROUND);
                    }
                    break;
                case EMERGENCY_SCHEDULE_SLOT:
                    if (isThisSlotAppointmentHeader(slot)){
                        setBackground(SystemDefinition.EMERGENCY_HEADER_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.EMERGENCY_HEADER_SLOT_FOREGROUND);
                    }else {
                        setBackground(SystemDefinition.EMERGENCY_BLOCK_SLOT_BACKGROUND);
                        setForeground(SystemDefinition.EMERGENCY_BLOCK_SLOT_FOREGROUND);
                    }
                    break;
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground(SystemDefinition.BOOKABLE_SLOT_BACKGROUND);
                    setForeground(SystemDefinition.BOOKABLE_SLOT_FOREGROUND);;
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
