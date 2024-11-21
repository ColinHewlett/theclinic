/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import model.entity.Appointment;
import model.entity.Patient;
import model.non_entity.SystemDefinition;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKABLE_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.BOOKED_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.EMERGENCY_SCHEDULE_SLOT;
import static model.non_entity.SystemDefinition.ScheduleSlotType.UNBOOKABLE_SCHEDULE_SLOT;
import view.view_support_classes.models.AppointmentScheduleTableModel;
import view.view_support_classes.models.CancelledAppointmentsTableModel;

/**
 *
 * @author colin
 */
public class CancelledAppointmentsTablePatientRenderer extends JLabel implements TableCellRenderer{
    private Appointment appointment = null;
    public CancelledAppointmentsTablePatientRenderer()
    {
        Font f = super.getFont();
        // bold
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
        CancelledAppointmentsTableModel model = 
                (CancelledAppointmentsTableModel)table.getModel();
        appointment = model.getElementAt(row);
        Patient patient = (Patient)value;
        super.setText(patient.toString());
        super.setHorizontalAlignment(JLabel.LEFT);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            setBackground(table.getBackground());
            if (appointment.getIsEmergency()) setForeground(Color.RED );
            else setForeground(table.getForeground());
        }
       
        setOpaque(true);
        return this;
    }
}
