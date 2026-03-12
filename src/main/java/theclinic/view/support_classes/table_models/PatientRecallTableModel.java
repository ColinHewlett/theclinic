/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes.table_models;

import theclinic.model.entity.Patient;
import theclinic.model.entity.PatientAppointmentData;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author colin
 */
public class PatientRecallTableModel  extends AbstractTableModel{
    private ArrayList<PatientAppointmentData> data = null;
    private enum COLUMN{ActionRecallNonGBT,
                        ActionRecallGBT,
                        Patient, 
                        LastAppointment,
                        Treatment,
                        Phone,
                        Email,
                        RecallNonGBTDate,
                        RecallGBTDate,
                        RecallNonGBTLastSentDate,
                        RecallGBTLastSentDate
                        };
    private final Class[] columnClass = new Class[] {
        Boolean.class,
        Boolean.class,
        Patient.class,
        LocalDate.class, 
        String.class,
        String.class,
        String.class, 
        LocalDate.class,
        LocalDate.class,
        LocalDate.class,
        LocalDate.class
        };

    public PatientRecallTableModel(){
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
                else if(result.equals("RecallNonGBTDate"))
                    result = "non-GBT";
                else if(result.equals("RecallGBTDate"))
                    result = "GBT";
                else if(result.equals("RecallNonGBTLastSentDate"))
                    result = "non-GBT";
                else if(result.equals("RecallGBTLastSentDate"))
                    result = "GBT";
                else if(result.equals("ActionRecallNonGBT"))
                    result = "non-GBT";
                else if(result.equals("ActionRecallGBT"))
                    result = "GBT"; 
                    //result = "<html><center>Send recall</center><center> to patient?</center></html>";
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
        int temp = 0;
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
                            if (pad.getAppointment().getIsKeyDefined())
                                result = pad.getAppointment().getStart().toLocalDate();
                            else result = null;
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
                        case RecallNonGBTDate:
                            result = pad.getPatient().getRecall().getDentalDate();
                            break;
                        case RecallGBTDate:
                            result = pad.getPatient().getRecall().getGBTDate();
                            break;
                        case RecallNonGBTLastSentDate:
                            result = pad.getPatient().getLastNonGBTRecallSentDate();
                            break;
                        case RecallGBTLastSentDate:
                            result = pad.getPatient().getLastGBTRecallSentDate();
                            break;
                        case ActionRecallNonGBT:
                            result = pad.getPatient().getIsRequestToSendPatientNonGBTRecallPending();
                            break;
                        case ActionRecallGBT:
                            result = pad.getPatient().getIsRequestToSendPatientGBTRecallPending();
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
    
    /**
     * if on entry the value is true
     * -- case selected column is 0 (non-GBT recall pending status), 
     * -- -- if current value of the patient's non-GBT recall pending status is false
     * -- -- -- the patient's non-GBT pending status is set to true
     * -- -- -- and the patient's GBT pending status is set to false (the non-GBT and GBT pending status cannot both be true)
     * -- -- else current value of patient's non-GBT pending status is set to false
     * -- case selected column is 1 (GBT recall pending status), 
     * -- -- if current value of the patient's GBT recall pending status is false
     * -- -- -- the patient's GBT pending status is set to true
     * -- -- -- and the patient's non-GBT pending status is set to false (the non-GBT and GBT pending status cannot both be true)
     * -- -- else current value of patient's non-GBT pending status is set to false
     * @param value value entered selected cell
     * @param row row value of selected cell
     * @param col column value of selected cell
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        System.out.println("object = " + value);
        PatientAppointmentData pad = data.get(row);
        Boolean newValue = null;
        newValue = (Boolean)value;
        switch(col){
            case 0 ->{
                if(newValue) {
                    if (!pad.getPatient().getIsRequestToSendPatientNonGBTRecallPending()) {
                        pad.getPatient().setIsRequestToSendPatientNonGBTRecallPending(true);
                        pad.getPatient().setIsRequestToSendPatientGBTRecallPending(false);
                    }
                }else pad.getPatient().setIsRequestToSendPatientNonGBTRecallPending(false);
                break;
            }
            case 1 ->{
                if(newValue) {
                    if (!pad.getPatient().getIsRequestToSendPatientGBTRecallPending()) {
                        pad.getPatient().setIsRequestToSendPatientGBTRecallPending(true);
                        pad.getPatient().setIsRequestToSendPatientNonGBTRecallPending(false);
                    }
                }else pad.getPatient().setIsRequestToSendPatientGBTRecallPending(false);
                break;
            }
        }
        if (newValue!=null){
            fireTableCellUpdated(row, col);
        }
         
    }
}
