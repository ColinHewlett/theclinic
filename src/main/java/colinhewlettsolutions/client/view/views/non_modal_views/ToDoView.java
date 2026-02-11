/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.non_modal_views;

import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.Descriptor;
import colinhewlettsolutions.client.controller.ViewController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import colinhewlettsolutions.client.model.entity.ToDo;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.support_classes.models.ToDoViewTableModel;
import colinhewlettsolutions.client.view.support_classes.renderers.LocalDateRenderer;
import colinhewlettsolutions.client.view.support_classes.renderers.PatientNotificationTableLocalDateRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author colin
 */
public class ToDoView extends View implements ActionListener,
                                              TableModelListener,
                                              ListSelectionListener,
                                              PropertyChangeListener{

    enum Action{
        REQUEST_ALL_TO_DO,
        REQUEST_CANCEL_TO_DO,
        CLOSE_VIEW,
        REQUEST_CREATE_TO_DO,
        REQUEST_UNACTIONED_TO_DO,
        REQUEST_UPDATE_TO_DO
    }
    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ToDoView(View.Viewer myViewType, 
            ViewController myController, DesktopView desktopView) {
        setTitle("'To Do' list");
        this.setMyViewType(myViewType);
        setMyController(myController);  
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        ViewController.NotificationViewControllerActionEvent
                actionCommand = null;
        switch (Action.valueOf(e.getActionCommand())){
            case REQUEST_CREATE_TO_DO:
                doCreateToDo();
                break;
            case REQUEST_ALL_TO_DO:
                this.doReadAllToDos();
                break;
            case REQUEST_CANCEL_TO_DO:
                this.doCancelToDo();
                break;
            case CLOSE_VIEW:
                doCloseView();
                break;
            case REQUEST_UNACTIONED_TO_DO:
                this.doReadUnactionedToDos();
                break;
            case REQUEST_UPDATE_TO_DO:
                doUpdateToDo();
                break;
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                this.btnEditSelectedToDo.setEnabled(true);
                this.btnCancelSelectedToDo.setEnabled(true);
                this.btnAddNewToDo.setEnabled(false);
                int selectedRow = this.tblToDo.getSelectedRow();
                ToDoViewTableModel model = 
                        (ToDoViewTableModel)tblToDo.getModel();
                ToDo toDo = model.getElementAt(selectedRow);
                getMyController().getDescriptor().getViewDescription()
                        .setProperty(SystemDefinition.Properties.TO_DO, toDo);
            }else{
                this.btnEditSelectedToDo.setEnabled(false);
                this.btnCancelSelectedToDo.setEnabled(false);
                this.btnAddNewToDo.setEnabled(true);
            }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        ViewController.ToDoViewControllerPropertyChangeEvent
                propertyName = ViewController.ToDoViewControllerPropertyChangeEvent.
                        valueOf(e.getPropertyName());
        
        switch (propertyName){
            case RECEIVED_TO_DOs:
                populateToDoTable(
                        (ArrayList<ToDo>)getMyController().getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.TO_DOS));
                setTitle(UI_ALL_TO_DOS_TITLE);
                this.tblToDo.clearSelection();
                this.rdbDisplayAllOptions.setSelected(true);
                break;
            case RECEIVED_UNACTIONED_TO_DOs:
                populateToDoTable(
                        (ArrayList<ToDo>)getMyController().getDescriptor().getControllerDescription().
                                getProperty(SystemDefinition.Properties.TO_DOS));
                setTitle(UI_UNACTIONED_TO_DOS_TITLE);
                this.tblToDo.clearSelection();
                this.rdbDisplayUnactionedToDo.setSelected(true);
                break;
        }
    }  
    
    @Override
    public void tableChanged(TableModelEvent e){
        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column!=-1){
            ToDoViewTableModel model =  
                    (ToDoViewTableModel)e.getSource();
            Boolean value = (Boolean)model.getValueAt(row, column);
            ToDo toDo = model.getElementAt(row);
            toDo.setIsActioned(value);
            getMyController().getDescriptor().getViewDescription().setProperty(SystemDefinition.Properties.TO_DO, toDo);
            //tblAppointments.clearSelection();

            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ToDoViewControllerActionEvent.
                        ACTION_TO_DO_REQUEST.toString());
            getMyController().actionPerformed(actionEvent);
            this.tblToDo.clearSelection();
        }
    }
    
    private final String UI_UNACTIONED_TO_DOS_TITLE = "'To do' list";
    private final String UI_ALL_TO_DOS_TITLE = "'To do' list";
    public void initialiseView(){
        //javax.swing.SwingUtilities.invokeLater(() -> {
            initComponents();

            setClosable(true);
            buttonGroup1.add(this.rdbDisplayAllOptions);
            buttonGroup1.add(this.rdbDisplayUnactionedToDo);
            rdbDisplayAllOptions.setActionCommand(Action.REQUEST_ALL_TO_DO.toString());
            rdbDisplayUnactionedToDo.setActionCommand(Action.REQUEST_UNACTIONED_TO_DO.toString());
            rdbDisplayAllOptions.addActionListener(this);
            rdbDisplayUnactionedToDo.addActionListener(this);
            rdbDisplayUnactionedToDo.setSelected(true);
            setUITitle(UI_UNACTIONED_TO_DOS_TITLE);

            try{
                setVisible(true);
                setTitle(getUITitle());
                setClosable(true);
                setMaximizable(false);
                setIconifiable(true);
                setResizable(false);
                setSelected(true);
                setSize(1078,455);

            }
            catch (PropertyVetoException ex){

            }

            this.pnlToDo.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    "To do list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                    (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                    (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));

            this.pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createEtchedBorder(), 
                    "'To do' actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                    (java.awt.Font)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT),
                    (java.awt.Color)getMyController().getDescriptor().getControllerDescription().
                        getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR)));


            this.btnAddNewToDo.setActionCommand(Action.REQUEST_CREATE_TO_DO.toString());
            this.btnAddNewToDo.addActionListener(this);
            this.btnEditSelectedToDo.setActionCommand(Action.REQUEST_UPDATE_TO_DO.toString());
            this.btnEditSelectedToDo.addActionListener(this);
            this.btnCancelSelectedToDo.setActionCommand(Action.REQUEST_CANCEL_TO_DO.toString());
            this.btnCancelSelectedToDo.addActionListener(this);
            this.btnCloseView.setActionCommand(Action.CLOSE_VIEW.toString());
            this.btnCloseView.addActionListener(this);
            
            this.btnEditSelectedToDo.setEnabled(false);
            this.btnCancelSelectedToDo.setEnabled(false);
            this.btnAddNewToDo.setEnabled(true);
                
            addInternalFrameListeners();
            createToDoTable();
            setToDoTableListener();
            
            // Add a component listener to adjust column widths after it is displayed
            /*tblToDo.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    adjustColumnWidthsAndViewPosition(tblToDo);
                }
            });*/
            
            ActionEvent actionEvent = new ActionEvent(
                this,ActionEvent.ACTION_PERFORMED,
                ViewController.ToDoViewControllerActionEvent.UNACTIONED_TO_DO_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            //adjustColumnWidths(tblToDo);
        //});
        
    }
    
    private void adjustColumnWidths(JTable table){
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewController.setRelativeColumnWidths(table, 794, new double[]{0.1,0.15,0.75});
            
        });
    }
    
    private void adjustColumnWidthsAndViewPosition(JTable table){
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewController.setRelativeColumnWidths(table, 794, new double[]{0.1,0.15,0.75});
            //ViewController.centerInternalFrame(getDesktopView().getDeskTop(), this);
        });
    }
    
    /**
     * Establish an InternalFrameListener for when the view is closed 
     * Setting DISPOSE_ON_CLOSE action when the window "X" is clicked, fires
     * InternalFrameEvent.INTERNAL_FRAME_CLOSED event for the listener to let 
     * the view controller know what's happening
     */
    private InternalFrameAdapter internalFrameAdapter = null;
    public void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        ToDoView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.ToDoViewControllerActionEvent.
                                VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
            @Override  
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        ToDoView.this,ActionEvent.ACTION_PERFORMED,
                        ViewController.NotificationViewControllerActionEvent.
                                VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private boolean tableValueChangedListenerActivated = false;
    private void setToDoTableListener(){
        this.tblToDo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblToDo.getSelectionModel();
        
        lsm.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {   // Ensure the event is not fired multiple times
                    int selectedRow = tblToDo.getSelectedRow();
                    if (selectedRow!=-1){
                        tableValueChangedListenerActivated = true;
                        //Patient patient = (Patient)tblAppointments.getModel().getValueAt(selectedRow, 0);
                        //doScheduleTitleRefresh(patient);
                    }
                    //else doScheduleTitleRefresh(null);   
                }
            }
        });
        
        tblToDo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!tableValueChangedListenerActivated){
                    int selectedRow = tblToDo.rowAtPoint(e.getPoint());
                    if (selectedRow!=-1 && tblToDo.isRowSelected(selectedRow))
                    tblToDo.clearSelection(); // Deselect the clicked row
                }else tableValueChangedListenerActivated = false;
            }
        });
        
    }
    
    
    private void populateToDoTable(ArrayList<ToDo> patientToDos){
        
        ToDoViewTableModel model = 
                (ToDoViewTableModel)this.tblToDo.getModel();
        model.removeAllElements();
        Iterator<ToDo> it = patientToDos.iterator();
        while (it.hasNext()){
            ((ToDoViewTableModel)this.tblToDo.getModel()).addElement(it.next());
        }
        TableColumn column = tblToDo.getColumnModel().getColumn(0);
        column.setPreferredWidth(96);
        column = tblToDo.getColumnModel().getColumn(1);
        column.setPreferredWidth(96);
        column = tblToDo.getColumnModel().getColumn(2);
        column.setPreferredWidth(771);
        tblToDo.setDefaultRenderer(LocalDate.class, new LocalDateRenderer());
        tblToDo.revalidate();
        tblToDo.repaint();
    }
    
    
    
    private void doCancelToDo(){
        boolean isError = false;
        if (this.tblToDo.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A toDo has not been selected for cancellation");
            isError = true;
        }
        if (!isError){
            String[] options = {"Yes", "No"};
            int reply = JOptionPane.showOptionDialog(this,
                        "Are you sure you want to cancel the selected "
                                + "notificatiom?",null,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);
            if (reply==JOptionPane.YES_OPTION){
                int row = this.tblToDo.getSelectedRow();
                ToDoViewTableModel model = 
                    (ToDoViewTableModel)this.tblToDo.getModel();
                getMyController().getDescriptor().getViewDescription().
                        setProperty(SystemDefinition.Properties.TO_DO, model.getElementAt(row));
                ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.ToDoViewControllerActionEvent.
                            CANCEL_TO_DO_REQUEST.toString());
                this.getMyController().actionPerformed(actionEvent);
            }
        }
    }
    
    private void doCloseView(){
        try{
            this.setClosed(true);
        }
        catch (PropertyVetoException ex){
            
        }
    }
    
    private void doCreateToDo(){
        ActionEvent actionEvent = new ActionEvent(
        this,ActionEvent.ACTION_PERFORMED,
                ViewController.ToDoViewControllerActionEvent.CREATE_TO_DO_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        this.tblToDo.clearSelection();
    }
    
    private void doReadAllToDos(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                    ViewController.ToDoViewControllerActionEvent.TO_DOs_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setUITitle(UI_ALL_TO_DOS_TITLE);
    }
    
    private void doReadUnactionedToDos(){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
                    ViewController.ToDoViewControllerActionEvent.UNACTIONED_TO_DO_REQUEST.toString());
        this.getMyController().actionPerformed(actionEvent);
        setUITitle(UI_UNACTIONED_TO_DOS_TITLE);
    }
    
    private void doUpdateToDo(){
        boolean isError = false;
        if (this.tblToDo.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(this, "A 'to do' item has not been selected");
            isError = true;
        }
        if (!isError){
            ToDoViewTableModel model = 
                (ToDoViewTableModel)this.tblToDo.getModel();
            getMyController().getDescriptor().getViewDescription().
                    setProperty(SystemDefinition.Properties.TO_DO, model.getElementAt(this.tblToDo.getSelectedRow()));
            ActionEvent actionEvent = new ActionEvent(
                    this,ActionEvent.ACTION_PERFORMED,
                    ViewController.ToDoViewControllerActionEvent.UPDATE_TO_DO_REQUEST.toString());
            this.getMyController().actionPerformed(actionEvent);
            this.tblToDo.clearSelection();
            //ViewController.setJTableColumnProperties(tblToDo, 964, 10,10,80);
            //ViewController.setRelativeColumnWidths(this.tblToDo, 964, new double[]{0.1,0.1,0.85});
            //adjustColumnWidthsAndViewPosition(tblToDo);
        }
    }
    
    private void createToDoTable(){
        this.tblToDo = null;
        this.tblToDo = new JTable(new ToDoViewTableModel(ToDoViewTableModel.ViewMode.WITH_DATE));
        ToDoViewTableModel model = (ToDoViewTableModel)tblToDo.getModel();
        model.addTableModelListener(this);
        this.tblToDo.setDefaultRenderer(LocalDate.class, new PatientNotificationTableLocalDateRenderer());
        setToDoTableDefaultRenderer(this.tblToDo.getDefaultRenderer(LocalDate.class));
        scrToDoTable.setViewportView(this.tblToDo);
        //ViewController.setJTableColumnProperties(tblToDo, 964, 10,10,80);
        this.tblToDo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel lsm = this.tblToDo.getSelectionModel();
        lsm.addListSelectionListener(this);
        //adjustColumnWidths(tblToDo);
    }
    
    private TableCellRenderer patientToDoTableDefaultRenderer = null;
    private TableCellRenderer getPatientToDoTableDefaultRenderer(){
        return patientToDoTableDefaultRenderer;
    }
    private void setToDoTableDefaultRenderer(TableCellRenderer renderer){
        patientToDoTableDefaultRenderer = renderer;
    }
    
    private String UITitle = null;
    private String getUITitle(){
        return UITitle;
    }
    private void setUITitle(String title){
        UITitle = title;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlToDo = new javax.swing.JPanel();
        scrToDoTable = new javax.swing.JScrollPane();
        tblToDo = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        rdbDisplayUnactionedToDo = new javax.swing.JRadioButton();
        rdbDisplayAllOptions = new javax.swing.JRadioButton();
        pnlActions = new javax.swing.JPanel();
        btnAddNewToDo = new javax.swing.JButton();
        btnEditSelectedToDo = new javax.swing.JButton();
        btnCancelSelectedToDo = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        pnlToDo.setBorder(javax.swing.BorderFactory.createTitledBorder("To do list"));

        tblToDo.setModel(new ToDoViewTableModel(ToDoViewTableModel.ViewMode.WITH_DATE));
        scrToDoTable.setViewportView(tblToDo);
        //ViewController.setJTableColumnProperties(tblToDo, 964, 10,5,85);

        javax.swing.GroupLayout pnlToDoLayout = new javax.swing.GroupLayout(pnlToDo);
        pnlToDo.setLayout(pnlToDoLayout);
        pnlToDoLayout.setHorizontalGroup(
            pnlToDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToDoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrToDoTable)
                .addContainerGap())
        );
        pnlToDoLayout.setVerticalGroup(
            pnlToDoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlToDoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrToDoTable, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );

        rdbDisplayUnactionedToDo.setText("only display unactioned 'To do' items");

        rdbDisplayAllOptions.setText("display all 'To Do' items");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(327, Short.MAX_VALUE)
                .addComponent(rdbDisplayUnactionedToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(rdbDisplayAllOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(123, 123, 123))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbDisplayUnactionedToDo)
                    .addComponent(rdbDisplayAllOptions))
                .addGap(14, 14, 14))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnAddNewToDo.setText("<html><center>Create</center><center>new task</center></html>");

        btnEditSelectedToDo.setText("<html><center>Update</center><center>selected task</center></html>");

        btnCancelSelectedToDo.setText("<html><center>Cancel</center><center>selected task</center><center>from 'to do' list</center></html>");

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActionsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAddNewToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEditSelectedToDo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelSelectedToDo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddNewToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnEditSelectedToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnCancelSelectedToDo, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlToDo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlToDo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNewToDo;
    private javax.swing.JButton btnCancelSelectedToDo;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnEditSelectedToDo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlToDo;
    private javax.swing.JRadioButton rdbDisplayAllOptions;
    private javax.swing.JRadioButton rdbDisplayUnactionedToDo;
    private javax.swing.JScrollPane scrToDoTable;
    private javax.swing.JTable tblToDo;
    // End of variables declaration//GEN-END:variables
}
