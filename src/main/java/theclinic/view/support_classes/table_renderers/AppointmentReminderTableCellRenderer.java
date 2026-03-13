/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_renderers;

import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JLabel;
/**
 *
 * @author colin
 */
public class AppointmentReminderTableCellRenderer extends JLabel implements TableCellRenderer{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        /*
        if (isSelected){
            //setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground()); 
            setBackground(table.getSelectionBackground());
        }
        else{
            setBackground(table.getBackground());
            setForeground(this.getForeground());
        }
*/
        //setOpaque(true);
        return this;
    }

}
