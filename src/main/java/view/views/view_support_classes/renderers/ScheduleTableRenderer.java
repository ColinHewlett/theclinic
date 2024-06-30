/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.renderers;
import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import model.entity.Appointment;
import model.non_entity.SystemDefinition;
import view.views.view_support_classes.models.AppointmentScheduleTableModel;

/**
 *
 * @author colin
 */
public class ScheduleTableRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        AppointmentScheduleTableModel model = (AppointmentScheduleTableModel)table.getModel();
        Appointment appointment = model.getElementAt(row);
        // Custom rendering logic
        if (column==4){

            if(appointment.getIsEmergency()){
                value = SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
                c.setForeground(Color.red);
            }
        }

        return c;
    }
}
