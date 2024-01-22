/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import controller.Descriptor;
import controller.ViewController;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.JOptionPane;
import view.View;
import java.time.LocalDateTime;
import view.views.non_modal_views.DesktopView;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.ImageIcon;

/**
 *
 * @author colin
 */
public class ModalPatientNotesEditorView extends ModalView{
    
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalPatientNotesEditorView(
            View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setTitle("Patient notes editor");
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView);  
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setTitle("Patient notes editor");
        setVisible(true);
        addListeners();
        setNotepadEnabledColor(txaNotepad.getBackground());
        txaNotepad.setEnabled(false);
        txaNotepad.setBackground(getNotepadDisabledColor());
    }
    
    private Color notepadDisabledColor = new Color(220,220,220);;
    private Color notepadEnabledColor = null;
            
    private Color getNotepadDisabledColor(){
        return notepadDisabledColor;
    }
    
    private Color getNotepadEnabledColor(){
        return notepadEnabledColor;
    }
    
    private void setNotepadEnabledColor(Color value){
        notepadEnabledColor = value;
    }
    
    private void enableNotepad(){
        txaNotepad.setEnabled(true);
        txaNotepad.setBackground(getNotepadEnabledColor());
    }
    
    private void disableNotepad(){
        txaNotepad.setEnabled(false);
        txaNotepad.setBackground(getNotepadDisabledColor());
    }

    private void addListeners(){
        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try{
                        ModalPatientNotesEditorView.this.setClosed(true);
                    }
                    catch (PropertyVetoException ex){

                    }
                }
            }
        );
        btnClearNotepad.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Boolean result = okToClearNotepad("OK to clear notepad");
                    if (result!=null){
                        if (result){
                            txaNotepad.setText("");
                            dateTimePicker.clear();
                            disableNotepad();
                        } 
                    }
                    else {//empty 
                        txaNotepad.setText("");
                        dateTimePicker.clear();
                        disableNotepad();
                    }
                }
            }
        );
        this.btnAddNewNote.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Boolean result = okToClearNotepad("OK to clear notepad");
                    if (result!=null){
                        if (result){
                            enableNotepad();
                            txaNotepad.setText("");
                            dateTimePicker.setDateTimePermissive(LocalDateTime.now());
                        } 
                    }
                    else {//empty 
                        enableNotepad();
                        txaNotepad.setText("");
                        dateTimePicker.setDateTimePermissive(LocalDateTime.now());
                    }
                }
            }
        );
    }
 
    private Boolean okToClearNotepad(String message){
        Boolean result = null;
        if (!txaNotepad.getText().equals("")
                || dateTimePicker.datePicker.getDate()!=null){
            int reply = 0;
            String[] options = {"Yes", "No"};
            reply = JOptionPane.showOptionDialog(
                    ModalPatientNotesEditorView.this,
                    message,null,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    null);
            if (reply == JOptionPane.YES_OPTION) result = true;
            else result = false;
        }
        return result;
    }
    
    private void initComponents() {

        pnlNoteIndex = new javax.swing.JPanel();
        scrNoteIndex = new javax.swing.JScrollPane();
        pnlNotepad = new javax.swing.JPanel();
        scrNotepad = new javax.swing.JScrollPane();
        pnlOperations = new javax.swing.JPanel();
        btnAddNewNote = new javax.swing.JButton();
        btnSaveNoteChanges = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnClearNotepad = new javax.swing.JButton();

        pnlNoteIndex.setBorder(javax.swing.BorderFactory.createTitledBorder("Note index"));
        pnlNotepad.setBorder(javax.swing.BorderFactory.createTitledBorder("Notepad"));
        pnlOperations.setBorder(javax.swing.BorderFactory.createTitledBorder("Note actions"));

        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.setDisplaySpinnerButtons(true);
        //timeSettings.setInitialTimeToNow();

        
        dateTimePicker = new DateTimePicker(new DatePickerSettings(), timeSettings);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/datepickerbutton1.png"));
        datePickerButton = dateTimePicker.datePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(icon);
        icon = new ImageIcon(this.getClass().getResource("/zzz.jpg"));
        timePickerButton = dateTimePicker.timePicker.getComponentToggleTimeMenuButton();
        timePickerButton.setText("");
        timePickerButton.setIcon(icon);
        //dateTimePicker.setDateTimePermissive(LocalDateTime.now());
        txaNotepad = new javax.swing.JTextArea();
        txaNotepad.setColumns(20);
        txaNotepad.setRows(5);
        txaNotepad.setLineWrap(true);
        scrNotepad.setViewportView(txaNotepad);
        
        tblNotesIndex = new javax.swing.JTable();
        tblNotesIndex.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Date", "Notes"
            }
        ));
        scrNoteIndex.setViewportView(tblNotesIndex);

        btnClearNotepad.setText("<html>"
                + "<center>Clear</center>"
                + "<center>notepad</center>"
                + "</html>");
        
        btnAddNewNote.setText("<html>"
                + "<center>Create</center>"
                + "<center>new note</center>"
                + "<center>on notepad</center>"
                + "</html>");

        btnSaveNoteChanges.setText("<html>"
                + "<center>Save</center>"
                + "<center>notepad</center>"
                //+ "<center>notes<center>"
                + "</html>");

        btnCloseView.setText("<html>"
                + "<center>Close</center>"
                + "<center>patient</center>"
                + "<center>notes view</center>"
                + "</html>");

//<editor-fold defaultstate="collapsed" desc="Notes index panel layout">
        javax.swing.GroupLayout pnlNoteIndexLayout = new javax.swing.GroupLayout(pnlNoteIndex);
        pnlNoteIndex.setLayout(pnlNoteIndexLayout);
        pnlNoteIndexLayout.setHorizontalGroup(
            pnlNoteIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNoteIndexLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrNoteIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlNoteIndexLayout.setVerticalGroup(
            pnlNoteIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNoteIndexLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrNoteIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
//</editor-fold>       
//<editor-fold defaultstate="collapsed" desc="Notepad panel layout">
        javax.swing.GroupLayout pnlNotepadLayout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(pnlNotepadLayout);
        pnlNotepadLayout.setHorizontalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrNotepad)
                    .addGroup(pnlNotepadLayout.createSequentialGroup()
                        .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlNotepadLayout.setVerticalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotepadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Operations panel layout">       
        javax.swing.GroupLayout pnlOperationsLayout = new javax.swing.GroupLayout(pnlOperations);
        pnlOperations.setLayout(pnlOperationsLayout);
        pnlOperationsLayout.setHorizontalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddNewNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClearNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveNoteChanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    //.addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        pnlOperationsLayout.setVerticalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnAddNewNote, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30,30,30)
                .addComponent(btnClearNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30,30,30)
                .addComponent(btnSaveNoteChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(30,30,30)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
//</editor-fold>
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlNoteIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlOperations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlNoteIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(pnlOperations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnSaveNoteChanges;
    private javax.swing.JButton btnAddNewNote;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnClearNotepad;
    private javax.swing.JPanel pnlOperations;
    private javax.swing.JPanel pnlNoteIndex;
    private javax.swing.JPanel pnlNotepad;
    private javax.swing.JScrollPane scrNoteIndex;
    private javax.swing.JScrollPane scrNotepad;
    private javax.swing.JTextArea txaNotepad;
    private javax.swing.JTable tblNotesIndex;
    private com.github.lgooddatepicker.components.DateTimePicker dateTimePicker;
    private javax.swing.JButton datePickerButton;
    private javax.swing.JButton timePickerButton;
    // End of variables declaration      
}
