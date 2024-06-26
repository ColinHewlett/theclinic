/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.view_support_classes.renderers;

import model.entity.Patient;
import model.non_entity.SystemDefinition;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class AppointmentsTablePatientRenderer  extends JLabel implements TableCellRenderer{
    private boolean isUnbookable = false;
    public AppointmentsTablePatientRenderer()
    {
        Font f = super.getFont();
        // bold
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    private boolean getIsUnbookable(){
        return isUnbookable;
    }
    
    private void setIsUnbookable(boolean value){
        isUnbookable = value;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        
        Patient patient = (PatientDelegate)value;
        if (patient == null) {
            super.setText("AVAILABLE SLOT");
            super.setHorizontalAlignment(JLabel.CENTER);
            setIsUnbookable(false);

        }
        else if (patient.toString().equals(SystemDefinition.APPOINTMENT_UNBOOKABILITY_MARKER)){
            super.setText("<< U N B O O K A B L E  S L O T >>");
            super.setForeground(Color.RED);
            super.setHorizontalAlignment(JLabel.CENTER);
            setIsUnbookable(true);
        }
        else {
            super.setText(patient.toString());
            super.setHorizontalAlignment(JLabel.LEFT);
            super.setForeground(Color.BLACK);
            setIsUnbookable(false);
            
        }
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            if (getIsUnbookable()) setForeground(Color.RED);
            else setForeground(table.getForeground());
        }
        setOpaque(true);
        return this;
    }
}
