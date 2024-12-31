/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package view.views.dialog_views;

import view.View;
import view.support_classes.renderers.ComboboxLocalDateTimeRenderer;
import view.support_classes.renderers.ComboboxScheduleDiaryActionRenderer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.TitledBorder;
import model.non_entity.SystemDefinition;

/**
 * 
 * @author colin
 * @param <T> 
 */
public class DialogUsingGenericSelector<T> extends DialogView implements ActionListener {

    /**
     * 
     * @param myViewType
     * @param myParentView
     * @param list
     * @param _dialogTitle
     * @param _selectorTitle 
     */
    public DialogUsingGenericSelector(
            View.Viewer myViewType,
            View myParentView,
            List<T> list,
            String _dialogTitle,
            String _selectorTitle) {
        setMyViewType(myViewType);
        setMyParentView(myParentView);
        items = list;
        dialogTitle = _dialogTitle;
        selectorTitle =  _selectorTitle;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Action actionCommand = Action.valueOf(e.getActionCommand());
        switch(actionCommand){
            case REQUEST_CANCEL:
                this.setSelectedItem(null);
                dispose();
                break;
            case REQUEST_SELECTED_ITEM:
                if (cmbSelector.getSelectedIndex() == -1){
                        setSelectedItem(null);
                }
                else {
                    setSelectedItem((T)cmbSelector.getSelectedItem());
                }
                break;
            case REQUEST_SUBMIT:
                dispose(); 
        }
    }

    @Override
    public void initialiseView(){
        initComponents();
        DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>();
        //model.addElement(null);
        for (T item : getItems()) {
            model.addElement(item);
        }
        cmbSelector.setModel(model); // Update the model with the populated one
        javax.swing.SwingUtilities.invokeLater(() -> { 
            cmbSelector.setSelectedItem(null);
            cmbSelector.repaint();
        });
        
        switch(getMyViewType()){
            case EARLY_BOOKING_START_EDITOR_DIALOG:
            case LATE_BOOKING_END_EDITOR_DIALOG:
                cmbSelector.setRenderer(new ComboboxLocalDateTimeRenderer());
                break;
            case EXTEND_SHIFT_BOOKING_DIALOG:
                cmbSelector.setRenderer(new ComboboxScheduleDiaryActionRenderer());
                break;
            default:  
                //cmbSelector.setRenderer(new ComboboxPatientRenderer());
                break;
        }
        
        //cmbSelector = new JComboBox<>(getItems().toArray((T[]) new Object[0]));
        //cmbSelector = new JComboBox<>(items.stream().toArray(size -> (T[]) new Object[size]));
        setTitle(getDialogTitle());
        
        pnlSelector.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Select item from list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder(
        javax.swing.BorderFactory.createEtchedBorder(), 
        "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
        SystemDefinition.TITLED_BORDER_FONT, 
        SystemDefinition.TITLED_BORDER_COLOR));// NOI18N
        
        TitledBorder titledBorder = (TitledBorder)pnlSelector.getBorder();
        titledBorder.setTitle(getSelectorTitle());
        //pnlScheduleForDay.repaint();
        
        btnCancel.setActionCommand(Action.REQUEST_CANCEL.toString());
        btnCancel.addActionListener(this);
        btnSubmit.setActionCommand(Action.REQUEST_SUBMIT.toString());
        btnSubmit.addActionListener(this);
        
        this.cmbSelector.setActionCommand(Action.REQUEST_SELECTED_ITEM.toString());
        cmbSelector.addActionListener(this);
    }
    
    private String dialogTitle = null;
    private String getDialogTitle(){
        return dialogTitle;
    }
    private void setDialogTitle(String value){
        dialogTitle = value;
    }
    
    
    
    private List<T> items = null;
    private List<T> getItems(){
        return items;
    }
    private void setItems(List<T> value){
        items = value;
    }
    
    private String selectorTitle = null;
    private String getSelectorTitle(){
        return selectorTitle;
    }
    private void setSelectorTitle(String value){
        selectorTitle = value;
    }
    
    enum Action{
        REQUEST_SUBMIT,
        REQUEST_SELECTED_ITEM,
        REQUEST_CANCEL
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSelector = new javax.swing.JPanel();
        cmbSelector = new javax.swing.JComboBox<T>();
        pnlActions = new javax.swing.JPanel();
        btnSubmit = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        pnlSelector.setBorder(javax.swing.BorderFactory.createTitledBorder("Select item from list"));

        cmbSelector.setModel(new javax.swing.DefaultComboBoxModel<T>());

        javax.swing.GroupLayout pnlSelectorLayout = new javax.swing.GroupLayout(pnlSelector);
        pnlSelector.setLayout(pnlSelectorLayout);
        pnlSelectorLayout.setHorizontalGroup(
            pnlSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectorLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(cmbSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        pnlSelectorLayout.setVerticalGroup(
            pnlSelectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSelectorLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(cmbSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlActions.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        btnSubmit.setText("Submit");

        btnCancel.setText("Cancel");

        javax.swing.GroupLayout pnlActionsLayout = new javax.swing.GroupLayout(pnlActions);
        pnlActions.setLayout(pnlActionsLayout);
        pnlActionsLayout.setHorizontalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSubmit)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlActionsLayout.setVerticalGroup(
            pnlActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSelector, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<T> cmbSelector;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlSelector;
    // End of variables declaration//GEN-END:variables
}
