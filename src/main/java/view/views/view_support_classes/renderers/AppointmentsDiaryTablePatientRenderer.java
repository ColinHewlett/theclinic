/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import model.entity.Appointment;
import model.entity.Patient;
import model.non_entity.Slot;
import model.non_entity.SystemDefinition;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
import view.views.view_support_classes.models.ScheduleDiaryTableModel;

/**
 *
 * @author colin
 */
public class AppointmentsDiaryTablePatientRenderer extends JLabel implements TableCellRenderer{
    private boolean isUnbookable = false;
    private Appointment appointment = null;
    private Slot slot = null;
    ScheduleDiaryTableModel model = null;
    
    public AppointmentsDiaryTablePatientRenderer()
    {
        Font f = super.getFont();
        // bold
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
        
        model = (ScheduleDiaryTableModel)table.getModel();
        slot = model.getElementAt(row);
        
        if (slot.getAppointment().getIsEmergency()){ 
            super.setText(slot.getAppointment().getPatient().toString());
            super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT);
        }else if (slot.getAppointment().getPatient() == null) {
            //super.setText(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT.mark());
            super.setText("");
            super.setHorizontalAlignment(JLabel.CENTER);
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT);
        }
        else if (slot.getAppointment().getPatient().toString().equals(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark())){
            super.setText(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT.mark());
            super.setHorizontalAlignment(JLabel.CENTER);
            setSlotMarker(SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT);
        }
        else {
            if (slot.getStart().equals(slot.getAppointment().getStart())){
                super.setText(slot.getAppointment().getPatient().toString());
                super.setFont(super.getFont().deriveFont(Font.BOLD));
            }
            else super.setText("");
            super.setHorizontalAlignment(JLabel.LEFT);
            setSlotMarker(SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT); 
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            switch(getSlotMarker()){
                case UNBOOKABLE_SCHEDULE_SLOT:
                    break;
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
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
            }
        }
       
        setOpaque(true);
        return this;
    }
}
