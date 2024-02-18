/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.non_modal_views;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateTimeChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DateTimeChangeEvent;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;
import controller.ViewController;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import model.Patient;
import model.PatientNote;
import view.View;
import view.views.modal_views.ModalPatientNotesEditorView;
import view.views.view_support_classes.models.PatientNote2ColumnTableModel;
import view.views.view_support_classes.renderers.TableHeaderCellBorderRenderer;

/**
 *
 * @author colin
 */
public class NotesView extends View {

      public NotesView (View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("Outstanding patient notifications");
        this.setMyViewType(myViewType);
        setMyController(myController);  
        setDesktopView(desktopView);
    }  
      
    @Override
    public void initialiseView(){
        initComponents();
        
        /**
         * rather than change the whole layout, now this button is redundant
         * -- following 2 lines will achieve the same effect as changing the layout
         * -- in far less time too
         */
        this.btnSaveNoteChanges.setEnabled(false);
        this.btnSaveNoteChanges.setVisible(false);
        
        ViewController.setJTableColumnProperties(
                tblNotesIndex, scrNoteIndex.getPreferredSize().width, 25,75);
        setVisible(true);
        addListeners();
        addInternalFrameListeners();
        dateTimePicker.addDateTimeChangeListener(new DateTimePickerChangeListener());
        
        Patient p = getMyController()
                .getDescriptor().getControllerDescription().getPatient();

        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.NotesViewControllerActionEvent.NOTES_FOR_PATIENT_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setTitle("Patient notes editor for " + p.toString());
        setPatient(p);
        setNotepad(new Notepad());
        getNotepad().modeSwitchTo(NOTEPAD_MODE.NOTEPAD_SHUT);
        setViewMode(ViewController.ViewMode.UPDATE);
        setNotesIndexTableListener();

        //JOptionPane.showMessageDialog(this,"gsdhjdsghsagh");
        /*
        else{
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.NotesViewControllerActionEvent.NOTES_PATIENT_SELECTION_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
        }
        */
        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.NotesViewControllerPropertyChangeEvent propertyName =
                ViewController.NotesViewControllerPropertyChangeEvent.valueOf(e.getPropertyName());
        switch (propertyName){
            case NOTES_FOR_PATIENT_RECEIVED:
                doRefreshNoteIndex();
                break; 
            case PATIENT_SELECTION_REQUESTED:
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.NotesViewControllerActionEvent.NOTES_PATIENT_SELECTION_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
    }
    
    private void doRefreshNoteIndex(){
        populateNotesIndexTable(getMyController()
                .getDescriptor()
                .getControllerDescription()
                .getPatientNotes());
    }

    private enum State{OPEN,SHUT};
    
    private Notepad notepad = null;
    private void setNotepad(Notepad value){
        notepad = value;
    }
    private Notepad getNotepad(){
        return notepad;
    }
    
    private PatientNote patientNote = null;
    private PatientNote getPatientNote(){
        return patientNote;
    }
    private void setPatientNote(PatientNote value){
        patientNote = value;
    }
    
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
    
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        NotesView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override  
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        NotesView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void addListeners(){
        ArrayList<PatientNote> patientNotes = new ArrayList<>();
        PatientNote2ColumnTableModel model = 
                (PatientNote2ColumnTableModel)tblNotesIndex.getModel();
        PatientNote patientNote;

        btnCloseView.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try{
                        NotesView.this.setClosed(true);
                    }
                    catch (PropertyVetoException ex){

                    }
                }
            }
        );
        btnOpenShutNotepad.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                /*
                Boolean result = okToClearNotepad("OK to lose current notepad contents");
                if (result){
                    getNotepad().shut();
                }
                */
                getNotepad().actionPerformed(USER_ACTION.NOTEPAD_ENTRY_SELECTED);
            }
        });
        this.btnCreateUpdateNote.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    /*
                    Boolean result = okToClearNotepad("Contents in notepad will be lost. OK to proceed?");
                    if (result){
                        getNotepad().shut();
                        getNotepad().open(null);

                    }
                    */
                    getNotepad().actionPerformed(USER_ACTION.NOTEPAD_SAVE_REQUESTED);
                }
            }
        );
        /*
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
                                    NotesView.this,
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
        */
        this.btnNextNote.addActionListener(new java.awt.event.ActionListener() {
            int row;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (!model.getPatientNotes().isEmpty()){//any rows in the table
                    row = tblNotesIndex.getSelectedRow();
                    if ((row+1) < model.getPatientNotes().size()){
                        tblNotesIndex.setRowSelectionInterval(row+1,row+1);
                        tblNotesIndex.scrollRectToVisible(
                                new Rectangle(tblNotesIndex.
                                        getCellRect(row+1, 0, true)));
                    }
                }
            }
        });
        this.btnPreviousNote.addActionListener(new java.awt.event.ActionListener() {
            int row;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (!model.getPatientNotes().isEmpty()){//any rows in the table
                    row = tblNotesIndex.getSelectedRow();
                    if ((row-1) >= 0){
                        tblNotesIndex.setRowSelectionInterval(row-1,row-1);
                        //following code auto moves the vertical scrool bar 
                        //when next row selected would be off scroll pane
                        tblNotesIndex.scrollRectToVisible(
                                new Rectangle(tblNotesIndex.
                                        getCellRect(row-1, 0, true)));
                    }
                }
            }
        });
    }
    
    enum NOTEPAD_MODE{NOTEPAD_SHUT,  
                      NOTEPAD_OPENED_FOR_CREATE,
                      NOTEPAD_OPENED_FOR_UPDATE
                      }
    enum USER_ACTION{ NOTEPAD_ENTRY_SELECTED,
                      NOTEPAD_INDEX_ITEM_SELECTED,
                      NOTEPAD_SAVE_REQUESTED
                      }
    
    
    
    private Boolean okToClearNotepad(String message){
        Boolean result = null;
        if ((getNotepad().getContent().trim().length() > 0)
                || getNotepad().getDatestamp()!=null){
            int reply = 0;
            String[] options = {"Yes", "No"};
            reply = JOptionPane.showOptionDialog(
                    NotesView.this,
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
                    Integer selectedRow = lsm.getMinSelectionIndex();
                    doPatientNotesIndexTableRowSelection(selectedRow);
                }
                else doPatientNotesIndexTableRowSelection(null);
            }
        });
    }
    
    private void doPatientNotesIndexTableRowSelection(Integer row){
        TitledBorder titledBorder = (TitledBorder)pnlNoteIndex.getBorder();
        if (row!=null){
            PatientNote2ColumnTableModel model = 
                    (PatientNote2ColumnTableModel)tblNotesIndex.getModel();
            setPatientNote((PatientNote)model.getElementAt(row));
            titledBorder.setTitle("Note index (selection last updated " 
                    + getPatientNote().getLastUpdated().toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yy"))
                    + " at " + getPatientNote().getLastUpdated().toLocalTime()
                            .format(DateTimeFormatter.ofPattern("hh:mm)")));
            getNotepad().shut();
            getNotepad().actionPerformed(USER_ACTION.NOTEPAD_INDEX_ITEM_SELECTED);
        }else titledBorder.setTitle("Note index");
        pnlNoteIndex.repaint();
        
        
        
        
    }
    
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
    }
    
    private void initComponents() {

        pnlNoteIndex = new javax.swing.JPanel();
        scrNoteIndex = new javax.swing.JScrollPane();
        pnlNotepad = new javax.swing.JPanel();
        scrNotepad = new javax.swing.JScrollPane();
        pnlOperations = new javax.swing.JPanel();
        btnCreateUpdateNote = new javax.swing.JButton();
        btnSaveNoteChanges = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnOpenShutNotepad = new javax.swing.JButton();
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
        tblNotesIndex.setModel(new PatientNote2ColumnTableModel());
        //tblNotesIndex.setModel(new javax.swing.table.DefaultTableModel(
        
        scrNoteIndex.setViewportView(tblNotesIndex);
        
        btnNextNote.setText(">>");
        btnPreviousNote.setText("<<");

        btnOpenShutNotepad.setText("<html>"
                + "<center>Clear</center>"
                + "<center>notepad</center>"
                + "</html>");
        
        btnCreateUpdateNote.setText("<html>"
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
                    .addComponent(btnCreateUpdateNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOpenShutNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    //.addComponent(btnSaveNoteChanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    //.addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCloseView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        pnlOperationsLayout.setVerticalGroup(pnlOperationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOperationsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnCreateUpdateNote, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30,30,30)
                .addComponent(btnOpenShutNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addGap(30,30,30)
                //.addComponent(btnSaveNoteChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                //.addGap(30,30,30)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
            )
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
    private javax.swing.JButton btnCreateUpdateNote;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnOpenShutNotepad;
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
    private InternalFrameAdapter internalFrameAdapter = null;
    // End of variables declaration    
    
    private class Notepad{
        
        
        Notepad(){
            setComponentEnabledColor(txaNotepad.getBackground());
            shut();
        }
       

        private Color componentDisabledColor = new Color(220,220,220);
        private Color getComponentDisabledColor(){
            return componentDisabledColor;
        }
        
        private Color componentEnabledColor = null;
        private Color getComponentEnabledColor(){
            return componentEnabledColor;
        }
        private void setComponentEnabledColor(Color value){
            componentEnabledColor = value;
        }

        private LocalDateTime datestamp = null;
        private LocalDateTime getDatestamp(){
            return dateTimePicker.getDateTimePermissive();
        }
        private void setDatestamp(LocalDateTime value){
            dateTimePicker.setDateTimePermissive(value);
        }
        
        /*
        private State state = null;
        private boolean isOpen(){
            return Notepad.this.state.equals(State.OPEN);
        }
        private boolean isShut(){
            return Notepad.this.state.equals(State.SHUT);
        }
        */
        
        private String getContent(){
            return txaNotepad.getText();
        }
        private void setContent(String value){
            txaNotepad.setText(value);
        }
        
        private void open(PatientNote patientNote){
            //state = State.OPEN;
            txaNotepad.setEnabled(true);
            txaNotepad.setBackground(getComponentEnabledColor());
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
            txaNotepad.setBackground(getComponentDisabledColor());
            dateTimePicker.clear();
            //state = State.SHUT;
        }
        
        private boolean hasUserConfirmedSave(String message){
            boolean result = false;
            if (message != null){
                int reply = JOptionPane.showConfirmDialog(
                        NotesView.this,
                        message,"",
                        JOptionPane.YES_NO_OPTION);
                switch(reply){
                    case JOptionPane.YES_OPTION:
                        result = true;
                        break;
                    case JOptionPane.NO_OPTION:
                        result = false;
                        break;
                }
            }
            else result = true;
            return result;
        }
        
        /**
         * method saves contents of notepad with a confirmation check with user if specified
         * @param confirmationMessage
         * @return true/false depending on whether save operation happened
         */
        private boolean save(String confirmationMessage){
            boolean result = false;
            PatientNote patientNote = null;
            boolean isDatestampError = false;
            if (hasUserConfirmedSave(confirmationMessage)){
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
                        patientNote = getPatientNote();
                        getPatientNote().setNote(getContent());
                        patientNote.setLastUpdated(LocalDateTime.now());
                        if (getDatestamp()!=null)
                            patientNote.setDatestamp(getDatestamp());
                        else isDatestampError = true;
                        break;
                }
                if (!isDatestampError){
                    setPatientNote(patientNote);
                    getMyController()
                            .getDescriptor()
                            .getViewDescription()
                            .setPatientNote(getPatientNote());
                    getMyController()
                            .getDescriptor()
                            .getViewDescription()
                            .setViewMode(getViewMode());

                    ActionEvent actionEvent = new ActionEvent(
                        NotesView.this,ActionEvent.ACTION_PERFORMED, 
                            ViewController
                                    .NotesViewControllerActionEvent
                                    .NOTES_FOR_PATIENT_CHANGE_REQUEST
                                    .toString());
                    getMyController().actionPerformed(actionEvent);
                    result = true;
                }else{
                    JOptionPane.showMessageDialog(NotesView.this, 
                            "a date and time must be specified before "
                                    + "the patient note is saved");
                    result = false;
                } 
            }else {
                result = false;
            }
            return result;
        }

        public void actionPerformed(USER_ACTION action){
            String messageToUser = null;
            switch (action){
                case NOTEPAD_ENTRY_SELECTED:
                    switch(getNotepadMode()){
                        case NOTEPAD_SHUT://open notepad requested
                            modeSwitchTo(NOTEPAD_MODE.NOTEPAD_OPENED_FOR_CREATE);   
                            break;
                        case NOTEPAD_OPENED_FOR_CREATE://shut notepad requested
                            messageToUser = "Save selected contents before closing the notebook?";
                            save(messageToUser);
                            modeSwitchTo(NOTEPAD_MODE.NOTEPAD_SHUT);
                            break;
                        case NOTEPAD_OPENED_FOR_UPDATE:
                            messageToUser =  "Save any changes to selected note before notebook closed?";
                            save(messageToUser);
                            modeSwitchTo(NOTEPAD_MODE.NOTEPAD_SHUT);
                            break;
                        default:
                            JOptionPane.showMessageDialog(NotesView.this,
                                    "Unexpected current notepad mode (" + getNotepadMode().toString() 
                                            + "); mode switch aborted",
                                    "Notes view error", JOptionPane.WARNING_MESSAGE);  
                            break;
                    }
                    break;
                case NOTEPAD_INDEX_ITEM_SELECTED:
                    switch(getNotepadMode()){
                        case NOTEPAD_SHUT:
                            modeSwitchTo(NOTEPAD_MODE.NOTEPAD_OPENED_FOR_UPDATE);
                            break;
                        case NOTEPAD_OPENED_FOR_UPDATE:
                            open(getPatientNote());
                            break;
                        case NOTEPAD_OPENED_FOR_CREATE:
                            break;
                        default:
                            JOptionPane.showMessageDialog(NotesView.this,
                                    "Unexpected current notepad mode (" + getNotepadMode().toString() 
                                            + "); mode switch aborted",
                                    "Notes view error", JOptionPane.WARNING_MESSAGE);  
                            break;
                    }
                    break;
                
                    case NOTEPAD_SAVE_REQUESTED:
                        save(null);
            }
        }
        
        private NOTEPAD_MODE mode = null;
        private NOTEPAD_MODE getNotepadMode(){
            return mode;
        }
        private void setNotepadMode(NOTEPAD_MODE value){
            mode = value;
        }
        
        private void disableNoteSelectionFromIndex(){
            btnPreviousNote.setEnabled(false);
            btnNextNote.setEnabled(false);
            tblNotesIndex.setEnabled(false);
            tblNotesIndex.setBackground(getComponentDisabledColor());
        }
        
        private void enableNoteSelectionFromIndex(){
            btnPreviousNote.setEnabled(true);
            btnNextNote.setEnabled(true);
            tblNotesIndex.setEnabled(true);
            tblNotesIndex.setBackground(getComponentEnabledColor());
        }
                
        
        private void modeSwitchTo(NOTEPAD_MODE value){
            switch(value){
                case NOTEPAD_SHUT:
                    setViewMode(ViewController.ViewMode.NO_ACTION);
                    btnCreateUpdateNote.setText(
                            "<html><center>Add</center><center>new note</center></html>");
                    btnCreateUpdateNote.setEnabled(false);
                    btnOpenShutNotepad.setText(
                            "<html><center>Open</center><center>notepad</center></html>");
                    enableNoteSelectionFromIndex();
                    getNotepad().shut();
                    setNotepadMode(value);
                    break;
                case NOTEPAD_OPENED_FOR_CREATE:
                    setViewMode(ViewController.ViewMode.CREATE);
                    btnCreateUpdateNote.setText(
                            "<html><center>Add</center><center>new note</center></html>");        
                    btnOpenShutNotepad.setText(
                            "<html><center>Close</center><center>notepad</center></html>");
                    btnCreateUpdateNote.setEnabled(true);
                    open(null);
                    disableNoteSelectionFromIndex();
                    doRefreshNoteIndex();
                    setNotepadMode(value);
                    break;
                case NOTEPAD_OPENED_FOR_UPDATE:
                    setViewMode(ViewController.ViewMode.UPDATE);
                    btnCreateUpdateNote.setText(
                            "<html><center>Update</center><center>note</center></html>");
                    btnOpenShutNotepad.setText(
                            "<html><center>Close</center><center>notepad</center></html>");
                    btnCreateUpdateNote.setEnabled(true);
                    enableNoteSelectionFromIndex();
                    open(patientNote);
                    setNotepadMode(value);
                    break;
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
            NotesView.this
                    .getNotepad()
                    .setDatestamp(dateTimePicker.getDateTimePermissive());
        }
    }

}
