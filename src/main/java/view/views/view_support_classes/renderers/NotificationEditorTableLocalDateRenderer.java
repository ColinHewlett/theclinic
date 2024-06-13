/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.views.view_support_classes.renderers;

import model.non_entity.SystemDefinition;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import model.entity.Notification;
import view.views.modal_views.ModalNotificationEditorView;
import view.views.non_modal_views.NotificationView;
import view.views.view_support_classes.models.PatientNotificationView2ColumnTableModel;

/**
 *
 * @author colin
 */
public class NotificationEditorTableLocalDateRenderer extends JLabel implements TableCellRenderer{
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private LocalDate date = null;
    boolean isNotificationDateOverdue = false;
    boolean isNotificationDateNow = false;
    private NotificationView  view = null;
    
    public NotificationEditorTableLocalDateRenderer(){
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
        PatientNotificationView2ColumnTableModel model = (PatientNotificationView2ColumnTableModel)table.getModel();
        Notification notification  = model.getElementAt(row);
        if (value != null){
            switch(column){
                case 0:
                    ModalNotificationEditorView.setDate((LocalDate)value);
                    super.setText(ModalNotificationEditorView.getDate().format(ddmmyy));
                    if (ModalNotificationEditorView.getDate().compareTo(LocalDate.now()) >= 0)
                        setIsNotificationDateOverdue(false);
                    else
                        setIsNotificationDateOverdue(true);
                    if (notification.getIsActioned())
                        super.setForeground(getActionedNotificationColour());
                    else if (getIsNotificationDateOverdue())
                        super.setForeground(getOverdueNotificationColour());
                    else super.setForeground(getNotDueYetNotificationColour());
                    break;
                case 1:
                    super.setText(value.toString());
                    if (ModalNotificationEditorView.getDate().compareTo(LocalDate.now()) >= 0)
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
