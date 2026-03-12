/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theclinic.view.support_classes.table_renderers;

import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 *
 * @author colin
 */
public class TableLocalTime24HourFormatCentredRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter _24HourFormat = DateTimeFormatter.ofPattern("HH:mm");
    
    public TableLocalTime24HourFormatCentredRenderer()
    {
        Font f = super.getFont();
        // plain
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            LocalTime startEndTime = (LocalTime)value;
            super.setText(startEndTime.format(_24HourFormat));
            super.setFont(getFont().deriveFont(Font.PLAIN));
            super.setHorizontalAlignment(JLabel.CENTER);
        }
        else super.setText("");
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        
        setOpaque(true);
        return this;
    }
}
