/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.view_support_classes.renderers;
import view.views.non_modal_views.NotificationView;
import model.entity.Notification;
import view.view_support_classes.models.NotificationView4ColumnTableModel;
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
public class NotificationTableKeyColorsAndLocalDateRenderer extends JLabel implements TableCellRenderer{
//public class NotificationTableKeyColorsAndLocalDateRenderer extends javax.swing.table.DefaultTableCellRenderer{  
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private LocalDate date = null;
    boolean isNotificationDateOverdue = false;
    boolean isNotificationDateNow = false;
    private NotificationView  view = null;
    
    public NotificationTableKeyColorsAndLocalDateRenderer(){
        Font f = super.getFont();
        // bold
        this.setFont(f.deriveFont(f.getStyle() | ~Font.PLAIN));
        setOpaque(true);
    }
            
    
    private NotificationView getView(){
        return view;
    }
    
    public void setView(NotificationView value){
        view = value;
    }
    
    private final Color notDueYetNotificationColour = new Color(0,0,255);
    private final Color overdueNotificationColour = new Color(255,0,0);
    private final Color actionedNotificationColour = new Color(0,0,0);
    
    public Color getActionedNotificationColour(){
        return actionedNotificationColour;
    }
    
    public Color getNotDueYetNotificationColour(){
        return notDueYetNotificationColour;
    }
    
    public Color getOverdueNotificationColour(){
        return overdueNotificationColour;
    }
    
    public boolean getIsNotificationDateOverdue(){
        return isNotificationDateOverdue;
    }
    

     public void setIsNotificationDateOverdue(boolean value){
        isNotificationDateOverdue = value;
    }
    
    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column){
        NotificationView4ColumnTableModel model = (NotificationView4ColumnTableModel)table.getModel();
        Notification notification  = model.getElementAt(row);
        if (value != null){
            switch(column){
                case 0:
                    date = (LocalDate)value;
                    super.setText(date.format(ddmmyy));
                    if (date.compareTo(LocalDate.now()) >= 0)
                        setIsNotificationDateOverdue(false);
                    else
                        setIsNotificationDateOverdue(true);
                    if (notification.getIsActioned())
                        super.setForeground(getActionedNotificationColour());
                    else if (getIsNotificationDateOverdue())
                        super.setForeground(getOverdueNotificationColour());
                    else super.setForeground(getNotDueYetNotificationColour());
                    break;
                default:
                    super.setText(value.toString());
                    if (date.compareTo(LocalDate.now()) >= 0)
                        setIsNotificationDateOverdue(false);
                    else
                        setIsNotificationDateOverdue(true);
                    if (notification.getIsActioned())
                        super.setForeground(getActionedNotificationColour());
                    else if (getIsNotificationDateOverdue())
                        super.setForeground(getOverdueNotificationColour());
                    else super.setForeground(getNotDueYetNotificationColour());
                    break;
            }
              
        }
        
        if (isSelected){
            setBackground(table.getSelectionBackground()); 
            setForeground(this.getForeground()); 
        }
        else{
            setBackground(table.getBackground());
            setForeground(this.getForeground());
        }
        return this;
    }
}

