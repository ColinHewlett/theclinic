/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.view_support_classes.models;

//import view.views.schedule_contact_details_view.*;
import model.entity.Patient;
import model.entity.Notification;
import controller.Descriptor;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author colin
 */
public class PatientNotificationViewTableModel extends DefaultTableModel{
    private DateTimeFormatter ddmmyy = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ArrayList<Notification> patientNotifications = null;
    private enum COLUMN{Actioned, Date, Patient,Notification};
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        LocalDate.class,
        Patient.class, 
        String.class};
    
    public PatientNotificationViewTableModel(){
        patientNotifications = new ArrayList<Notification>();
    }
        
    public ArrayList<Notification> getPatientNotifications(){
        return patientNotifications;
    }
    
    public void addElement(Notification patientNotification){
        patientNotifications.add(patientNotification);
    }
    
    public Notification getElementAt(int row){
        return patientNotifications.get(row);
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
                if (result.equals("Actioned"))
                    result = result + "?";
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
        if (col==0){
            if ((Boolean)value)patientNotifications.get(row).setIsActioned(Boolean.TRUE);
            else patientNotifications.get(row).setIsActioned(Boolean.FALSE);
            fireTableCellUpdated(row, col);
        }   
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
                    Patient patient = patientNotification.getPatient();
                    String notification = patientNotification.getNotificationText();
                    Boolean actionedStatus = patientNotification.getIsActioned();
                    
                    switch (column){
                        case Actioned:
                            result = actionedStatus;
                            break;
                        case Date:
                            //result = date.format(ddmmyy);
                            result = date;
                            break;
                        case Patient:
                            result = patient;
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

