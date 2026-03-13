/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.view.support_classes.table_renderers;

import theclinic.model.entity.Patient;
import theclinic.model.entity.Appointment;
import theclinic.controller.SystemDefinition;
import theclinic.controller.SystemDefinition.ScheduleSlotType;
import theclinic.view.support_classes.table_models.ScheduleListTableModel;
import theclinic.view.support_classes.table_models.ScheduleDiaryTableModel;
import theclinic.view.View;
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
public class ScheduleListTablePatientRenderer  extends JLabel implements TableCellRenderer{
    private boolean isUnbookable = false;
    private Appointment appointment = null;
    ScheduleListTableModel listModel = null;
    
    public ScheduleListTablePatientRenderer(View view)
    {
        this.view = view;
    }
    
    private View view = null;
    private View getView(){
        return view;
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
        
        ScheduleListTableModel model = (ScheduleListTableModel)table.getModel();
        appointment = model.getElementAt(row);
        Patient patient = (Patient)value;
        
        if (appointment.getIsEmergency()){ 
            super.setText(patient.toString());
            //super.setFont(super.getFont().deriveFont(Font.PLAIN));
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
                    setBackground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND));
                    break;
                case BOOKABLE_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND));
                    setForeground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND));
                    break;
                case BOOKED_SCHEDULE_SLOT:
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                    break;
                case EMERGENCY_SCHEDULE_SLOT:
                    setBackground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND));
                    setForeground((Color)getView().
                        getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND));
                    break;
            }
        }
       
        setOpaque(true);
        return this;
    }
}
