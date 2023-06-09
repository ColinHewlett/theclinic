/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clinicpms.view.views.view_support_classes.models;

//import clinicpms.view.views.schedule_contact_details_view.*;
import clinicpms.model.Notification;
import clinicpms.controller.Descriptor;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author colin
 */
public class PatientNotificationView2ColumnTableModel extends DefaultTableModel{
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ArrayList<Notification> patientNotifications = null;
    private enum COLUMN{Date, Notification};
    private final Class[] columnClass = new Class[] {
        LocalDate.class,
        String.class};
    
    public PatientNotificationView2ColumnTableModel(){
        patientNotifications = new ArrayList<Notification>();
    }
        
    public ArrayList<Notification> getPatientNotifications(){
        return patientNotifications;
    }
    
    public void addElement(Notification patientNotification){
        patientNotifications.add(patientNotification);
    }
    
    public void removeAllElements(){
        patientNotifications.clear();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount(){
        int result;
        if (patientNotifications!=null) result = patientNotifications.size();
        else result = 0;
        return result;
    }

    @Override
    public int getColumnCount(){
        return COLUMN.values().length;
    }
    @Override
    public String getColumnName(int columnIndex){
        String result = null;
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                result = column.toString();
                break;
            }
        }
        return result;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex){
        return columnClass[columnIndex];
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        /*
        if (col==5){
            if ((Boolean)value)appointments.get(row).setHasBeenSelected(Boolean.TRUE);
            else appointments.get(row).setHasBeenSelected(Boolean.FALSE);
            fireTableCellUpdated(row, col);
        }
        */
        
    }

    @Override
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        Notification patientNotification = (Notification)getPatientNotifications().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (patientNotification == null){
                    return null;
                }
                else{
                    LocalDate date = patientNotification.getNotificationDate();
                    String notification = patientNotification.getNotificationText();
                    
                    switch (column){
                        case Date:
                            //result = date.format(ddmmyy);
                            result = date;
                            break;
                        case Notification:
                            result = notification;
                            break;
                    }
                    break;
                }
            }
        }
        return result;
    }
    
}
