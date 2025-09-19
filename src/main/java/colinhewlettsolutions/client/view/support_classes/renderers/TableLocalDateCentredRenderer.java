/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import colinhewlettsolutions.client.model.non_entity.Slot;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.support_classes.models.ScheduleDiaryTableModel;

/**
 *
 * @author colin
 */
public class TableLocalDateCentredRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter dateOnlyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    public TableLocalDateCentredRenderer()
    {
        Font f = super.getFont();
         //plain
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            LocalDate day = (LocalDate)value;
            super.setText(day.format(dateOnlyFormat));
            super.setFont(getFont().deriveFont(Font.PLAIN));
            super.setHorizontalAlignment(JLabel.CENTER);
        }else super.setText("");

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
