/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.PatientDocumentStoreViewController;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.model.entity.Patient;
import colinhewlettsolutions.client.view.View;
import org.apache.commons.io.FilenameUtils;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author colin
 */
public class PatientDocumentStoreView extends View 
        implements ActionListener, PropertyChangeListener {
    
    enum Actions {
        REQUEST_CLOSE_VIEW,
        REQUEST_FILE_DELETE,
        REQUEST_FILE_OPEN
    }
    
    enum Properties {
        VIEW_CHANGED_NOTIFICATION
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public PatientDocumentStoreView(View.Viewer myViewType, 
            ViewController myController,
            DesktopView desktopView) {
        setMyController(myController);
        setMyViewType(myViewType);
        setDesktopView(desktopView); 
    }
    
    public void actionPerformed(ActionEvent e){
        Actions action = Actions.valueOf(e.getActionCommand());
        switch(action){
            case REQUEST_CLOSE_VIEW ->{
                doCloseViewRequest();
                break;
            }
            case REQUEST_FILE_DELETE ->{
                doFileDeleteRequest();
                break;
            }
            case REQUEST_FILE_OPEN ->{
                doFileOpenRequest();
                break;
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent e){
        Properties event = Properties.valueOf(e.getPropertyName());
        switch(event){
            case VIEW_CHANGED_NOTIFICATION ->{
                populateDocumentStoreFileList();
            }
        }
    }

    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        switch(getViewMode()){
            case DOCUMENT ->{
                setTitle("Documents for " + getPatient());
                break;
            }
            case MEDICAL_HISTORY ->{
                setTitle("Medical history scans for " + getPatient());
                break;
            }
        }
        btnCloseView.setActionCommand(Actions.REQUEST_CLOSE_VIEW.toString());
        btnCloseView.addActionListener(this);
        btnDelete.setActionCommand(Actions.REQUEST_FILE_DELETE.toString());
        btnDelete.addActionListener(this);
        btnOpen.setActionCommand(Actions.REQUEST_FILE_OPEN.toString());
        btnOpen.addActionListener(this);
        setViewTitledBorderSettings();
        populateDocumentStoreFileList();
    }
    
    private void doCloseViewRequest(){
        try{
            setClosed(true);
        }catch(PropertyVetoException ex){

        }
        doActionEventFor(PatientDocumentStoreViewController.Actions.VIEW_CLOSE_NOTIFICATION);
    }
    
    private boolean confirmFileDeletion(String message){
        boolean result = false;
        int reply = JOptionPane.showInternalConfirmDialog(
                    this,message,"Request to delete file from " + getPatientNameString() + "'s document store",JOptionPane.YES_NO_OPTION);
        return reply==JOptionPane.YES_OPTION;
    }
    
    private String getPatientNameString(){
        String result = null;
        String fornames = null;
        String surname = null;
        String title = null;        
        if (getPatient()!=null){
            result = getPatient().getName().getTitle();
            result = result + " " + getPatient().getName().getForenames();
            result = result + " " +getPatient().getName().getSurname();
        }
        return result;
    }
    
    private void doFileDeleteRequest(){
        String selectedFile = null;
        String message = null;
        ArrayList<File> files = new ArrayList<>();
        //File[] the_files = null;
        if (this.lstDocumentStore.getSelectedIndex()!=-1){
            selectedFile = this.lstDocumentStore.getSelectedValue();
            switch(getViewMode()){
                case DOCUMENT ->{
                    message = "are you sure you want to delete the document '" + selectedFile +"' from the document store";
                    if (confirmFileDeletion(message)){
                        files = new DocumentStore().getFileList();
                        try{
                            for (File file : files){
                                if (file.getName().equals(selectedFile)){
                                    Files.deleteIfExists(file.toPath());
                                }
                            }
                        }catch(IOException |
                                InvalidPathException ex){
                            message = ex.getMessage() + "\n";
                            message = message + "Raised in PatientDocumentStoreView::doFileDeleteRequest()";
                            ViewController.displayErrorMessage(message, "View error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                }
                case MEDICAL_HISTORY ->{
                    File file = null;
                    message = "are you sure you want to delete the medical history date stamped '" + selectedFile +"'";
                    if (confirmFileDeletion(message)){
                        int dotIndex = selectedFile.lastIndexOf('.');
                        selectedFile = selectedFile.substring(0,dotIndex); 
                        LocalDateTime date = LocalDateTime.parse(selectedFile,DateTimeFormatter.ofPattern("dd/MM/yyyy (HH:mm:ss)"));
                        file = new DocumentStore().getFileWithDate(date);
                        try{
                            if (file!=null){
                                Files.deleteIfExists(file.toPath());
                            }else{
                                message = "Selected file cannot be located";
                                ViewController.displayErrorMessage(message, "View error", JOptionPane.WARNING_MESSAGE);
                            }
                        }catch(IOException |
                                InvalidPathException ex){
                            message = ex.getMessage() + "\n";
                            message = message + "Raised in PatientDocumentStoreView::doFileDeleteRequest()";
                            ViewController.displayErrorMessage(message, "View error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    break;
                }
            }
            this.populateDocumentStoreFileList();
        }else{
            JOptionPane.showInternalMessageDialog(this,
                    "the file to delete from this patient's document store has not been selected yet",
                    "Request to delete file from " + getPatientNameString() + "'s document store",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void doFileOpenRequest(){
        String selectedFile = null;
        if (this.lstDocumentStore.getSelectedIndex()!=-1){
            selectedFile = this.lstDocumentStore.getSelectedValue();
            new DocumentStore().openFile(selectedFile);
        }else{
            JOptionPane.showInternalMessageDialog(this,
                    "the file to open in this patient's document store has not been selected yet",
                    "Request to open a file in " + getPatientNameString() + "'s document store",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private String changeDateFormat(String original){
        // 1️⃣ Define the input pattern (matching your string)
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

        // 2️⃣ Define the desired output pattern
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // 3️⃣ Parse and reformat
        LocalDateTime dateTime = LocalDateTime.parse(original, inputFormat);
        String reformatted = dateTime.format(outputFormat);
        return reformatted;
    }
    
    private void populateDocumentStoreFileList(){
        DefaultListModel<String> model = new DefaultListModel<>();
        ArrayList<File> files = new DocumentStore().getFileList();
        Collections.sort(files);
        switch(getViewMode()){
            case DOCUMENT ->{
                for(File file : files){
                    model.addElement(file.getName());
                }
                break;
            }
            case MEDICAL_HISTORY ->{
                if(files!=null){
                    for(File file: files){
                        String dateTimeStamp = file.getName();
                        String extension = FilenameUtils.getExtension(dateTimeStamp);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                        LocalDateTime ltdDateTimeStamp = LocalDateTime.parse(FilenameUtils.getBaseName(dateTimeStamp), formatter); 
                        dateTimeStamp = ltdDateTimeStamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy (HH:mm:ss)"));
                        dateTimeStamp = dateTimeStamp + "." + extension;
                        model.addElement(dateTimeStamp);
                    }
                }
                break;
            }
        }      
        lstDocumentStore.setModel(model);
        if (files.isEmpty()){
            this.btnDelete.setEnabled(false);
            this.btnOpen.setEnabled(false);
        }else{
            this.btnDelete.setEnabled(true);
            this.btnOpen.setEnabled(true);
        }
    }
    
    private ViewController.ViewMode viewMode = null;
    private ViewController.ViewMode getViewMode(){
        return (ViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.VIEW_MODE);
    }
    
    private Patient getPatient(){
        return (Patient)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.PATIENT);
    }
    
    private Path getDocumentStorePath(){
        return (Path)getMyController().getDescriptor().getControllerDescription().
                    getProperty(SystemDefinition.Properties.DOCUMENT_STORE);
    }
    
    private void doActionEventFor(PatientDocumentStoreViewController.Actions action){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            action.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    class DocumentStore{
        
        void openFile(String theFile){
            switch(getViewMode()){
                case DOCUMENT ->{
                    int dotIndex = theFile.lastIndexOf('.');
                    String ext = theFile.substring(dotIndex);
                    if (ext.equals(".docx") ||
                        ext.equals(".doc") ||
                        ext.equals(".pdf")){
                        File[] files = getFiles();
                        for (File file : files){
                            if (file.getName().equals(theFile)){
                                try{
                                    Desktop.getDesktop().open(file);
                                }catch(IOException ex){
                                    
                                }
                            }
                        }
                    }else{
                        JOptionPane.showInternalMessageDialog(
                                getMyController().getView(), "Selected document to open is not a Word file", "View error",JOptionPane.WARNING_MESSAGE);
                    }
                    break;
                }
                case MEDICAL_HISTORY ->{
                    int dotIndex = theFile.lastIndexOf('.');
                    theFile = theFile.substring(0,dotIndex);
                    LocalDateTime dateTimeOfFile = 
                            LocalDateTime.parse(theFile, DateTimeFormatter.ofPattern("dd/MM/yyyy (HH:mm:ss)"));
                    File file = getFileWithDate(dateTimeOfFile);
                    try{
                        Desktop.getDesktop().open(file);
                    }catch(IOException ex){

                    }
                    break;
                }
            }
        }
        
        ArrayList<File> getFileList(){
            ArrayList<File> arrayListResult = new ArrayList<File>();
            File directory = null;
            File[] result = null;
            //File directory = getDocumentStorePath().resolve(String.valueOf(getPatient().getKey())).toFile();
            Path path = getDocumentStorePath().resolve(String.valueOf(getPatient().getKey()));
            switch(getViewMode()){
                case DOCUMENT ->{
                    directory = path.resolve("document").toFile();
                    result = directory.listFiles((d, name) ->
                        name.toLowerCase().endsWith(".pdf") ||
                        name.toLowerCase().endsWith(".docx") ||
                        name.toLowerCase().endsWith(".doc"));
                    break;
                }
                case MEDICAL_HISTORY ->{
                    directory = path.resolve("medical_history").toFile();
                    result = directory.listFiles((d, name) ->
                        name.toLowerCase().endsWith(".pdf") ||
                        name.toLowerCase().endsWith(".docx") ||
                        name.toLowerCase().endsWith(".doc"));
                    break;
                }
            }
            if (result!=null){
                for(File file : result){
                    arrayListResult.add(file);
                }
            }
            return arrayListResult;
        }

        File[] getFiles(){ 
            File directory = null;
            File[] result = null;
            //File directory = getDocumentStorePath().resolve(String.valueOf(getPatient().getKey())).toFile();
            Path path = getDocumentStorePath().resolve(String.valueOf(getPatient().getKey()));
            switch(getViewMode()){
                case DOCUMENT ->{
                    directory = path.resolve("document").toFile();
                    result = directory.listFiles((d, name) ->
                        name.toLowerCase().endsWith(".pdf") ||
                        name.toLowerCase().endsWith(".docx") ||
                        name.toLowerCase().endsWith(".doc"));
                    break;
                }
                case MEDICAL_HISTORY ->{
                    directory = path.resolve("medical_history").toFile();
                    result = directory.listFiles((d, name) ->
                        name.toLowerCase().endsWith(".pdf") ||
                        name.toLowerCase().endsWith(".docx") ||
                        name.toLowerCase().endsWith(".doc"));
                    break;
                }
            }
            
            return result;
        }
        
        File getFileWithDate(LocalDateTime localDateTime){
            File result = null;
            String filename = null;
            String filenameDatePortion = null;
            filenameDatePortion = localDateTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            File[] files = getFiles();
            if(getFiles()!=null){ 
                for(File file : files){
                    filename = FilenameUtils.getBaseName(file.getName());
                    if (filename.equals(filenameDatePortion)){
                        result = file; 
                        break;
                    }
                }
            }
            return result;
        }
        
        /**
         * fetches a file from medical history folder namwed with specified datetime stamp
         * @param localDateTime
         * @return File
         */
        File getFilesWithDate(LocalDateTime localDateTime){
            //ArrayList<File> result = new ArrayList<>();
            File result = null;
            String filename = null;
            String filenameDatePortion = null;
            File[] files = getFiles();
            filenameDatePortion = localDateTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            for(File file : files){
                filename = file.getName();
                if (filename.equals(filenameDatePortion)){
                    result = file;  
                }
            }
            return result;
        }
        
        ArrayList<LocalDateTime> getScanDatesList(ArrayList<File> files){
            HashSet<LocalDateTime> scanDates = new HashSet<>();
            for (File file : files){
                scanDates.add(getDateFromFile(file));
            }
            ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
            for (LocalDateTime localDateTime : scanDates){
                dateTimes.add(localDateTime);
            }
            Collections.sort(dateTimes);
            return dateTimes;
        }
        
        ArrayList<LocalDateTime> getScanDates(File[] files){
            HashSet<LocalDateTime> scanDates = new HashSet<>();
            for (File file : files){
                scanDates.add(getDateFromFile(file));
            }
            ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
            for (LocalDateTime localDateTime : scanDates){
                dateTimes.add(localDateTime);
            }
            Collections.sort(dateTimes);
            return dateTimes;
        }
        
        private LocalDateTime dateFromFile = null;
        LocalDateTime getDateFromFile(File file){
            String filename = file.getName();
            //extract date portion of filename
            int scanIndex = filename.lastIndexOf('_');
            filename = (scanIndex == -1) ? filename : filename.substring(0,scanIndex);
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            //and return this as a LocalDateTime
            return LocalDateTime.parse(filename, inputFormat);
        }
        
        private final ArrayList<File> files = null;
        ArrayList<File> getFilesFomDate(LocalDateTime date, File[] documents){
            ArrayList<File> result = new ArrayList<>();
            String filename = null;
            String filenameDatePortion = null;
            //getdate portion of filename so we can search for this
            filenameDatePortion = date.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            // ... in the array of files sent to method
            for (File file : documents){
                filename = file.getName();
                //remove file extension and index from filename
                int fileIndex = filename.lastIndexOf('_');
                filename = filename.substring(0, fileIndex);
                if (filename.equals(filenameDatePortion)){
                    result.add(file);
                }
            }
            return files;
        }
/*
        private File file = null;
        File getFileFromDate(LocalDateTime date, File[] scans){
            File[] result = null;
            String filename = null;
            //get date portion of file to fetch
            String fileNameWithoutExtension = date.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            //loop thru array of files sent
            for(File file : scans){
                filename = file.getName();
                //remove file extension from filename
                int dotIndex = filename.lastIndexOf('.');
                filename = filename.substring(0, dotIndex);
                //remove index from filename
                int fileIndex = filename.lastIndexOf('_');
                String filenameWithoutIndex = filename.substring(0, fileIndex);
                if (filenameWithoutIndex.equals(fileNameWithoutExtension)){
                    result = result.
                    break;
                }
            }
            return result;
        }
 */       
        
    }
    
    enum BorderTitles{ACTIONS,DOCUMENT_STORE_LIST}
    
    private void setViewTitledBorderSettings(){
        setBorderTitles(PatientDocumentStoreView.BorderTitles.ACTIONS);
        setBorderTitles(PatientDocumentStoreView.BorderTitles.DOCUMENT_STORE_LIST);
    }
    
    private void setBorderTitles(PatientDocumentStoreView.BorderTitles borderTitles){
        /*ViewController.ViewMode viewMode = (ViewController.ViewMode)getMyController().getDescriptor().getControllerDescription().
                getProperty(SystemDefinition.Properties.VIEW_MODE);*/
        JPanel panel = null;
        String caption = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case ACTIONS ->{
                caption = "Actions";
                panel = pnlActions;
                isPanelBackgroundDefault = false;
                break;
            }
            case DOCUMENT_STORE_LIST ->{
                panel = this.pnlDocumentStoreFiles;
                switch (getViewMode()){
                    case DOCUMENT ->{
                        caption = "Documents";
                        break;
                    }
                    case MEDICAL_HISTORY ->{
                        caption = "Date of medical history (time uploaded)";
                        break;
                    }
                }
                isPanelBackgroundDefault = true;
                break;
            }       
        }
        
        if (panel!=null){
            panel.setBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    caption, 
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                    (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                    (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                            getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));
            if (!isPanelBackgroundDefault)
                panel.setBackground(new java.awt.Color(220, 220, 220));
        }else{
            String message = "Unexpected null value for titled border panel encountered in PatientView::setBorderTitles() method";
            JOptionPane.showMessageDialog(this, message, "View error", JOptionPane.WARNING_MESSAGE);
        }
        
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDocumentStoreFiles = new javax.swing.JPanel();
        scrDocumentStoreFiles = new javax.swing.JScrollPane();
        lstDocumentStore = new javax.swing.JList<String>();
        pnlActions = new javax.swing.JPanel();
        btnOpen = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        setTitle("Patient document store");

        pnlDocumentStoreFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        lstDocumentStore.setModel(new javax.swing.DefaultListModel<String>() {
            String[] strings = {  };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        scrDocumentStoreFiles.setViewportView(lstDocumentStore);

        javax.swing.GroupLayout pnlDocumentStoreFilesLayout = new javax.swing.GroupLayout(pnlDocumentStoreFiles);
        pnlDocumentStoreFiles.setLayout(pnlDocumentStoreFilesLayout);
        pnlDocumentStoreFilesLayout.setHorizontalGroup(
            pnlDocumentStoreFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDocumentStoreFilesLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(scrDocumentStoreFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        pnlDocumentStoreFilesLayout.setVerticalGroup(
            pnlDocumentStoreFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDocumentStoreFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrDocumentStoreFiles)
                .addGap(12, 12, 12))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Action"));

        btnOpen.setText("Open");

        btnDelete.setText("Delete");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pnlDocumentStoreFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlDocumentStoreFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOpen;
    private javax.swing.JList<String> lstDocumentStore;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlDocumentStoreFiles;
    private javax.swing.JScrollPane scrDocumentStoreFiles;
    // End of variables declaration//GEN-END:variables
}
