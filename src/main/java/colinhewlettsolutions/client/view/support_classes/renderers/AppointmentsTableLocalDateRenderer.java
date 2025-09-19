/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class AppointmentsTableLocalDateRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter ddMMyyyyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public AppointmentsTableLocalDateRenderer()
    {
        //Font f = super.getFont();
        // bold
        //this.setFont(f.deriveFont(f.getStyle() | ~Font.BOLD));;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            LocalDate date = (LocalDate)value;
            super.setText(date.format(ddMMyyyyFormat));
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
