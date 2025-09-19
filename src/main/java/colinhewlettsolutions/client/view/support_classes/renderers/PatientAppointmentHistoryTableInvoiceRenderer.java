/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.view.support_classes.renderers;

import colinhewlettsolutions.client.model.entity.Invoice;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class PatientAppointmentHistoryTableInvoiceRenderer extends JLabel implements TableCellRenderer{
    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        if (value != null){
            Invoice invoice = (Invoice)value;
            if (invoice.getKey() != 0) super.setText(invoice.getKey().toString());
            else super.setText("--");
        }
        else super.setText("--");
        
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
