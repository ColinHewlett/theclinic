/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.support_classes.renderers;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class TableIntegerCenteredRenderer extends JLabel implements TableCellRenderer{
    
    public TableIntegerCenteredRenderer()
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
            Integer frequency = (Integer)value;
            if (frequency > 0){
                super.setText(String.valueOf(frequency));
                super.setFont(getFont().deriveFont(Font.PLAIN));
                super.setHorizontalAlignment(JLabel.CENTER);
            }else super.setText("");
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
