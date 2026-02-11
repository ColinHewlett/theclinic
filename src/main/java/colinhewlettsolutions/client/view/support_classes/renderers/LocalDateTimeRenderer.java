/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

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
public class LocalDateTimeRenderer extends JLabel implements TableCellRenderer {
    private DateTimeFormatter dateOnlyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public LocalDateTimeRenderer(){
        Font f = super.getFont();
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            LocalDateTime startTime = (LocalDateTime)value;
            super.setText(startTime.format(dateOnlyFormat));
            //super.setFont(getFont().deriveFont(Font.BOLD));
            super.setHorizontalAlignment(JLabel.CENTER);
        }
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        setOpaque(true);
        return this;
    }
}
