/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.renderers;

import model.entity.Patient;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class TablePatientEmboldenedRenderer extends JLabel implements TableCellRenderer{
    
    public TablePatientEmboldenedRenderer()
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
            Patient patient = (Patient)value;
            super.setText(patient.toString());
            super.setFont(getFont().deriveFont(Font.BOLD));
            super.setHorizontalAlignment(JLabel.LEFT);
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
