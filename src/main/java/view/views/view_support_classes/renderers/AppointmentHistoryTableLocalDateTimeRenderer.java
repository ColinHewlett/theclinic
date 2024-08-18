/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.view_support_classes.renderers;

import java.awt.Component;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */

public class AppointmentHistoryTableLocalDateTimeRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter ddMMyyhhmm12Format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm ");
    
    public AppointmentHistoryTableLocalDateTimeRenderer()
    {
        super();
        setFont(getFont().deriveFont(Font.PLAIN));
        //Font f = super.getFont();
        // plain
        //this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        setFont(getFont().deriveFont(Font.PLAIN));
        if (value != null){
            LocalDateTime startTime = (LocalDateTime)value;
            super.setText(startTime.format(ddMMyyhhmm12Format));
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