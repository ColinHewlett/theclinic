/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_renderers;

import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import theclinic.model.non_entity.Slot;
import theclinic.view.support_classes.table_models.ScheduleDiaryTableModel;

/**
 *
 * @author colin
 */
public class TableLocalDateTimeToLocalDateAndCentredRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter dateOnlyFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ScheduleDiaryTableModel model = null;
    private Slot slot = null;
    
    public TableLocalDateTimeToLocalDateAndCentredRenderer()
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
            LocalDate day = ((LocalDateTime)value).toLocalDate();
            super.setText(day.format(dateOnlyFormat));
            super.setFont(getFont().deriveFont(Font.PLAIN));
            super.setHorizontalAlignment(JLabel.CENTER);
        }

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
