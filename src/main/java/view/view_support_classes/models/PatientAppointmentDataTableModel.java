/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.view_support_classes.models;

import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import model.entity.Patient;
import model.entity.PatientAppointmentData;

/**
 *
 * @author colin
 */
public class PatientAppointmentDataTableModel extends DefaultTableModel{
    private ArrayList<PatientAppointmentData> data = null;
    private enum COLUMN{Patient, LastAppointment,Treatment,Phone,Email,RecallDate,RecallFrequency,GBTRecallDate,GBTRecallFrequency};
    private final Class[] columnClass = new Class[] {
        Patient.class,
        LocalDate.class, 
        String.class,
        String.class,
        String.class, 
        LocalDate.class,
        Integer.class,
        LocalDate.class,
        Integer.class
        };

    public PatientAppointmentDataTableModel(){
        data = new ArrayList<>();
        
    }
    
    public ArrayList<PatientAppointmentData> getPatientAppointmentAnalyses(){
        return this.data;
    }

    public void removeAllElements(){
        data.clear();
        this.fireTableDataChanged();
    }
    
    public void setData(ArrayList<PatientAppointmentData> newData){
        data = newData;
        fireTableDataChanged();
    }
    
    public PatientAppointmentData getElementAt(int row){
        return data.get(row);
    }

    @Override
    public int getRowCount(){
        int result;
        if (data!=null) result = data.size();
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
                if (result.equals("LastAppointment"))
                    result = "<html><center>Last</center><center>booking</center></html>";
                else if(result.equals("Phone"))
                    result = "Phone(s)";
                else if(result.equals("RecallDate"))
                    result = "<html><center>Recall</center><center>date</center></html>";
                else if(result.equals("RecallFrequency"))
                    result = "<html><center>Recall</center><center>frequency</center></html>";
                else if(result.equals("GBTRecallDate"))
                    result = "<html><center>GBT recall</center><center>date</center></html>";
                else if(result.equals("GBTRecallFrequency"))
                    result = "<html><center>GBT recall</center><center>frequency</center></html>";
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
    public Object getValueAt(int row, int columnIndex){
        Object result = null;
        PatientAppointmentData pad = getPatientAppointmentAnalyses().get(row);
        for (COLUMN column: COLUMN.values()){
            if (column.ordinal() == columnIndex){
                if (pad == null){
                    return null;
                }
                else{
                    switch (column){
                        case Patient:
                            result = pad.getPatient();
                            break;
                        case LastAppointment:
                            result = pad.getAppointment().getStart().toLocalDate();
                            break;
                        case Treatment:
                            result = pad.getAppointment().getNotes();
                            break;
                        case Phone:
                            String patient_phone = "";
                            if (!pad.getPatient().getPhone1().trim().isEmpty()){
                                patient_phone = pad.getPatient().getPhone1().trim();
                                if (!pad.getPatient().getPhone2().trim().isEmpty()){
                                    patient_phone = patient_phone + " / " + pad.getPatient().getPhone2().trim();
                                }
                            }else if (!pad.getPatient().getPhone2().trim().isEmpty()){
                                patient_phone = pad.getPatient().getPhone2().trim();
                            }
                            result = patient_phone;
                            break;
                        case Email:
                            result = pad.getPatient().getEmail();
                            break;
                        case RecallDate:
                            result = pad.getPatient().getRecall().getDentalDate();
                            break;
                        case RecallFrequency:
                            result = pad.getPatient().getRecall().getDentalFrequency();
                            break;
                        case GBTRecallDate:
                            result = pad.getPatient().getRecall().getGBTDate();
                            break;
                        case GBTRecallFrequency:
                            result = pad.getPatient().getRecall().getGBTFrequency();
                            break;
                        /*case ClinicalNote:
                            result = pad.getClinicalNote().getNotes().substring(0,15);
                            break;*/
                    }
                    break;
                }
            }
        }
        return result;
    }
}
