/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.Descriptor;
import theclinic.controller.ScheduleViewController;
import theclinic.controller.SystemDefinition;
import theclinic.controller.ViewController;
import theclinic.view.View;
import theclinic.view.views.non_modal_views.DesktopView;
import com.bric.colorpicker.ColorPickerDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

/**
 *
 * @author colin
 */
public class ModalUserScheduleListSettingsView extends ModalView 
                                           implements ActionListener, 
                                                       PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalUserScheduleListSettingsView(View.Viewer myViewType,
                                             ViewController myController, 
                                             DesktopView desktopView) {
        setMyViewType(myViewType);
        setMyController(myController);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Action action = Action.valueOf(e.getActionCommand());
        switch(action){
            case REQUEST_CLOSE_VIEW ->{
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                /*sendActionRequestToController(
                        ViewController.UserSettingsViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION);*/
                break;
            }
            case REQUEST_USER_SCHEDULE_LIST_FACTORY_SETTINGS ->{
                sendActionRequestToController(
                        ScheduleViewController.Actions.USER_SCHEDULE_LIST_FACTORY_SETTINGS_REQUEST);
                break;
            }
            case REQUEST_USER_SCHEDULE_LIST_SETTINGS_UPDATE ->{
                copySettingsFromViewSettingsToViewDescription();
                sendActionRequestToController(
                        ScheduleViewController.Actions.USER_SCHEDULE_LIST_SETTINGS_UPDATE_REQUEST);
                break;
            }
            case REQUEST_LIST_BOOKABLE_SLOT_BACKGROUND_CHANGE,
                 REQUEST_LIST_BOOKABLE_SLOT_FOREGROUND_CHANGE,
                 REQUEST_LIST_EMERGENCY_BOOKING_BACKGROUND_CHANGE,
                 REQUEST_LIST_EMERGENCY_BOOKING_FOREGROUND_CHANGE,
                 REQUEST_LIST_UNBOOKABLE_SLOT_BACKGROUND_CHANGE, 
                 REQUEST_LIST_UNBOOKABLE_SLOT_FOREGROUND_CHANGE  ->{
                setSettingFor(action);
                break;
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        addListeners();
        copySettingsFromControllerDescriptionToViewDescription();
        copySettingsFromViewDescriptionToViewSettings();
    }
    
    private void addListeners(){
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnListBookableSlotBackgroundSetting.setActionCommand(Action.REQUEST_LIST_BOOKABLE_SLOT_BACKGROUND_CHANGE.toString());
        this.btnListBookableSlotForegroundSetting.setActionCommand(Action.REQUEST_LIST_BOOKABLE_SLOT_FOREGROUND_CHANGE.toString());
        this.btnListEmergencyBookingBackgroundSetting.setActionCommand(Action.REQUEST_LIST_EMERGENCY_BOOKING_BACKGROUND_CHANGE.toString());
        this.btnListEmergencyBookingForegroundSetting.setActionCommand(Action.REQUEST_LIST_EMERGENCY_BOOKING_FOREGROUND_CHANGE.toString());
        this.btnListUnbookableSlotBackgroundSetting.setActionCommand(Action.REQUEST_LIST_UNBOOKABLE_SLOT_BACKGROUND_CHANGE.toString());
        this.btnListUnbookableSlotforegoundSetting.setActionCommand(Action.REQUEST_LIST_UNBOOKABLE_SLOT_FOREGROUND_CHANGE.toString());
        this.btnFactorySettings.setActionCommand(Action.REQUEST_USER_SCHEDULE_LIST_FACTORY_SETTINGS.toString());
        this.btnUpdateSettings.setActionCommand(Action.REQUEST_USER_SCHEDULE_LIST_SETTINGS_UPDATE.toString());
        this.btnCloseView.addActionListener(this);
        this.btnListBookableSlotBackgroundSetting.addActionListener(this);
        this.btnListBookableSlotForegroundSetting.addActionListener(this);
        this.btnListEmergencyBookingBackgroundSetting.addActionListener(this);
        this.btnListEmergencyBookingForegroundSetting.addActionListener(this);
        this.btnListUnbookableSlotBackgroundSetting.addActionListener(this);
        this.btnListUnbookableSlotforegoundSetting.addActionListener(this);
        this.btnFactorySettings.addActionListener(this);
        this.btnUpdateSettings.addActionListener(this);  
    }
    
    private void sendActionRequestToController(
            ScheduleViewController.Actions request){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            request.toString());
        this.getMyController().actionPerformed(actionEvent);
        /*actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            ViewController.UserSettingsViewControllerActionEvent.
                    VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);*/
    }
    
    private void copySettingsFromControllerDescriptionToViewDescription(){
        setControllerDescription(getMyController().getDescriptor().getControllerDescription());
        setViewDescription(getMyController().getDescriptor().getViewDescription());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND));
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND));
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND));
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND));
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND));
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND,
                getControllerDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND));
        
    }
    
    private void copySettingsFromViewDescriptionToViewSettings(){
        setViewDescription(getMyController().getDescriptor().getViewDescription());
        this.setBookableSlotBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND));
        this.setBookableSlotForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND));
        this.setEmergencyBookingBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND));
        this.setEmergencyBookingForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND));
        this.setUnbookableSlotBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND));
        this.setUnbookableSlotForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND));
    }
    
    private void copySettingsFromViewSettingsToViewDescription(){
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_BACKGROUND, this.getBookableSlotBackground());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_BOOKABLE_SLOT_FOREGROUND, this.getBookableSlotForeground());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_BACKGROUND, this.getEmergencyBookingBackground());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_EMERGENCY_BOOKING_FOREGROUND, this.getEmergencyBookingForeground());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_BACKGROUND, this.getUnbookableSlotBackground());
        getViewDescription().setProperty(SystemDefinition.Properties.LIST_UNBOOKABLE_SLOT_FOREGROUND, this.getUnbookableSlotForeground());
    }
    
    private Descriptor.ControllerDescription controllerDescription = null;
    private Descriptor.ControllerDescription getControllerDescription(){
        return controllerDescription;
    }
    private void setControllerDescription(Descriptor.ControllerDescription value){
        controllerDescription = value;
    }
    
    private Descriptor.ViewDescription viewDescription = null;
    private Descriptor.ViewDescription getViewDescription(){
        return viewDescription;
    }
    private void setViewDescription(Descriptor.ViewDescription value){
        viewDescription = value;
    }
    
    private void setSettingFor(Action action){
        switch (action){
            case REQUEST_LIST_BOOKABLE_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getBookableSlotBackground();
                this.setBookableSlotBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_LIST_BOOKABLE_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getBookableSlotForeground();
                this.setBookableSlotForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_LIST_EMERGENCY_BOOKING_BACKGROUND_CHANGE ->{
                Color initialColor = this.getEmergencyBookingBackground();
                this.setEmergencyBookingBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_LIST_EMERGENCY_BOOKING_FOREGROUND_CHANGE ->{
                Color initialColor = this.getEmergencyBookingForeground();
                this.setEmergencyBookingForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_LIST_UNBOOKABLE_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getUnbookableSlotBackground();
                this.setUnbookableSlotBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            } 
            case REQUEST_LIST_UNBOOKABLE_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getUnbookableSlotForeground();
                this.setUnbookableSlotForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }     
        }
    }

    private Color bookableSlotBackground = null;
    private void setBookableSlotBackground(Color value){
        bookableSlotBackground = value;
        this.lblListBookableSlotBackgroundSetting.setOpaque(true);
        this.lblListBookableSlotBackgroundSetting.setBackground(bookableSlotBackground);
    }
    private Color getBookableSlotBackground(){
        return bookableSlotBackground;
    }
    
    private Color bookableSlotForeground = null;
    private void setBookableSlotForeground(Color value){
        bookableSlotForeground = value;
        this.lblListBookableSlotForegroundSetting.setOpaque(true);
        this.lblListBookableSlotForegroundSetting.setBackground(bookableSlotForeground);
    }
    private Color getBookableSlotForeground(){
        return bookableSlotForeground;
    }
    
    private Color emergencyBookingBackground = null;
    private void setEmergencyBookingBackground(Color value){
        emergencyBookingBackground = value;
        this.lblListEmergencyBookingBackgroundSetting.setOpaque(true);
        this.lblListEmergencyBookingBackgroundSetting.setBackground(emergencyBookingBackground);
    }
    private Color getEmergencyBookingBackground(){
        return emergencyBookingBackground;
    }
    
    private Color emergencyBookingForeground = null;
    private void setEmergencyBookingForeground(Color value){
        emergencyBookingForeground = value;
        this.lblListEmergencyBookingForegroundSetting.setOpaque(true);
        this.lblListEmergencyBookingForegroundSetting.setBackground(emergencyBookingForeground);
    }
    private Color getEmergencyBookingForeground(){
        return emergencyBookingForeground;
    }
    
    private Color unbookableSlotBackground = null;
    private void setUnbookableSlotBackground(Color value){
        unbookableSlotBackground = value;
        this.lblListUnbookableSlotBackgroundSetting.setOpaque(true);
        this.lblListUnbookableSlotBackgroundSetting.setBackground(unbookableSlotBackground);
    }
    private Color getUnbookableSlotBackground(){
        return unbookableSlotBackground;
    }
    
    private Color unbookableSlotForeground = null;
    private void setUnbookableSlotForeground(Color value){
        unbookableSlotForeground = value;
        this.lblListUnbookableSlotForegroundSetting.setOpaque(true);
        this.lblListUnbookableSlotForegroundSetting.setBackground(unbookableSlotForeground);
    }
    private Color getUnbookableSlotForeground(){
        return unbookableSlotForeground;
    }

    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_LIST_BOOKABLE_SLOT_BACKGROUND_CHANGE,
        REQUEST_LIST_BOOKABLE_SLOT_FOREGROUND_CHANGE,
        REQUEST_LIST_EMERGENCY_BOOKING_BACKGROUND_CHANGE,
        REQUEST_LIST_EMERGENCY_BOOKING_FOREGROUND_CHANGE,
        REQUEST_LIST_UNBOOKABLE_SLOT_BACKGROUND_CHANGE, 
        REQUEST_LIST_UNBOOKABLE_SLOT_FOREGROUND_CHANGE, 
        REQUEST_USER_SCHEDULE_LIST_FACTORY_SETTINGS,
        REQUEST_USER_SCHEDULE_LIST_SETTINGS_UPDATE
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlColorSettings = new javax.swing.JPanel();
        pnlEmptySlotSettings = new javax.swing.JPanel();
        btnListBookableSlotBackgroundSetting = new javax.swing.JButton();
        btnListBookableSlotForegroundSetting = new javax.swing.JButton();
        lblListBookableSlotBackgroundSetting = new javax.swing.JLabel();
        lblListBookableSlotForegroundSetting = new javax.swing.JLabel();
        pnlEmergencyBookingSettings = new javax.swing.JPanel();
        btnListEmergencyBookingBackgroundSetting = new javax.swing.JButton();
        btnListEmergencyBookingForegroundSetting = new javax.swing.JButton();
        lblListEmergencyBookingBackgroundSetting = new javax.swing.JLabel();
        lblListEmergencyBookingForegroundSetting = new javax.swing.JLabel();
        pnlUnbookableSlotSettings = new javax.swing.JPanel();
        btnListUnbookableSlotBackgroundSetting = new javax.swing.JButton();
        lblListUnbookableSlotBackgroundSetting = new javax.swing.JLabel();
        btnListUnbookableSlotforegoundSetting = new javax.swing.JButton();
        lblListUnbookableSlotForegroundSetting = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnFactorySettings = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnUpdateSettings = new javax.swing.JButton();

        setTitle("Schedule list colour settings");

        pnlColorSettings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        pnlEmptySlotSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Empty slot"));

        btnListBookableSlotBackgroundSetting.setText("Background");

        btnListBookableSlotForegroundSetting.setText("Foreground");

        lblListBookableSlotBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblListBookableSlotForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblListBookableSlotForegroundSetting.setOpaque(true);

        javax.swing.GroupLayout pnlEmptySlotSettingsLayout = new javax.swing.GroupLayout(pnlEmptySlotSettings);
        pnlEmptySlotSettings.setLayout(pnlEmptySlotSettingsLayout);
        pnlEmptySlotSettingsLayout.setHorizontalGroup(
            pnlEmptySlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmptySlotSettingsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnListBookableSlotBackgroundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnListBookableSlotForegroundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlEmptySlotSettingsLayout.setVerticalGroup(
            pnlEmptySlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmptySlotSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmptySlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblListBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblListBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlEmergencyBookingSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Emergency booking"));

        btnListEmergencyBookingBackgroundSetting.setText("Background");

        btnListEmergencyBookingForegroundSetting.setText("Foreground");

        lblListEmergencyBookingBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblListEmergencyBookingForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlEmergencyBookingSettingsLayout = new javax.swing.GroupLayout(pnlEmergencyBookingSettings);
        pnlEmergencyBookingSettings.setLayout(pnlEmergencyBookingSettingsLayout);
        pnlEmergencyBookingSettingsLayout.setHorizontalGroup(
            pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmergencyBookingSettingsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnListEmergencyBookingBackgroundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnListEmergencyBookingForegroundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlEmergencyBookingSettingsLayout.setVerticalGroup(
            pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmergencyBookingSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblListEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblListEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlUnbookableSlotSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Unbookable slot"));

        btnListUnbookableSlotBackgroundSetting.setText("Background");

        lblListUnbookableSlotBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnListUnbookableSlotforegoundSetting.setText("Foreground");

        lblListUnbookableSlotForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlUnbookableSlotSettingsLayout = new javax.swing.GroupLayout(pnlUnbookableSlotSettings);
        pnlUnbookableSlotSettings.setLayout(pnlUnbookableSlotSettingsLayout);
        pnlUnbookableSlotSettingsLayout.setHorizontalGroup(
            pnlUnbookableSlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnbookableSlotSettingsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnListUnbookableSlotBackgroundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnListUnbookableSlotforegoundSetting)
                .addGap(12, 12, 12)
                .addComponent(lblListUnbookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUnbookableSlotSettingsLayout.setVerticalGroup(
            pnlUnbookableSlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnbookableSlotSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnbookableSlotSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblListUnbookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListUnbookableSlotforegoundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblListUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlColorSettingsLayout = new javax.swing.GroupLayout(pnlColorSettings);
        pnlColorSettings.setLayout(pnlColorSettingsLayout);
        pnlColorSettingsLayout.setHorizontalGroup(
            pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlColorSettingsLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlUnbookableSlotSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEmergencyBookingSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEmptySlotSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        pnlColorSettingsLayout.setVerticalGroup(
            pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColorSettingsLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(pnlEmergencyBookingSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlEmptySlotSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlUnbookableSlotSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnFactorySettings.setText("Factory settings");
        btnFactorySettings.setSelected(true);

        btnCloseView.setText("Close view");
        btnCloseView.setSelected(true);

        btnUpdateSettings.setText("Update settings");
        btnUpdateSettings.setSelected(true);

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdateSettings, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(btnUpdateSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnlColorSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlColorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnFactorySettings;
    private javax.swing.JButton btnListBookableSlotBackgroundSetting;
    private javax.swing.JButton btnListBookableSlotForegroundSetting;
    private javax.swing.JButton btnListEmergencyBookingBackgroundSetting;
    private javax.swing.JButton btnListEmergencyBookingForegroundSetting;
    private javax.swing.JButton btnListUnbookableSlotBackgroundSetting;
    private javax.swing.JButton btnListUnbookableSlotforegoundSetting;
    private javax.swing.JButton btnUpdateSettings;
    private javax.swing.JLabel lblListBookableSlotBackgroundSetting;
    private javax.swing.JLabel lblListBookableSlotForegroundSetting;
    private javax.swing.JLabel lblListEmergencyBookingBackgroundSetting;
    private javax.swing.JLabel lblListEmergencyBookingForegroundSetting;
    private javax.swing.JLabel lblListUnbookableSlotBackgroundSetting;
    private javax.swing.JLabel lblListUnbookableSlotForegroundSetting;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlColorSettings;
    private javax.swing.JPanel pnlEmergencyBookingSettings;
    private javax.swing.JPanel pnlEmptySlotSettings;
    private javax.swing.JPanel pnlUnbookableSlotSettings;
    // End of variables declaration//GEN-END:variables
}
