/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.non_entity;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.text.StyleConstants;
import java.time.LocalDate;
/**
 *
 * @author colin
 */
public class DatePickerInDialog {
    
    private DatePicker datePicker = null;
    private DatePicker getDatePicker(){
        return datePicker;
    }
    private void setDatePicker(DatePicker value){
        datePicker = value;
    }
    
    private Frame parentFrame = null;
    private Frame getParentFrame(){
        return parentFrame;
    }
    private void setParentFrame(Frame value){
        parentFrame = value;
    }
    
    private LocalDate selectedDate = null;
    public LocalDate getSelectedDate(){
        return selectedDate;
    }
    private void setSelectedDate(LocalDate value){
        selectedDate = value;
    }
    
    public DatePickerInDialog(Frame parentFrame){
        //datePicker.addDateChangeListener(this);
        setParentFrame(parentFrame);
        
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra(DateTimeFormatter.ofPattern("E MMM dd, yyyy"));
        settings.setAllowKeyboardEditing(true);
        setDatePicker(new DatePicker(settings));
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        JButton datePickerButton = getDatePicker().getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
    }

    public void showDatePickerDialog() {
        // Create a dialog
        JDialog dialog = new JDialog(parentFrame, "Select schedule date", true);
        dialog.setSize(200,95);
        dialog.setLayout(new BorderLayout());
        
        JButton selectButton = new JButton("Close");
        selectButton.addActionListener(e -> {
            setSelectedDate(getDatePicker().getDate());
            dialog.dispose();
        });
        
        getDatePicker().setDateToToday();
        getDatePicker().getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
        getDatePicker().getComponentDateTextField().setFocusable(false);
        // Add components to the dialog
        dialog.add(getDatePicker(), BorderLayout.CENTER);
        dialog.add(selectButton, BorderLayout.SOUTH);
        
        // Display the dialog
        dialog.setLocationRelativeTo(getParentFrame());
        dialog.setVisible(true);  
    }
}
