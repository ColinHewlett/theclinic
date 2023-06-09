/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views.view_support_classes.renderers;
import clinicpms.model.Notification;
import clinicpms.view.views.view_support_classes.models.PatientNotificationView4ColumnTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author colin
 */
public class PatientNotificationTableOverdueLocalDateRenderer extends JLabel implements TableCellRenderer{
//public class PatientNotificationTableOverdueLocalDateRenderer extends javax.swing.table.DefaultTableCellRenderer{  
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private LocalDate date = null;
    boolean isNotificationDateOverdue = false;
    boolean isNotificationDateNow = false;
    
    public boolean getIsNotificationDateOverdue(){
        return isNotificationDateOverdue;
    }
    
    public boolean getIsNotificationDateNow(){
        return isNotificationDateNow;
    }
    
     public void setIsNotificationDateOverdue(boolean value){
        isNotificationDateOverdue = value;
    }
    
    public void setIsNotificationDateNow(boolean value){
        isNotificationDateNow = value;
    }
    
    public PatientNotificationTableOverdueLocalDateRenderer(){
        //Font font = super.getFont();
        //super.setFont(font.deriveFont(font.getStyle()|~Font.BOLD));
        //super.setFont(font.deriveFont(font.getStyle()|~Font.ITALIC));
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column){
        PatientNotificationView4ColumnTableModel model = (PatientNotificationView4ColumnTableModel)table.getModel();
        Notification notification  = model.getElementAt(row);
//Font font = super.getFont();
        //super.setFont(font.deriveFont(font.getStyle()|~Font.BOLD));
        //super.setFont(font.deriveFont(font.getStyle()|~Font.ITALIC));
        if (value != null){
            switch(column){
                case 0:
                    date = (LocalDate)value;
                    super.setText(date.format(ddmmyy));
                    if (date.compareTo(LocalDate.now()) == 0) { //notification due today
                        isNotificationDateOverdue  = false;
                        isNotificationDateNow  = true;
                        super.setForeground(Color.RED);
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        }
                    }
                    else if (date.compareTo(LocalDate.now()) < 0) { //notification overdue
                        isNotificationDateNow  = false;
                        isNotificationDateOverdue = true;
                        super.setForeground(Color.BLUE);
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        } 
                    }
                    else {
                        super.setForeground(Color.BLACK);
                        isNotificationDateOverdue = false;
                        isNotificationDateNow  = false;
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        }
                    }
                    break;
                default:
                    super.setText(String.valueOf(value));
                    if (isNotificationDateNow){
                        super.setForeground(Color.RED);
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        }
                    }
                    else if (isNotificationDateOverdue) { //notification due today
                        super.setForeground(Color.BLUE);
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        }
                    }
                    else{
                        super.setForeground(Color.BLACK);
                        if (notification.getIsActioned()){
                            super.setForeground(Color.GREEN);
                        }
                    }
                    break;
            }
        }
        
        if (isSelected){
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground()); 
        }
        else{
            setBackground(table.getBackground());
            setForeground(this.getForeground());
            /*
            super.setFont(font.deriveFont(font.getStyle()|~Font.ITALIC));
            if (isNotificationDateOverdue || isNotificationDateNow){
                super.setFont(font.deriveFont(font.getStyle()|~Font.BOLD));
            }
*/
        }
        //table.repaint();
        return this;
    }
}

