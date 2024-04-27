/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view.views.non_modal_views;
import controller.ViewController;
import model.*;
import view.View;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;  
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.io.IOException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;       
import java.beans.PropertyVetoException;
import javax.swing.KeyStroke;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import patient_view_original_class.NotesView;

/**
 *
 * @author colin
 */
public class ClinicalNoteView extends View 
        implements ActionListener, PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ClinicalNoteView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {//ViewMode arg
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        
        txaNotepad.setLineWrap(true);
        txaNotepad.setWrapStyleWord(true);
        txaNotepad.setComponentPopupMenu(this.makePopupMenu());
        
        
        setClosable(true);
        this.setIconifiable(true);
        this.setResizable(true);
        this.setClosable(true);
        this.setMaximizable(true);
        setVisible(true);
        
        addFrameListeners();
        
        
        Appointment appointment = getAppointment();
        if (appointment!=null){
            setTitle("Clinical notes for " 
                + appointment.getAppointeeName() + "'s appointment on " 
                    + appointment.getAppointmentDate());
            
            this.mniCreateNote.setActionCommand(Action.REQUEST_CREATE_NOTE.toString());
            this.mniDeleteNote.setActionCommand(Action.REQUEST_DELETE_NOTE.toString());
            this.mniUpdateNote.setActionCommand(Action.REQUEST_UPDATE_NOTE.toString());
            this.mniPasteNote.setActionCommand(Action.REQUEST_PASTE_NOTE.toString());
            this.mniCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
            
            this.mniCreateNote.addActionListener(this);
            this.mniDeleteNote.addActionListener(this);
            this.mniUpdateNote.addActionListener(this);
            this.mniPasteNote.addActionListener(this);
            this.mniCloseView.addActionListener(this);
            
            /*
            this.mniPasteNote.setAccelerator(
                    KeyStroke.getKeyStroke('V', 
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            */
            
            titledBorder = (TitledBorder)pnlClinicalNote.getBorder();
            titledBorder.setTitleColor(getBorderTitleColor());
            titledBorder.setTitleFont(getBorderTitleFont());
            
            actionCommand = ViewController
                    .ClinicalNoteViewControllerActionEvent.CLINICAL_NOTE_FOR_APPOINTMENT_REQUEST;
            doSendActionEvent(actionCommand);
            
            switch (getViewMode()){
                case CREATE:
                    titledBorder.setTitle("Clinical note (undefined)");
                    break;
                case UPDATE:
                    titledBorder.setTitle("Clinical note");
                    break;
            }
            
        }else{
            String message = "<html><center>Attempt to view the appointment's clinical note "
                    + "aborted</center>"
                    + "<center>An appointment has not been defined</center>"
                    + "</html>";
            JOptionPane.showInternalMessageDialog(this, message, "View error", 
                    JOptionPane.WARNING_MESSAGE);
            actionCommand = ViewController
                    .ClinicalNoteViewControllerActionEvent.VIEW_CLOSED_NOTIFICATION;
            doSendActionEvent(actionCommand);
            
        }
    }

    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_CREATE_NOTE,
        REQUEST_DELETE_NOTE,
        REQUEST_PASTE_NOTE,
        REQUEST_UPDATE_NOTE
    }
    
    private InternalFrameAdapter internalFrameAdapter = null;
    private void addFrameListeners(){
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                doSendActionEvent(ViewController
                        .ClinicalNoteViewControllerActionEvent.VIEW_CLOSED_NOTIFICATION);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        String message = null;
        String note = null;
        Appointment appointment = null;
        ClinicalNote clinicalNote = null;
        actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CLOSE_VIEW:
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            case REQUEST_CREATE_NOTE:{
                appointment = getMyController().getDescriptor().
                        getControllerDescription().getAppointment();
                clinicalNote = new ClinicalNote(appointment);
                note = txaNotepad.getText().trim();
                if (note.isEmpty()){
                    message = "Notepad is empty, nothing to save";  
                    JOptionPane.showInternalMessageDialog(this,message,
                            "View error", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    clinicalNote.setNotes(note);
                    getMyController().getDescriptor()
                        .getViewDescription().setClinicalNote(clinicalNote);
                    actionCommand = ViewController.ClinicalNoteViewControllerActionEvent
                            .CLINICAL_NOTE_CREATE_REQUEST;
                    doSendActionEvent(actionCommand);
                    pnlClinicalNote.repaint();
                }
                break;
            }
            case REQUEST_DELETE_NOTE:{
                clinicalNote = getMyController().getDescriptor().
                                getControllerDescription().getClinicalNote();
                message = "Are you sure want to delete the clinical note for "
                        + getAppointment().getAppointeeName() + "("
                        + getAppointment().getAppointmentDate() + ")";
                int reply = JOptionPane.showInternalConfirmDialog(this,message,
                        "Delete query",JOptionPane.YES_NO_OPTION);
                if (reply==JOptionPane.YES_OPTION){
                    getMyController().getDescriptor().
                                getViewDescription().setClinicalNote(clinicalNote);
                    actionCommand = ViewController.ClinicalNoteViewControllerActionEvent
                            .CLINICAL_NOTE_DELETE_REQUEST;
                    doSendActionEvent(actionCommand);
                }
                actionCommand = ViewController.ClinicalNoteViewControllerActionEvent
                        .CLINICAL_NOTE_UPDATE_REQUEST;
                doSendActionEvent(actionCommand);
                break;
            }
            case REQUEST_UPDATE_NOTE:{
                clinicalNote = getMyController().getDescriptor().
                                getControllerDescription().getClinicalNote();
                note = txaNotepad.getText().trim();
                if (note.isEmpty()){
                    message = "Notepad is empty, nothing to update";  
                    JOptionPane.showInternalMessageDialog(this,message,
                            "View error", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    clinicalNote.setNotes(note);
                    getMyController().getDescriptor()
                        .getViewDescription().setClinicalNote(clinicalNote);
                    actionCommand = ViewController.ClinicalNoteViewControllerActionEvent
                            .CLINICAL_NOTE_UPDATE_REQUEST;
                    doSendActionEvent(actionCommand);
                }
                break;
            }
            case REQUEST_PASTE_NOTE:
                note = this.getClipboardContents();
                if (note!=null) {
                    txaNotepad.setText(note);
                    txaNotepad.setCaretPosition(0);
                }
                break;
        }
    }
    
    @Override 
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ClinicalNoteViewControllerPropertyChangeEvent propertyName =
                ViewController.ClinicalNoteViewControllerPropertyChangeEvent
                        .valueOf(e.getPropertyName());
        switch (propertyName){
            case CLINICAL_NOTE_RECEIVED:
                setClinicalNote(getMyController().getDescriptor()
                        .getControllerDescription().getClinicalNote());
                break;
            case CLINICAL_NOTE_ERROR_RECEIVED:
                String error = getMyController().getDescriptor()
                        .getControllerDescription().getError();
                JOptionPane.showInternalMessageDialog(this, error, 
                        "View controller error", JOptionPane.WARNING_MESSAGE);
                break;   
        }
    }
    
    private void doSendActionEvent(
            ViewController.ClinicalNoteViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    private Appointment getAppointment(){
        boolean isError = false;
        Appointment appointment = getMyController().getDescriptor()
                .getControllerDescription().getAppointment();
        if (appointment!=null){
            if (!appointment.getIsKeyDefined()){
                isError = true;
            }else if (appointment.getIsUnbookableSlot()){
                isError = true;   
            }
        }else isError = true;
        if (isError) appointment = null;
        return appointment;
    }
    
    private ClinicalNote clinicalNote = null;
    private void setClinicalNote(ClinicalNote value){
        clinicalNote = value;
        if (clinicalNote==null) {
            setViewMode(ViewController.ViewMode.CREATE);
            txaNotepad.setText("");
        }else {
            setViewMode(ViewController.ViewMode.UPDATE);
            txaNotepad.setText(clinicalNote.getNotes());
            txaNotepad.setCaretPosition(0);
        }
        
    }
    private ClinicalNote getClinicalNote(){
        return clinicalNote;
    }
    
    private ViewController.ViewMode viewMode = null;
    private void setViewMode(ViewController.ViewMode value){
        viewMode = value;
        switch (viewMode){
            case CREATE:
                this.mniCreateNote.setEnabled(true);
                this.mniUpdateNote.setEnabled(false);
                this.mniDeleteNote.setEnabled(false);
                titledBorder.setTitle("Clinical note (undefined)");
                break;
            case UPDATE:
                this.mniCreateNote.setEnabled(false);
                this.mniUpdateNote.setEnabled(true);
                this.mniDeleteNote.setEnabled(true);
                titledBorder.setTitle("Clinical note");
                break;
        }
    }
    
    private ViewController.ViewMode getViewMode(){
        return viewMode;
    }
    
    public String getClipboardContents() {
        String message = null;
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null) && 
                contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if ( hasTransferableText ) {
            try {
              result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException ex){
                //highly unlikely since we are using a standard DataFlavor
                message = ex.getMessage() + "\n"
                        + "UnsupportedFlavorException handled in "
                        + "ClinicalNoteView.getClipboardContents()";
                JOptionPane.showInternalMessageDialog(this,message,
                              "View error", JOptionPane.WARNING_MESSAGE);
            }
            catch (IOException ex) {
                message = ex.getMessage() + "\n"
                      + "IOException handled in "
                      + "ClinicalNoteView.getClipboardContents()";
                JOptionPane.showInternalMessageDialog(this,message,
                        "View error", JOptionPane.WARNING_MESSAGE);
            }
        }
        //result = makeSectionHeadersUpperCase(result);
        return result;
    }
    
    private JPopupMenu makePopupMenu(){
        JPopupMenu popup = new JPopupMenu();
        pastePopupMenuItem = popup.add("Paste note");
        pastePopupMenuItem.setActionCommand(
                Action.REQUEST_PASTE_NOTE.toString());
        pastePopupMenuItem.addActionListener(this);
        popup.add(new JPopupMenu.Separator());
        savePopupMenuItem = popup.add("Save note");
        savePopupMenuItem.setActionCommand(
                Action.REQUEST_CREATE_NOTE.toString());
        savePopupMenuItem.addActionListener(this);
        
        return popup;
                    
    }
    
    private String makeSectionHeadersUpperCase(String s){
        setUnmodifiedNote(s);
        String news = "";
        String target = null;
        String uppercaseHeading = null;
        int start = 0 ;
        int end = -2;
        
        do{
            //next TITLE
            start = start + end + 2;
            target = s.substring(start);
            end = target.indexOf("\n");
            //if (end == -1) break;
            uppercaseHeading = target.substring(0,end).toUpperCase();
            if (start > 0) news = news + "\n" + uppercaseHeading;
            else news = news + uppercaseHeading;
            
            //nex section
            start = start+ end;
            target = s.substring(start);
            end = target.indexOf("\n\n");
            //news = news + target.substring(0,end+1);
            
            if (end == -1) {
                news = news + target.substring(0);
                break;
            }
            else news = news + target.substring(0,end+1);
            
        }while(end!=-1);   
        return news;
    }

    private String unmodifiedUnmodifiedNote = null;
    private void setUnmodifiedNote(String value){
        unmodifiedUnmodifiedNote = value;
    }
    private String setUnmodifiedNote(){
        return unmodifiedUnmodifiedNote;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        pnlClinicalNote = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaNotepad = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mniCreateNote = new javax.swing.JMenuItem();
        mniUpdateNote = new javax.swing.JMenuItem();
        mniDeleteNote = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mniPasteNote = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mniCloseView = new javax.swing.JMenuItem();

        pnlClinicalNote.setBorder(javax.swing.BorderFactory.createTitledBorder("Patent name"));

        txaNotepad.setColumns(20);
        txaNotepad.setRows(5);
        jScrollPane1.setViewportView(txaNotepad);

        javax.swing.GroupLayout pnlClinicalNoteLayout = new javax.swing.GroupLayout(pnlClinicalNote);
        pnlClinicalNote.setLayout(pnlClinicalNoteLayout);
        pnlClinicalNoteLayout.setHorizontalGroup(
            pnlClinicalNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClinicalNoteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlClinicalNoteLayout.setVerticalGroup(
            pnlClinicalNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClinicalNoteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenu1.setText("File");

        mniCreateNote.setText("Create clinical note");
        jMenu1.add(mniCreateNote);

        mniUpdateNote.setText("Update clinical note");
        jMenu1.add(mniUpdateNote);

        mniDeleteNote.setText("Delete clinical note");
        jMenu1.add(mniDeleteNote);
        jMenu1.add(jSeparator1);

        mniPasteNote.setText("Paste notes");
        jMenu1.add(mniPasteNote);
        jMenu1.add(jSeparator3);

        mniCloseView.setText("Close view");
        jMenu1.add(mniCloseView);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlClinicalNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlClinicalNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem mniCloseView;
    private javax.swing.JMenuItem mniCreateNote;
    private javax.swing.JMenuItem mniDeleteNote;
    private javax.swing.JMenuItem mniPasteNote;
    private javax.swing.JMenuItem mniUpdateNote;
    private javax.swing.JPanel pnlClinicalNote;
    private javax.swing.JTextArea txaNotepad;
    // End of variables declaration//GEN-END:variables
    private ViewController.ClinicalNoteViewControllerActionEvent actionCommand = null;
    private TitledBorder titledBorder = null;
    private javax.swing.JMenuItem pastePopupMenuItem = null;
    private javax.swing.JMenuItem savePopupMenuItem = null;
}
