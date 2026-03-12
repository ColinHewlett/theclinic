/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package theclinic.view.views.modal_views;

import theclinic.controller.Descriptor;
import theclinic.controller.UserSystemWideSettingsViewController;
import theclinic.controller.PatientViewController;
import theclinic.controller.SystemDefinition;
import theclinic.controller.ViewController;
import theclinic.controller.LoginViewController;
import theclinic.controller.DesktopViewController;
import theclinic.view.View;
import theclinic.view.views.modal_views.ModalView;
import theclinic.view.views.non_modal_views.DesktopView;
import theclinic.view.views.non_modal_views.PatientView;
import theclinic.view.views.non_modal_views.UserLoginView;
import com.bric.colorpicker.ColorPickerDialog;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;


/**
 *
 * @author colin
 */
public class UserSystemWideSettingsView extends View 
        implements ActionListener, PropertyChangeListener{

    /**
     * 
     * @param myViewType
     * @param myController
     * @param desktopView 
     */
    public UserSystemWideSettingsView(View.Viewer myViewType,
                                      ViewController myController,
                                      DesktopView desktopView) {
        setMyViewType(myViewType);
        setMyController(myController);
        setDesktopView(desktopView);
    }
    
    @Override
    public void actionPerformed (ActionEvent e){
        Action action = Action.valueOf(e.getActionCommand());
        switch(action){
            case REQUEST_CLOSE_VIEW ->{
                try{
                    this.setClosed(true);   
                }catch (PropertyVetoException ex){
                    
                }
                break;
            }
            case REQUEST_FACTORY_SETTINGS ->{
                sendActionRequestToController(
                        UserSystemWideSettingsViewController.Actions.USER_SYSTEM_WIDE_FACTORY_SETTINGS_REQUEST);
                copySettingsFromControllerDescriptionToViewDescription();
                copyViewDescriptionToViewSettings();
                break;
            }
            case REQUEST_UPDATE_SETTINGS ->{
                copyCurrentViewSettingsToViewDescription();
                sendActionRequestToController(
                        UserSystemWideSettingsViewController.Actions.USER_SYSTEM_WIDE_SETTINGS_UPDATE_REQUEST);
                copySettingsFromControllerDescriptionToViewDescription();
                copyViewDescriptionToViewSettings();
                
                break;
            }
            case REQUEST_FONT_COLOR_CHANGE,
                 REQUEST_FONT_CHANGE,
                 REQUEST_FONT_SIZE_CHANGE ->{
                setSettingFor(action);
                break;
            }
        }
    }
    
    @Override
    public void initialiseView(){
        initComponents();
        setVisible(true);
        String[] fontNames = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        Integer[] fontSizes = {10, 11, 12, 13};
        cmbPanelTitleFontSetting.setModel(new javax.swing.DefaultComboBoxModel<>(fontNames));
        //Font font = (Font)this.getMyController().getDescriptor().getControllerDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT);
       // Font font = new Font("Segoe UI", 1, 12);
       // this.cmbPanelTitleFontSetting.setSelectedItem(font.getName());
        //javax.swing.ComboBoxModel<String> model = cmbPanelTitleFontSetting.getModel();
        //this.cmbPanelTitleSizeSetting.setSelectedItem(font.getSize());
        setControllerDescription(getMyController().getDescriptor().getControllerDescription());
        setViewDescription(getMyController().getDescriptor().getViewDescription());
        ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                DesktopViewController.Actions.USER_SYSTEM_WIDE_SETTINGS_REQUEST.toString());
        this.getMyController().getMyController().actionPerformed(actionEvent);
        setScheduleTitledBorderSettings();
        addInternalFrameListeners();
        addActionListeners();
        
        copySettingsFromControllerDescriptionToViewDescription();
        copyViewDescriptionToViewSettings();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e){
        DesktopViewController.Properties propertyName = 
                DesktopViewController.Properties.valueOf(e.getPropertyName());
        switch(propertyName){
            case USER_SYSTEM_WIDE_SETTINGS_RECEIVED ->{
                this.setScheduleTitledBorderSettings();
                break;
            }
        }
    }
    
    private void setScheduleTitledBorderSettings(){
        setBorderTitles(BorderTitles.ACTIONS);
        setBorderTitles(BorderTitles.FONT_SETTINGS);
    }
    
    private String panelActionsCaption = "Actions";
    private String panelScheduleDiaryBackgroundColorCaption = "Panel title font & colour settings";
    private void setBorderTitles(BorderTitles borderTitles){
        JPanel panel = null;
        String caption = null;
        boolean isPanelBackgroundDefault = false;
        switch (borderTitles){
            case FONT_SETTINGS ->{
                panel = this.pnlScheduleDiaryBackgroundColor;
                caption = panelScheduleDiaryBackgroundColorCaption;
                isPanelBackgroundDefault = false;
                break;
            }
            case ACTIONS ->{
                panel = this.pnlActions;
                caption = panelActionsCaption;
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
    
    private InternalFrameAdapter internalFrameAdapter = null;
    private void addInternalFrameListeners(){
        /**
         * Establish an InternalFrameListener for when the view is closed 
         */
        internalFrameAdapter = new InternalFrameAdapter(){
            @Override  
            public void internalFrameClosed(InternalFrameEvent e) {
                /**
                 * NO ACTION REQUIRED; UPDATED CODE AS FOLLOWS
                 * -- on close view request the VIEW_CLOSE_NOTIFICATION is sent to the controller immediately
                 * -- the controller will send the view.setClosed(true) message to close the view before closing the view controller
                 */
                
                ActionEvent actionEvent = new ActionEvent(
                        UserSystemWideSettingsView.this,ActionEvent.ACTION_PERFORMED,
                        LoginViewController.Actions.VIEW_CLOSED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
                
            }
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                ActionEvent actionEvent = new ActionEvent(
                        UserSystemWideSettingsView.this,ActionEvent.ACTION_PERFORMED,
                        PatientViewController.Actions.VIEW_ACTIVATED_NOTIFICATION.toString());
                getMyController().actionPerformed(actionEvent);
            }
        };
        this.addInternalFrameListener(internalFrameAdapter);
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    }
    
    private void addActionListeners(){
        this.btnCloseView.setActionCommand(Action.REQUEST_CLOSE_VIEW.toString());
        this.btnFactorySettings.setActionCommand(Action.REQUEST_FACTORY_SETTINGS.toString());
        this.btnFontColorSetting.setActionCommand(Action.REQUEST_FONT_COLOR_CHANGE.toString());
        this.btnFontNameSetting.setActionCommand(Action.REQUEST_FONT_CHANGE.toString());
        this.btnFontSizeSetting.setActionCommand(Action.REQUEST_FONT_SIZE_CHANGE.toString());
        this.btnUpdateSettings.setActionCommand(Action.REQUEST_UPDATE_SETTINGS.toString());
        this.btnCloseView.addActionListener(this);
        this.btnFactorySettings.addActionListener(this);
        this.btnFontColorSetting.addActionListener(this);
        this.btnFontNameSetting.addActionListener(this);
        this.btnFontSizeSetting.addActionListener(this);
        this.btnUpdateSettings.addActionListener(this);
        
    }
    
    private void sendActionRequestToController(
            UserSystemWideSettingsViewController.Actions request){
        ActionEvent actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            request.toString());
        this.getMyController().actionPerformed(actionEvent);
        actionEvent = new ActionEvent(
            this,ActionEvent.ACTION_PERFORMED,
            UserSystemWideSettingsViewController.Actions.VIEW_CHANGED_NOTIFICATION.toString());
        this.getMyController().actionPerformed(actionEvent);
    }
    
    private void copySettingsFromControllerDescriptionToViewDescription(){
        getViewDescription().setProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR,
                getControllerDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR));
        getViewDescription().setProperty(SystemDefinition.Properties.TITLED_BORDER_FONT,
                getControllerDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT));
    }
    
    private void copyViewDescriptionToViewSettings(){
        this.setFontColor((Color)getViewDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR));
        this.setSelectedFont((Font)getViewDescription().getProperty(SystemDefinition.Properties.TITLED_BORDER_FONT));
    }
    
    private void copyCurrentViewSettingsToViewDescription(){
        getViewDescription().setProperty(SystemDefinition.Properties.TITLED_BORDER_COLOR, getFontColor());
        getViewDescription().setProperty(SystemDefinition.Properties.TITLED_BORDER_FONT, 
                new Font((String)this.cmbPanelTitleFontSetting.getSelectedItem(), 
                        Font.BOLD, (int)this.cmbPanelTitleSizeSetting.getSelectedItem()));
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
            case REQUEST_FONT_COLOR_CHANGE ->{
                Color initialColor = this.getFontColor();
                this.setFontColor(ColorPickerDialog.showDialog(null, initialColor, true));
                break;
            }
            case REQUEST_FONT_CHANGE ->{
                Integer fontSize = null;
                String fontName = null;
                fontName = (String)this.cmbPanelTitleFontSetting.getSelectedItem();
                fontSize = (Integer)this.cmbPanelTitleSizeSetting.getSelectedItem();
                setSelectedFont(new Font(fontName, Font.BOLD, fontSize));
                break;
            }
        }
    }

    private Color titleBorderColor = null;
    private void setFontColor(Color value){
        titleBorderColor = value;
        this.lblFontColor.setOpaque(true);
        this.lblFontColor.setBackground(titleBorderColor);
    }
    private Color getFontColor(){
        return titleBorderColor;
    }
    
    private Font selectedFont = null;
    private void setSelectedFont(Font value){
        selectedFont = value;
        this.cmbPanelTitleFontSetting.setSelectedItem(selectedFont.getFamily());
        this.cmbPanelTitleSizeSetting.setSelectedItem(selectedFont.getSize());
    }
    private Font getSelectedFont(){
        return selectedFont;
    }

    enum Action{
        REQUEST_CLOSE_VIEW,
        REQUEST_FACTORY_SETTINGS,
        REQUEST_FONT_COLOR_CHANGE,
        REQUEST_FONT_CHANGE,
        REQUEST_FONT_SIZE_CHANGE,
        REQUEST_UPDATE_SETTINGS,
    }
    
    enum BorderTitles{
        ACTIONS,
        FONT_SETTINGS
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlScheduleDiaryBackgroundColor = new javax.swing.JPanel();
        btnFontColorSetting = new javax.swing.JButton();
        btnFontSizeSetting = new javax.swing.JButton();
        btnFontNameSetting = new javax.swing.JButton();
        lblFontColor = new javax.swing.JLabel();
        cmbPanelTitleFontSetting = new javax.swing.JComboBox<>();
        cmbPanelTitleSizeSetting = new javax.swing.JComboBox<>();
        pnlActions = new javax.swing.JPanel();
        btnUpdateSettings = new javax.swing.JButton();
        btnFactorySettings = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        setTitle("System wide settings");

        pnlScheduleDiaryBackgroundColor.setBorder(javax.swing.BorderFactory.createTitledBorder("Panel title font & colour settings"));

        btnFontColorSetting.setText("Colour");

        btnFontSizeSetting.setText("Size");

        btnFontNameSetting.setText("Font");

        lblFontColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        cmbPanelTitleFontSetting.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "String[]" }));
        String[] fontNames = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getAvailableFontFamilyNames();
        cmbPanelTitleFontSetting = new JComboBox<>(fontNames);

        cmbPanelTitleSizeSetting.setModel(new javax.swing.DefaultComboBoxModel<>(new Integer[] { 10, 11, 12, 13, 14, 15, 16 }));

        javax.swing.GroupLayout pnlScheduleDiaryBackgroundColorLayout = new javax.swing.GroupLayout(pnlScheduleDiaryBackgroundColor);
        pnlScheduleDiaryBackgroundColor.setLayout(pnlScheduleDiaryBackgroundColorLayout);
        pnlScheduleDiaryBackgroundColorLayout.setHorizontalGroup(
            pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDiaryBackgroundColorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlScheduleDiaryBackgroundColorLayout.createSequentialGroup()
                        .addComponent(btnFontColorSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(lblFontColor, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlScheduleDiaryBackgroundColorLayout.createSequentialGroup()
                        .addComponent(btnFontSizeSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(cmbPanelTitleSizeSetting, 0, 79, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlScheduleDiaryBackgroundColorLayout.createSequentialGroup()
                        .addComponent(btnFontNameSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(cmbPanelTitleFontSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlScheduleDiaryBackgroundColorLayout.setVerticalGroup(
            pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlScheduleDiaryBackgroundColorLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFontColorSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFontColor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFontSizeSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPanelTitleSizeSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(pnlScheduleDiaryBackgroundColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFontNameSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbPanelTitleFontSetting, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnUpdateSettings.setText("<html><center>Update</center><center>settings</center></html>");
        btnUpdateSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateSettingsActionPerformed(evt);
            }
        });

        btnFactorySettings.setText("<html><center>Factory</center><center>settings</center></html>");
        btnFactorySettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFactorySettingsActionPerformed(evt);
            }
        });

        btnCloseView.setText("<html><center>Close</center><center>view</center></html>");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlActionsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnUpdateSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlActionsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnUpdateSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFactorySettings, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlScheduleDiaryBackgroundColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlScheduleDiaryBackgroundColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFactorySettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFactorySettingsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFactorySettingsActionPerformed

    private void btnUpdateSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateSettingsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateSettingsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseView;
    private javax.swing.JButton btnFactorySettings;
    private javax.swing.JButton btnFontColorSetting;
    private javax.swing.JButton btnFontNameSetting;
    private javax.swing.JButton btnFontSizeSetting;
    private javax.swing.JButton btnUpdateSettings;
    private javax.swing.JComboBox<String> cmbPanelTitleFontSetting;
    private javax.swing.JComboBox<Integer> cmbPanelTitleSizeSetting;
    private javax.swing.JLabel lblFontColor;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlScheduleDiaryBackgroundColor;
    // End of variables declaration//GEN-END:variables
}
