/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patient_view_original_class;

import model.non_entity.SystemDefinition;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/*28/03/2024import model.PatientNote;*/

/**
 *
 * @author colin
 */
public class AppointmentsTablePatientNoteRenderer extends JLabel implements TableCellRenderer{
    
    public AppointmentsTablePatientNoteRenderer()
    {
        Font f = super.getFont();
        // plain
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column)
    {
        PatientNote patientNote = null;
        if (value!=null){
            patientNote = (PatientNote)value;
            super.setText(patientNote.getNote());
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
