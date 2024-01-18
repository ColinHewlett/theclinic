/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.views.modal_views;

import controller.Descriptor;
import controller.ViewController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.JOptionPane;
import view.View;
import view.views.non_modal_views.DesktopView;

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
        setTitle("Patient phone & email editor");
        setVisible(true);
        addListeners();
                
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
        });
    }
    
    
    private void initComponents() {

        pnlNoteIndex = new javax.swing.JPanel();
        scrNoteIndex = new javax.swing.JScrollPane();
        pnlNotepad = new javax.swing.JPanel();
        scrNotepad = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        btnAddNewNote = new javax.swing.JButton();
        BtnSaveChanges = new javax.swing.JButton();
        btnCloseView = new javax.swing.JButton();

        pnlNoteIndex.setBorder(javax.swing.BorderFactory.createTitledBorder("Note index"));

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

        pnlNotepad.setBorder(javax.swing.BorderFactory.createTitledBorder("Notepad"));

        javax.swing.GroupLayout pnlNotepadLayout = new javax.swing.GroupLayout(pnlNotepad);
        pnlNotepad.setLayout(pnlNotepadLayout);
        pnlNotepadLayout.setHorizontalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotepad)
                .addContainerGap())
        );
        pnlNotepadLayout.setVerticalGroup(
            pnlNotepadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNotepadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrNotepad, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Note actions"));

        btnAddNewNote.setText("Create");

        BtnSaveChanges.setText("Save");

        btnCloseView.setText("Close");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddNewNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnSaveChanges, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCloseView, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnAddNewNote, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(BtnSaveChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCloseView, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

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
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(pnlNotepad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        


    // Variables declaration - do not modify                     
    private javax.swing.JButton BtnSaveChanges;
    private javax.swing.JButton btnAddNewNote;
    private javax.swing.JButton btnCloseView;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel pnlNoteIndex;
    private javax.swing.JPanel pnlNotepad;
    private javax.swing.JScrollPane scrNoteIndex;
    private javax.swing.JScrollPane scrNotepad;
    // End of variables declaration      
}
