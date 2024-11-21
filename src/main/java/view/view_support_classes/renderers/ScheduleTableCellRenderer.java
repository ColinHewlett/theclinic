/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.renderers;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.JLabel;

/**
 *
 * @author colin
 */
public class ScheduleTableCellRenderer extends DefaultTableCellRenderer{
    
    @Override
    public Component getTableCellRendererComponent(
                        JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {
        JLabel result = null;
        result = (JLabel)super.getTableCellRendererComponent(
                    table, value, isSelected,hasFocus, row, column);
        if (value!=null) result.setToolTipText(value.toString());
        return result;
    }
}
