/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package colinhewlettsolutions.client.view.views.modal_views;

import com.bric.colorpicker.listeners.ColorListener;
import com.bric.colorpicker.models.ColorModel;
import com.bric.colorpicker.ColorPickerDialog;
import colinhewlettsolutions.client.controller.Descriptor.ControllerDescription;
import colinhewlettsolutions.client.controller.Descriptor.ViewDescription;
import colinhewlettsolutions.client.controller.SystemDefinition;
import colinhewlettsolutions.client.controller.SystemDefinition.Properties;
import colinhewlettsolutions.client.controller.ViewController;
import colinhewlettsolutions.client.controller.ViewController.UserSettingsViewControllerActionEvent;
import colinhewlettsolutions.client.view.View;
import colinhewlettsolutions.client.view.views.modal_views.ModalView;
import colinhewlettsolutions.client.view.views.non_modal_views.DesktopView;
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
public class ModalUserScheduleDiarySettingsView extends ModalView 
                                            implements ActionListener, 
                                                       PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public ModalUserScheduleDiarySettingsView (View.Viewer myViewType,
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
                break;
            }
            case REQUEST_USER_SCHEDULE_DIARY_FACTORY_SETTINGS ->{
                sendActionRequestToController(
                        ViewController.ScheduleViewControllerActionEvent.USER_SCHEDULE_DIARY_FACTORY_SETTINGS_REQUEST);
                copySettingsFromControllerDescriptionToViewDescription();
                copySettingsFromViewDescriptionToViewSettings();
                break;
            }
            case REQUEST_USER_SCHEDULE_DIARY_SETTINGS_UPDATE ->{
                copyCurrentViewSettingsToViewDescription();
                sendActionRequestToController(
                        ViewController.ScheduleViewControllerActionEvent.USER_SCHEDULE_DIARY_SETTINGS_UPDATE_REQUEST);
                copySettingsFromControllerDescriptionToViewDescription();
                copySettingsFromViewDescriptionToViewSettings();
                break;
            }
            case REQUEST_DIARY_BOOKABLE_SLOT_BACKGROUND_CHANGE,
                 REQUEST_DIARY_BOOKABLE_SLOT_FOREGROUND_CHANGE,
                 REQUEST_DIARY_BOOKING_FIRST_SLOT_BACKGROUND_CHANGE,
                 REQUEST_DIARY_BOOKING_FIRST_SLOT_FOREGROUND_CHANGE,
                 REQUEST_DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND_CHANGE, 
                 REQUEST_DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND_CHANGE, 
                 REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND_CHANGE,
                 REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND_CHANGE, 
                 REQUEST_DIARY_UNBOOKABLE_SLOT_BACKGROUND_CHANGE, 
                 REQUEST_DIARY_UNBOOKABLE_SLOT_FOREGROUND_CHANGE ->{
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
        ViewController vc = getMyController();
        ViewController dvc = getMyController().getMyController();
        initComponents();
        setVisible(true);
                
        copySettingsFromControllerDescriptionToViewDescription();
        copySettingsFromViewDescriptionToViewSettings();
        addListeners();
    }
    
    private void addListeners(){
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnBookableSlotBackgroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKABLE_SLOT_BACKGROUND_CHANGE.toString());
        this.btnBookableSlotForegroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKABLE_SLOT_FOREGROUND_CHANGE.toString());
        this.btnBookingHeaderBackgroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKING_FIRST_SLOT_BACKGROUND_CHANGE.toString());
        this.btnBookingHeaderForegroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKING_FIRST_SLOT_FOREGROUND_CHANGE.toString());
        this.btnBookingRemainingRowsBackgroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND_CHANGE.toString());
        this.btnBookingRemainingRowsForegroundSetting.setActionCommand(Action.REQUEST_DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND_CHANGE.toString());
        this.btnEmergencyBookingBackgroundSetting.setActionCommand(Action.REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND_CHANGE.toString());
        this.btnEmergencyBookingForegroundSetting.setActionCommand(Action.REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND_CHANGE.toString());
        this.btnUnbookableSlotBackgroundSetting.setActionCommand(Action.REQUEST_DIARY_UNBOOKABLE_SLOT_BACKGROUND_CHANGE.toString());
        this.btnUnbookableSlotForegroundSetting.setActionCommand(Action.REQUEST_DIARY_UNBOOKABLE_SLOT_FOREGROUND_CHANGE.toString());
        this.btnFactorySettings.setActionCommand(Action.REQUEST_USER_SCHEDULE_DIARY_FACTORY_SETTINGS.toString());
        this.btnUpdateSettings.setActionCommand(Action.REQUEST_USER_SCHEDULE_DIARY_SETTINGS_UPDATE.toString());
        this.btnBookingHeaderBackgroundSetting.addActionListener(this);
        this.btnBookingHeaderForegroundSetting.addActionListener(this);
        this.btnBookableSlotBackgroundSetting.addActionListener(this);
        this.btnBookableSlotForegroundSetting.addActionListener(this);
        this.btnBookingRemainingRowsBackgroundSetting.addActionListener(this);
        this.btnBookingRemainingRowsForegroundSetting.addActionListener(this);
        this.btnEmergencyBookingBackgroundSetting.addActionListener(this);
        this.btnEmergencyBookingForegroundSetting.addActionListener(this);
        this.btnUnbookableSlotBackgroundSetting.addActionListener(this);
        this.btnUnbookableSlotForegroundSetting.addActionListener(this);
        this.btnCloseView.addActionListener(this);
        this.btnFactorySettings.addActionListener(this);
        this.btnUpdateSettings.addActionListener(this);  
    }
    
    private void sendActionRequestToController(
            ViewController.ScheduleViewControllerActionEvent request){
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
        getViewDescription().setProperty(Properties.DIARY_BOOKABLE_SLOT_BACKGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKABLE_SLOT_BACKGROUND));
        getViewDescription().setProperty(Properties.DIARY_BOOKABLE_SLOT_FOREGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKABLE_SLOT_FOREGROUND));
        getViewDescription().setProperty(Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND));
        getViewDescription().setProperty(Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND));
        getViewDescription().setProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND));
        getViewDescription().setProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND,
                getControllerDescription().getProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND));
        getViewDescription().setProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND,
                getControllerDescription().getProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND));
        getViewDescription().setProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND,
                getControllerDescription().getProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND));
        getViewDescription().setProperty(Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND,
                getControllerDescription().getProperty(Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND));
        getViewDescription().setProperty(Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND,
                getControllerDescription().getProperty(Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND));
    }
    
    private void copySettingsFromViewDescriptionToViewSettings(){
        setViewDescription(getMyController().getDescriptor().getViewDescription());
        this.setBookableSlotBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_BACKGROUND));
        this.setBookableSlotForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKABLE_SLOT_FOREGROUND));
        this.setBookingHeaderBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND));
        this.setBookingHeaderForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND));
        this.setBookingRemainingRowsBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND));
        this.setBookingRemainingRowsForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND));
        this.setEmergencyBookingBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND));
        this.setEmergencyBookingForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND));
        this.setUnbookableSlotBackground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND));
        this.setUnbookableSlotForeground((Color)getViewDescription().getProperty(SystemDefinition.Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND));
    }
    
    private void copyCurrentViewSettingsToViewDescription(){
        getViewDescription().setProperty(Properties.DIARY_BOOKABLE_SLOT_BACKGROUND, this.getBookableSlotBackground());
        getViewDescription().setProperty(Properties.DIARY_BOOKABLE_SLOT_FOREGROUND, this.getBookableSlotForeground());
        getViewDescription().setProperty(Properties.DIARY_BOOKING_FIRST_SLOT_BACKGROUND, this.getBookingHeaderBackground());
        getViewDescription().setProperty(Properties.DIARY_BOOKING_FIRST_SLOT_FOREGROUND, this.getBookingHeaderForeground());
        getViewDescription().setProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND, this.getBookingRemainingRowsBackground());
        getViewDescription().setProperty(Properties.DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND, this.getBookingRemainingRowsForeground());
        getViewDescription().setProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND, this.getEmergencyBookingBackground());
        getViewDescription().setProperty(Properties.DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND, this.getEmergencyBookingForeground());
        getViewDescription().setProperty(Properties.DIARY_UNBOOKABLE_SLOT_BACKGROUND, this.getUnbookableSlotBackground());
        getViewDescription().setProperty(Properties.DIARY_UNBOOKABLE_SLOT_FOREGROUND, this.getUnbookableSlotForeground());
    }
    
    private ControllerDescription controllerDescription = null;
    private ControllerDescription getControllerDescription(){
        return controllerDescription;
    }
    private void setControllerDescription(ControllerDescription value){
        controllerDescription = value;
    }
    
    private ViewDescription viewDescription = null;
    private ViewDescription getViewDescription(){
        return viewDescription;
    }
    private void setViewDescription(ViewDescription value){
        viewDescription = value;
    }
    
    private void setSettingFor(Action action){
        switch (action){
            case REQUEST_DIARY_BOOKABLE_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getBookableSlotBackground();
                this.setBookableSlotBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_BOOKABLE_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getBookableSlotForeground();
                this.setBookableSlotForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_BOOKING_FIRST_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getBookingHeaderBackground();
                this.setBookingHeaderBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_BOOKING_FIRST_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getBookingHeaderForeground();
                this.setBookingHeaderForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND_CHANGE ->{
                Color initialColor = this.getBookingRemainingRowsBackground();
                this.setBookingRemainingRowsBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            } 
            case REQUEST_DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND_CHANGE ->{
                Color initialColor = this.getBookingRemainingRowsForeground();
                this.setBookingRemainingRowsForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            } 
            case REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getEmergencyBookingBackground();
                this.setEmergencyBookingBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getEmergencyBookingForeground();
                this.setEmergencyBookingForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_UNBOOKABLE_SLOT_BACKGROUND_CHANGE ->{
                Color initialColor = this.getUnbookableSlotBackground();
                this.setUnbookableSlotBackground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_DIARY_UNBOOKABLE_SLOT_FOREGROUND_CHANGE ->{
                Color initialColor = this.getUnbookableSlotForeground();
                this.setUnbookableSlotForeground(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }     
        }
    }
    
    private Color bookingHeaderBackground = null;
    private void setBookingHeaderBackground(Color value){
        bookingHeaderBackground = value;
        this.lblBookingHeaderBackgroundSetting.setOpaque(true);
        this.lblBookingHeaderBackgroundSetting.setBackground(bookingHeaderBackground);
    }
    private Color getBookingHeaderBackground(){
        return bookingHeaderBackground;
    }
    
    private Color bookingHeaderForeground = null;
    private void setBookingHeaderForeground(Color value){
        bookingHeaderForeground = value;
        this.lblBookingHeaderForegroundSetting.setOpaque(true);
        this.lblBookingHeaderForegroundSetting.setBackground(bookingHeaderForeground);
    }
    private Color getBookingHeaderForeground(){
        return bookingHeaderForeground;
    }
    
    private Color bookingRemainingRowsBackground = null;
    private void setBookingRemainingRowsBackground(Color value){
        bookingRemainingRowsBackground = value;
        this.lblBookingRemainingRowsBackgroundSetting.setOpaque(true);
        this.lblBookingRemainingRowsBackgroundSetting.setBackground(bookingRemainingRowsBackground);
    }
    private Color getBookingRemainingRowsBackground(){
        return bookingRemainingRowsBackground;
    }
    
    private Color bookingRemainingRowsForeground = null;
    private void setBookingRemainingRowsForeground(Color value){
        bookingRemainingRowsForeground = value;
        this.lblBookingRemainingRowsForegroundSetting.setOpaque(true);
        this.lblBookingRemainingRowsForegroundSetting.setBackground(bookingRemainingRowsForeground);
    }
    private Color getBookingRemainingRowsForeground(){
        return bookingRemainingRowsForeground;
    }
    
    private Color bookableSlotBackground = null;
    private void setBookableSlotBackground(Color value){
        bookableSlotBackground = value;
        this.lblBookableSlotBackgroundSetting.setOpaque(true);
        this.lblBookableSlotBackgroundSetting.setBackground(bookableSlotBackground);
    }
    private Color getBookableSlotBackground(){
        return bookableSlotBackground;
    }
    
    private Color bookableSlotForeground = null;
    private void setBookableSlotForeground(Color value){
        bookableSlotForeground = value;
        this.lblBookableSlotForegroundSetting.setOpaque(true);
        this.lblBookableSlotForegroundSetting.setBackground(bookableSlotForeground);
    }
    private Color getBookableSlotForeground(){
        return bookableSlotForeground;
    }
    
    private Color emergencyBookingBackground = null;
    private void setEmergencyBookingBackground(Color value){
        emergencyBookingBackground = value;
        this.lblEmergencyBookingBackgroundSetting.setOpaque(true);
        this.lblEmergencyBookingBackgroundSetting.setBackground(emergencyBookingBackground);
    }
    private Color getEmergencyBookingBackground(){
        return emergencyBookingBackground;
    }
    
    private Color emergencyBookingForeground = null;
    private void setEmergencyBookingForeground(Color value){
        emergencyBookingForeground = value;
        this.lblEmergencyBookingForegroundSetting.setOpaque(true);
        this.lblEmergencyBookingForegroundSetting.setBackground(emergencyBookingForeground);
    }
    private Color getEmergencyBookingForeground(){
        return emergencyBookingForeground;
    }
    
    private Color unbookableSlotBackground = null;
    private void setUnbookableSlotBackground(Color value){
        unbookableSlotBackground = value;
        this.lblUnbookableSlotBackgroundSetting.setOpaque(true);
        this.lblUnbookableSlotBackgroundSetting.setBackground(unbookableSlotBackground);
    }
    private Color getUnbookableSlotBackground(){
        return unbookableSlotBackground;
    }
    
    private Color unbookableSlotForeground = null;
    private void setUnbookableSlotForeground(Color value){
        unbookableSlotForeground = value;
        this.lblUnbookableSlotForegroundSetting.setOpaque(true);
        this.lblUnbookableSlotForegroundSetting.setBackground(unbookableSlotForeground);
    }
    private Color getUnbookableSlotForeground(){
        return unbookableSlotForeground;
    }
    
    private void doSendActionEvent(UserSettingsViewControllerActionEvent actionCommand){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            actionCommand.toString());
        getMyController().actionPerformed(actionEvent);
    }
    
    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_DIARY_BOOKABLE_SLOT_BACKGROUND_CHANGE,
        REQUEST_DIARY_BOOKABLE_SLOT_FOREGROUND_CHANGE,
        REQUEST_DIARY_BOOKING_FIRST_SLOT_BACKGROUND_CHANGE,
        REQUEST_DIARY_BOOKING_FIRST_SLOT_FOREGROUND_CHANGE,
        REQUEST_DIARY_BOOKING_REMAINING_SLOTS_BACKGROUND_CHANGE, 
        REQUEST_DIARY_BOOKING_REMAINING_SLOTS_FOREGROUND_CHANGE, 
        REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_BACKGROUND_CHANGE,
        REQUEST_DIARY_EMERGENCY_BOOKING_SLOT_FOREGROUND_CHANGE, 
        REQUEST_DIARY_UNBOOKABLE_SLOT_BACKGROUND_CHANGE, 
        REQUEST_DIARY_UNBOOKABLE_SLOT_FOREGROUND_CHANGE,
        REQUEST_USER_SCHEDULE_DIARY_FACTORY_SETTINGS,
        REQUEST_USER_SCHEDULE_DIARY_SETTINGS_UPDATE
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
        pnlBookingRemainingSlotsSetting = new javax.swing.JPanel();
        btnBookingRemainingRowsBackgroundSetting = new javax.swing.JButton();
        lblBookingRemainingRowsBackgroundSetting = new javax.swing.JLabel();
        btnBookingRemainingRowsForegroundSetting = new javax.swing.JButton();
        lblBookingRemainingRowsForegroundSetting = new javax.swing.JLabel();
        pnlBookingHeaderSetting = new javax.swing.JPanel();
        btnBookingHeaderBackgroundSetting = new javax.swing.JButton();
        lblBookingHeaderBackgroundSetting = new javax.swing.JLabel();
        btnBookingHeaderForegroundSetting = new javax.swing.JButton();
        lblBookingHeaderForegroundSetting = new javax.swing.JLabel();
        pnlEmptySlotSetting = new javax.swing.JPanel();
        btnBookableSlotBackgroundSetting = new javax.swing.JButton();
        lblBookableSlotBackgroundSetting = new javax.swing.JLabel();
        btnBookableSlotForegroundSetting = new javax.swing.JButton();
        lblBookableSlotForegroundSetting = new javax.swing.JLabel();
        pnlEmergencyBookingSettings = new javax.swing.JPanel();
        btnEmergencyBookingBackgroundSetting = new javax.swing.JButton();
        lblEmergencyBookingBackgroundSetting = new javax.swing.JLabel();
        btnEmergencyBookingForegroundSetting = new javax.swing.JButton();
        lblEmergencyBookingForegroundSetting = new javax.swing.JLabel();
        pnlUnbookableSlotSetting = new javax.swing.JPanel();
        btnUnbookableSlotBackgroundSetting = new javax.swing.JButton();
        lblUnbookableSlotBackgroundSetting = new javax.swing.JLabel();
        btnUnbookableSlotForegroundSetting = new javax.swing.JButton();
        lblUnbookableSlotForegroundSetting = new javax.swing.JLabel();
        pnlActions = new javax.swing.JPanel();
        btnFactorySettings = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();
        btnUpdateSettings = new javax.swing.JButton();

        setTitle("Diary schedule colour settings");

        pnlColorSettings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlColorSettings.setForeground(new java.awt.Color(102, 204, 255));

        pnlBookingRemainingSlotsSetting.setBorder(javax.swing.BorderFactory.createTitledBorder("Booking (remaining rows)"));

        btnBookingRemainingRowsBackgroundSetting.setText("Background");

        lblBookingRemainingRowsBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnBookingRemainingRowsForegroundSetting.setText("Foreground");

        lblBookingRemainingRowsForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlBookingRemainingSlotsSettingLayout = new javax.swing.GroupLayout(pnlBookingRemainingSlotsSetting);
        pnlBookingRemainingSlotsSetting.setLayout(pnlBookingRemainingSlotsSettingLayout);
        pnlBookingRemainingSlotsSettingLayout.setHorizontalGroup(
            pnlBookingRemainingSlotsSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBookingRemainingSlotsSettingLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnBookingRemainingRowsBackgroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookingRemainingRowsBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBookingRemainingRowsForegroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookingRemainingRowsForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBookingRemainingSlotsSettingLayout.setVerticalGroup(
            pnlBookingRemainingSlotsSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBookingRemainingSlotsSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBookingRemainingSlotsSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBookingRemainingRowsForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookingRemainingRowsForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBookingRemainingRowsBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookingRemainingRowsBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlBookingHeaderSetting.setBorder(javax.swing.BorderFactory.createTitledBorder("Booking (header)"));

        btnBookingHeaderBackgroundSetting.setText("Background");

        lblBookingHeaderBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnBookingHeaderForegroundSetting.setText("Foreground");

        lblBookingHeaderForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlBookingHeaderSettingLayout = new javax.swing.GroupLayout(pnlBookingHeaderSetting);
        pnlBookingHeaderSetting.setLayout(pnlBookingHeaderSettingLayout);
        pnlBookingHeaderSettingLayout.setHorizontalGroup(
            pnlBookingHeaderSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBookingHeaderSettingLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnBookingHeaderBackgroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookingHeaderBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBookingHeaderForegroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookingHeaderForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlBookingHeaderSettingLayout.setVerticalGroup(
            pnlBookingHeaderSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBookingHeaderSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBookingHeaderSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBookingHeaderForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookingHeaderForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBookingHeaderBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookingHeaderBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlEmptySlotSetting.setBorder(javax.swing.BorderFactory.createTitledBorder("Bookable slots"));

        btnBookableSlotBackgroundSetting.setText("Background");

        lblBookableSlotBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnBookableSlotForegroundSetting.setText("Foreground");

        lblBookableSlotForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlEmptySlotSettingLayout = new javax.swing.GroupLayout(pnlEmptySlotSetting);
        pnlEmptySlotSetting.setLayout(pnlEmptySlotSettingLayout);
        pnlEmptySlotSettingLayout.setHorizontalGroup(
            pnlEmptySlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmptySlotSettingLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnBookableSlotBackgroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBookableSlotForegroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEmptySlotSettingLayout.setVerticalGroup(
            pnlEmptySlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmptySlotSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmptySlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlEmergencyBookingSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Emergency booking"));

        btnEmergencyBookingBackgroundSetting.setText("Background");

        lblEmergencyBookingBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnEmergencyBookingForegroundSetting.setText("Foreground");

        lblEmergencyBookingForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlEmergencyBookingSettingsLayout = new javax.swing.GroupLayout(pnlEmergencyBookingSettings);
        pnlEmergencyBookingSettings.setLayout(pnlEmergencyBookingSettingsLayout);
        pnlEmergencyBookingSettingsLayout.setHorizontalGroup(
            pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmergencyBookingSettingsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnEmergencyBookingBackgroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEmergencyBookingForegroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEmergencyBookingSettingsLayout.setVerticalGroup(
            pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmergencyBookingSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmergencyBookingSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEmergencyBookingForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEmergencyBookingBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlUnbookableSlotSetting.setBorder(javax.swing.BorderFactory.createTitledBorder("Unbookable slot"));

        btnUnbookableSlotBackgroundSetting.setText("Background");

        lblUnbookableSlotBackgroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnUnbookableSlotForegroundSetting.setText("Foreground");

        lblUnbookableSlotForegroundSetting.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlUnbookableSlotSettingLayout = new javax.swing.GroupLayout(pnlUnbookableSlotSetting);
        pnlUnbookableSlotSetting.setLayout(pnlUnbookableSlotSettingLayout);
        pnlUnbookableSlotSettingLayout.setHorizontalGroup(
            pnlUnbookableSlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnbookableSlotSettingLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnUnbookableSlotBackgroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUnbookableSlotForegroundSetting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUnbookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUnbookableSlotSettingLayout.setVerticalGroup(
            pnlUnbookableSlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUnbookableSlotSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUnbookableSlotSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUnbookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUnbookableSlotForegroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUnbookableSlotBackgroundSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlColorSettingsLayout = new javax.swing.GroupLayout(pnlColorSettings);
        pnlColorSettings.setLayout(pnlColorSettingsLayout);
        pnlColorSettingsLayout.setHorizontalGroup(
            pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlColorSettingsLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlUnbookableSlotSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEmergencyBookingSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlEmptySlotSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlBookingHeaderSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlBookingRemainingSlotsSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        pnlColorSettingsLayout.setVerticalGroup(
            pnlColorSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlColorSettingsLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(pnlBookingHeaderSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlBookingRemainingSlotsSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlEmptySlotSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlEmergencyBookingSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlUnbookableSlotSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlActions.setForeground(new java.awt.Color(102, 204, 255));

        btnFactorySettings.setText("Factory settings");

        btnCloseView.setText("Close view");

        btnUpdateSettings.setText("Update settings");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnUpdateSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(btnUpdateSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCloseView.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(pnlColorSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlColorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBookableSlotBackgroundSetting;
    private javax.swing.JButton btnBookableSlotForegroundSetting;
    private javax.swing.JButton btnBookingHeaderBackgroundSetting;
    private javax.swing.JButton btnBookingHeaderForegroundSetting;
    private javax.swing.JButton btnBookingRemainingRowsBackgroundSetting;
    private javax.swing.JButton btnBookingRemainingRowsForegroundSetting;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnEmergencyBookingBackgroundSetting;
    private javax.swing.JButton btnEmergencyBookingForegroundSetting;
    private javax.swing.JButton btnFactorySettings;
    private javax.swing.JButton btnUnbookableSlotBackgroundSetting;
    private javax.swing.JButton btnUnbookableSlotForegroundSetting;
    private javax.swing.JButton btnUpdateSettings;
    private javax.swing.JLabel lblBookableSlotBackgroundSetting;
    private javax.swing.JLabel lblBookableSlotForegroundSetting;
    private javax.swing.JLabel lblBookingHeaderBackgroundSetting;
    private javax.swing.JLabel lblBookingHeaderForegroundSetting;
    private javax.swing.JLabel lblBookingRemainingRowsBackgroundSetting;
    private javax.swing.JLabel lblBookingRemainingRowsForegroundSetting;
    private javax.swing.JLabel lblEmergencyBookingBackgroundSetting;
    private javax.swing.JLabel lblEmergencyBookingForegroundSetting;
    private javax.swing.JLabel lblUnbookableSlotBackgroundSetting;
    private javax.swing.JLabel lblUnbookableSlotForegroundSetting;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBookingHeaderSetting;
    private javax.swing.JPanel pnlBookingRemainingSlotsSetting;
    private javax.swing.JPanel pnlColorSettings;
    private javax.swing.JPanel pnlEmergencyBookingSettings;
    private javax.swing.JPanel pnlEmptySlotSetting;
    private javax.swing.JPanel pnlUnbookableSlotSetting;
    // End of variables declaration//GEN-END:variables
}
