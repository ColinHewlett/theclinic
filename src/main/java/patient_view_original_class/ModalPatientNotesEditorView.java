/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package patient_view_original_class;

import controller.Descriptor;
import controller.ViewController;
/*28/03/2024import model.PatientNote;*/
import view.views.modal_views.ModalView;
import model.entity.Patient;
import java.awt.Color;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.JOptionPane;
import view.View;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import view.views.non_modal_views.DesktopView;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateTimeChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateTimeChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import view.views.view_support_classes.models.PatientNote2ColumnTableModel;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;


/**
 *
 * @author colin
 */
public class ModalPatientNotesEditorView extends ModalView 
                                         implements PropertyChangeListener{
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
        ViewController.setJTableColumnProperties(
                tblNotesIndex, scrNoteIndex.getPreferredSize().width, 25,75);
        setVisible(true);
        addListeners();
        dateTimePicker.addDateTimeChangeListener(new DateTimePickerChangeListener());
        
        Patient patient = getMyController()
                .getDescriptor().getControllerDescription().getPatient();
        setTitle("Patient notes editor for " + patient.toString());
        setPatient(patient);
        setNotepad(new Notepad());
        getNotepad().shut();
        //txaNotepad.setEnabled(false);
        //txaNotepad.setBackground(getNotepadDisabledColor());
        setViewMode(ViewController.ViewMode.UPDATE);
        /*28/03/2024ArrayList<PatientNote> patientNotes = getMyController()
                .getDescriptor().getControllerDescription().getPatientNotes();*/
        setNotesIndexTableListener();
        /*28/03/2024populateNotesIndexTable(patientNotes); */
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.PatientViewControllerPropertyChangeEvent propertyName =
                ViewController.PatientViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case PATIENT_NOTES_RECEIVED:
                /*28/03/2024populateNotesIndexTable(getMyController()
                        .getDescriptor()
                        .getControllerDescription()
                        .getPatientNotes());*/
                break;
        }
    }
    
    private enum State{OPEN,SHUT};
    
    private Notepad notepad = null;
    private void setNotepad(Notepad value){
        notepad = value;
    }
    private Notepad getNotepad(){
        return notepad;
    }
    /*28/03/2024
    private PatientNote patientNote = null;
    private PatientNote getPatientNote(){
        return patientNote;
    }
    private void setPatientNote(PatientNote value){
        patientNote = value;
    }*/
    
    private Patient patient = null;
    private Patient getPatient(){
        return patient;
    }
    private void setPatient(Patient value){
        patient = value;
    }

    
    
    
    
    
    
    private ViewController.ViewMode viewMode = null;
    
    private ViewController.ViewMode getViewMode(){
        return viewMode;
    }
    
    private void setViewMode(ViewController.ViewMode value){
        viewMode = value;
    }
    
    private void addListeners(){
        /*28/03/2024ArrayList<PatientNote> patientNotes = new ArrayList<>();*/
        PatientNote2ColumnTableModel model = 
                (PatientNote2ColumnTableModel)tblNotesIndex.getModel();
        /*28/03/2024PatientNote patientNote;*/

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
                Boolean result = okToClearNotepad("OK to lose current notepad contents");
                if (result){
                    getNotepad().shut();
                }
            }
        });
        this.btnAddNewNote.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Boolean result = okToClearNotepad("Contents in notepad will be lost. OK to proceed?");
                    if (result){
                        getNotepad().shut();
                        getNotepad().open(null);

                    }
                }
            }
        );
        this.btnSaveNoteChanges.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PatientNote patientNote;
                boolean result = true;
                if (getNotepad()!=null){
                    if (getNotepad().isOpen()){
                        if (txaNotepad.getText().trim().length() == 0){
                            int reply = 0;
                            String[] options = {"Yes", "No"};
                            reply = JOptionPane.showOptionDialog(
                                    ModalPatientNotesEditorView.this,
                                    "Nothing written in notepad; OK to save it?",null,
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE,
                                    null,
                                    options,null);
                            if (reply == JOptionPane.YES_OPTION) result = true;
                            else result = false;
                        }
                    }
                }
                if (result){
                    if (getViewMode()== null) setViewMode(ViewController.ViewMode.UPDATE);
                    getNotepad().save();
                    
                }   
            }
                      
        });
        this.btnNextNote.addActionListener(new java.awt.event.ActionListener() {
            int row;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*28/03/2024if (!model.getPatientNotes().isEmpty()){//any rows in the table
                    row = tblNotesIndex.getSelectedRow();
                    if ((row+1) < model.getPatientNotes().size()){
                        tblNotesIndex.setRowSelectionInterval(row+1,row+1);
                        tblNotesIndex.scrollRectToVisible(
                                new Rectangle(tblNotesIndex.
                                        getCellRect(row+1, 0, true)));
                    }
                }*/
            }
        });
        this.btnPreviousNote.addActionListener(new java.awt.event.ActionListener() {
            int row;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*28/03/2024if (!model.getPatientNotes().isEmpty()){//any rows in the table
                    row = tblNotesIndex.getSelectedRow();
                    if ((row-1) >= 0){
                        tblNotesIndex.setRowSelectionInterval(row-1,row-1);
                        //following code auto moves the vertical scrool bar 
                        //when next row selected would be off scroll pane
                        tblNotesIndex.scrollRectToVisible(
                                new Rectangle(tblNotesIndex.
                                        getCellRect(row-1, 0, true)));
                    }
                }*/
            }
        });
    }
    
    private Boolean okToClearNotepad(String message){
        Boolean result = null;
        if ((getNotepad().getContent().trim().length() > 0)
                || getNotepad().getDatestamp()!=null){
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
        }else result = true;
        return result;
    }
    
    private void setNotesIndexTableListener(){
        this.tblNotesIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblNotesIndex.getSelectionModel();
        lsm.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;

                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    doPatientNotesIndexTableRowSelection(selectedRow);
                }
            }
        });
    }
    
    private void doPatientNotesIndexTableRowSelection(int row){
        /*28/03/2024PatientNote patientNote = 
                ((PatientNote2ColumnTableModel)this.tblNotesIndex.getModel()).getElementAt(row);*/
        Notepad notepad = getNotepad();
        notepad.shut();
        /*
        if (notepad.isOpen()){
            Boolean result = okToClearNotepad("Current notepad cotents would be lost. Proceed?");
            if (result){
                getNotepad().shut();
            }
        }
        */
        /*28/03/2024setPatientNote(patientNote);
        notepad.open(patientNote);
        notepad.setDatestamp(patientNote.getDatestamp());
        notepad.setContent(patientNote.getNote());*/
        setViewMode(ViewController.ViewMode.UPDATE);
        
    }
    /*28/03/2024
    private void populateNotesIndexTable(ArrayList<PatientNote> patientNotes){
        if (patientNotes==null) patientNotes = new ArrayList<PatientNote>();
        PatientNote2ColumnTableModel model;
        
        if (this.tblNotesIndex==null){
            this.tblNotesIndex = new JTable(new PatientNote2ColumnTableModel());
            scrNoteIndex.setViewportView(this.tblNotesIndex);
            //this.scrNoteIndex.remove(this.tblNotesIndex);   
        }

        model = (PatientNote2ColumnTableModel)this.tblNotesIndex.getModel();
        model.removeAllElements();
        Iterator<PatientNote> it = patientNotes.iterator();
        while (it.hasNext()){
            ((PatientNote2ColumnTableModel)this.tblNotesIndex.getModel()).addElement(it.next());
        }

        JTableHeader tableHeader = this.tblNotesIndex.getTableHeader();
        tableHeader.setBackground(new Color(220,220,220));
        tableHeader.setOpaque(true);
        
        TableColumnModel columnModel = this.tblNotesIndex.getColumnModel();
        columnModel.getColumn(0).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
        columnModel.getColumn(1).setHeaderRenderer(new TableHeaderCellBorderRenderer(Color.LIGHT_GRAY));
    }*/
    
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
        btnNextNote = new javax.swing.JButton();
        btnPreviousNote = new javax.swing.JButton();

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
        /*28/03/2024tblNotesIndex.setModel(new PatientNote2ColumnTableModel());*/
        //tblNotesIndex.setModel(new javax.swing.table.DefaultTableModel(
        
        scrNoteIndex.setViewportView(tblNotesIndex);
        
        btnNextNote.setText(">>");
        btnPreviousNote.setText("<<");

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
                        //.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(100)
                        .addComponent(btnPreviousNote)
                        .addGap(25,25,25)
                        .addComponent(btnNextNote)))
                .addContainerGap())
            /*
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrNotepad)
                    .addGroup(pnlNotepadLayout.createSequentialGroup()
                        .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPreviousNote)
                        .addGap(20,20,20)
                        .addComponent(btnNextNote)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
                */
        );
        pnlNotepadLayout.setVerticalGroup(
            /*
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotepadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            */
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotepadLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPreviousNote)
                    .addComponent(btnNextNote))
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
    private javax.swing.JButton btnPreviousNote;
    private javax.swing.JButton btnNextNote;
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
    
    private class Notepad{
        
        
        Notepad(){
            setOpenColor(txaNotepad.getBackground());
            shut();
        }
       
        private Color shutColor = new Color(220,220,220);
        private Color getShutColor(){
            return shutColor;
        }
        
        private Color openColor = null;
        private Color getOpenColor(){
            return openColor;
        }
        private void setOpenColor(Color value){
            openColor = value;
        }

        private LocalDateTime datestamp = null;
        private LocalDateTime getDatestamp(){
            return dateTimePicker.getDateTimePermissive();
        }
        private void setDatestamp(LocalDateTime value){
            dateTimePicker.setDateTimePermissive(value);
        }
        
        private State state = null;
        private boolean isOpen(){
            return Notepad.this.state.equals(State.OPEN);
        }
        private boolean isShut(){
            return Notepad.this.state.equals(State.SHUT);
        }
        
        private String getContent(){
            return txaNotepad.getText();
        }
        private void setContent(String value){
            txaNotepad.setText(value);
        }
        
        private void open(PatientNote patientNote){
            state = State.OPEN;
            txaNotepad.setEnabled(true);
            txaNotepad.setBackground(getOpenColor());
            if (patientNote==null){
                txaNotepad.setText("");
                dateTimePicker.setDateTimePermissive(LocalDateTime.now());
                setViewMode(ViewController.ViewMode.CREATE);
            }else{
                txaNotepad.setText(patientNote.getNote());
                dateTimePicker.datePicker
                        .setDate(patientNote
                                .getDatestamp().toLocalDate());
                dateTimePicker.timePicker
                        .setTime(patientNote
                                .getDatestamp().toLocalTime());
                setViewMode(ViewController.ViewMode.UPDATE);
            }
        }
        
        private void shut(){
            txaNotepad.setEnabled(false);
            txaNotepad.setText("");
            txaNotepad.setBackground(getShutColor());
            dateTimePicker.clear();
            state = State.SHUT;
        }
        
        private void save(){
            PatientNote patientNote = null;
            boolean isDatestampError = false;
            switch(getViewMode()){
                case CREATE:
                    patientNote = new PatientNote();
                    patientNote.setPatient(getPatient());
                    patientNote.setNote(getContent());
                    patientNote.setLastUpdated(LocalDateTime.now());
                    if (getDatestamp()!=null)
                        patientNote.setDatestamp(getDatestamp());
                    else isDatestampError = true;
                    break;
                case UPDATE:
                    /*28/03/2024patientNote = getPatientNote();
                    getPatientNote().setNote(getContent());
                    patientNote.setLastUpdated(LocalDateTime.now());
                    if (getDatestamp()!=null)
                        patientNote.setDatestamp(getDatestamp());
                    else isDatestampError = true;*/
                    break;
            }
            if (!isDatestampError){
                /*28/03/2024setPatientNote(patientNote);
                getMyController()
                        .getDescriptor()
                        .getViewDescription()
                        .setPatientNote(getPatientNote());*/
                getMyController()
                        .getDescriptor()
                        .getViewDescription()
                        .setViewMode(getViewMode());

                ActionEvent actionEvent = new ActionEvent(
                ModalPatientNotesEditorView.this,ActionEvent.ACTION_PERFORMED, 
                    ViewController
                            .PatientViewControllerActionEvent
                            .PATIENT_EDITOR_VIEW_CHANGE
                            .toString());
                getMyController().actionPerformed(actionEvent);
                setViewMode(ViewController.ViewMode.UPDATE);
            }else{
                JOptionPane.showMessageDialog(ModalPatientNotesEditorView.this, 
                        "a date and time must be specified before "
                                + "the patient note is saved");
            }
            
        }
    }
    
    /**
   * SampleDateTimeChangeListener, A DateTimeChangeListener provides a way for a class to receive
   * notifications whenever the date or time has changed in a DateTimePicker.
   */
    private class DateTimePickerChangeListener implements DateTimeChangeListener {

        @Override
        public void dateOrTimeChanged(DateTimeChangeEvent event) {
            DateChangeEvent dateEvent = event.getDateChangeEvent();
            if (dateEvent!=null){
                LocalDate oldDate = dateEvent.getOldDate();
                LocalDate newDate = dateEvent.getNewDate();
            }
            TimeChangeEvent timeEvent = event.getTimeChangeEvent();
            if (timeEvent!=null){
                LocalTime oldTime = timeEvent.getOldTime();
                LocalTime newTime = timeEvent.getNewTime();
            }
            ModalPatientNotesEditorView.this
                    .getNotepad()
                    .setDatestamp(dateTimePicker.getDateTimePermissive());
        }
    }
}
