/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import static controller.ViewController.displayErrorMessage;
import controller.exceptions.TemplateReaderException;
import view.views.non_modal_views.DesktopView;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.math.BigInteger;
import java.util.List;
import javax.swing.JOptionPane;
import model.*;
import repository.StoreException;
import view.View;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
/**
 *
 * @author colin
 */
public class PatientMedicalHistoryViewController extends ViewController{
    
    public PatientMedicalHistoryViewController(DesktopViewController controller,
                               DesktopView desktopView){
        setMyController(controller);
        setDesktopView(desktopView);  
    }
    
    public void propertyChange(PropertyChangeEvent e){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        PatientPrimaryCondition ppc = null;
        PatientSecondaryCondition psc = null;
        //PatientSecondaryCondition psc = new PatientSecondaryCondition(pac.getPatient(),new SecondaryCondition());;
        //Condition condition = null;
        boolean isError = false;
        boolean isViewClosed = false;
        String error = null;
        ConditionWithState conditionWithState = null;
        PatientCondition pac = null;
        Patient patient = getDescriptor()
                .getControllerDescription().getPatient();
        ConditionWithState CWS = getDescriptor()
                .getViewDescription().getConditionWithState();
        

        //appointmentTreatment.setScope(Entity.Scope.SINGLE);
        ViewController.PatientMedicalHistoryViewControllerActionEvent actionCommand =
            ViewController.PatientMedicalHistoryViewControllerActionEvent
                    .valueOf(e.getActionCommand());
        try{
            switch (actionCommand){
                case PRINT_PATIENT_MEDICAL_HISTORY_REQUEST:
                    doPrintPatientMedicalHistoryRequest();
                    break;
                case VIEW_CLOSE_NOTIFICATION:
                    ActionEvent actionEvent = new ActionEvent(
                        this,ActionEvent.ACTION_PERFORMED,
                        ViewController.DesktopViewControllerActionEvent.
                                VIEW_CONTROLLER_CLOSE_NOTIFICATION.toString());
                    getMyController().actionPerformed(actionEvent);
                    isViewClosed = true;
                    break;
                case PATIENT_CONDITION_COMMENT_UPDATE_REQUEST:
                    if (CWS!=null){
                        pac = new PatientCondition();
                        pac.setPatient(patient);
                        pac.setCondition(CWS.getCondition());
                        pac.update();
                        CWS = getConditionWithState(pac);
                    }else{
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Action to update condition comment aborted";
                    }
                    break;
                case PATIENT_CONDITION_READ_REQUEST:
                    if (CWS==null) {
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Patient condition read requewst aborted";
                    }else{
                        if(CWS.getCondition().getIsPrimaryCondition()){
                            ppc = new PatientPrimaryCondition(patient, (PrimaryCondition)CWS.getCondition());
                            conditionWithState = getConditionWithState(ppc);
                        }else if(CWS.getCondition().getIsSecondaryCondition()){
                            psc = new PatientSecondaryCondition(patient, (SecondaryCondition)CWS.getCondition());
                            conditionWithState = getConditionWithState(psc);
                        }else{
                            isError = true;
                            error = "The view's patient condition has not been properly defined/n"
                                    + "Patient secondary condition read request aborted";
                        }
                    }

                    break;
                case PATIENT_CONDITION_CREATE_REQUEST:
                    if (CWS==null) {
                        isError = true;
                        error = "A condition with state has not been defined;/n"
                                + "Action to add to patient conditions aborted";
                    }else{
                        if (CWS.getCondition().getIsPrimaryCondition())
                            pac = new PatientPrimaryCondition(
                                    patient,(PrimaryCondition)CWS.getCondition());
                        else if(CWS.getCondition().getIsSecondaryCondition())
                            pac = new PatientSecondaryCondition(
                                    patient,(SecondaryCondition)CWS.getCondition());
                        else{
                            isError = true;
                            error = "Patient condition has not been defined properly (primary or secondary?)/n"
                                    + "Action to add to patient conditions aborted";
                        }
                        pac.insert();
                        
                        synchPrimaryConditionStateWithItsSecondaries(pac);
                        conditionWithState = getConditionWithState(pac);
                    }
                    break;
                case PATIENT_CONDITION_DELETE_REQUEST:
                    isError = false;
                    if (CWS==null) {
                        isError = true;
                        error = "Patient condition has not been defined;/n"
                                + "Action to remove a patient condition aborted";
                    }else{
                        if (CWS.getCondition().getIsPrimaryCondition())
                            pac = new PatientPrimaryCondition(
                                    patient,(PrimaryCondition)CWS.getCondition());
                        else if(CWS.getCondition().getIsSecondaryCondition())
                            pac = new PatientSecondaryCondition(
                                    patient,(SecondaryCondition)CWS.getCondition());
                        else{
                            isError = true;
                            error = "Patient condition has not been defined properly (primary or secondary?)/n"
                                    + "Action to add to delete patient condition aborted";
                        }
                        if (!isError){
                            pac.setScope(Entity.Scope.SINGLE);
                            pac.delete();

                            synchPrimaryConditionStateWithItsSecondaries(pac);
                            conditionWithState = getConditionWithState(pac);
                        }
                    }
                    break;
            }
        }catch(StoreException ex){
            String message = ex.getMessage() + "\n"
                    + "Exception raised in "
                    + "PatientMedicalHistoryViewController.actionPerformed"
                    + "(" + actionCommand + ")";
            displayErrorMessage(message, "View controller error", 
                    JOptionPane.WARNING_MESSAGE);
            isError = true;
        }
        if((!actionCommand.equals(
                PatientMedicalHistoryViewControllerActionEvent.VIEW_CLOSE_NOTIFICATION))&&
                (!actionCommand.equals(
                PatientMedicalHistoryViewControllerActionEvent.PRINT_PATIENT_MEDICAL_HISTORY_REQUEST))){//handles case idf VC is about to close 
            if (!isError){
                if (error == null){ // ensures upstream StoreException error is not handled again
                    getDescriptor().getControllerDescription()
                            .setConditionWithState(conditionWithState);
                    firePropertyChangeEvent(
                            ViewController.PatientMedicalHistoryViewControllerPropertyChangeEvent.
                                    CONDITION_WITH_STATE_RECEIVED.toString(),
                            (View)e.getSource(),
                            this,
                            null,
                            null
                    );
                    firePropertyChangeEvent(
                            ViewController.DesktopViewControllerPropertyChangeEvent.
                                    PATIENT_MEDICAL_HISTORY_VIEW_CONTROLLER_CHANGE_NOTIFICATION.toString(),
                            (DesktopViewController)getMyController(),
                            this,
                            null,
                            getDescriptor()
                    );
                }else{//error != null; view controller error message on its way
                    getDescriptor().getControllerDescription().setError(error);
                }
            }
        }
    }
    
    private XWPFDocument setMargins(XWPFDocument document, int pips){
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            
        // Create a new page margin
        CTPageMar pageMar = sectPr.addNewPgMar();

        // Set the margins (values are in twentieths of a point, so 1440 = 1 inch)
        pageMar.setLeft(576);   // 1 inch
        pageMar.setRight(576);  // 1 inch
        //pageMar.setTop(1440);    // 1 inch
        //pageMar.setBottom(1440); // 1 inch
        return document;
    }
    
    private XWPFTable setHeaderRowHeight(XWPFTable table, int row, int pips){
        XWPFTableCell cell = null;
        cell = table.getRow(row).getCell(0);
        // Get the table cell properties
        CTTc ctTc = cell.getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();

        // Set the height of the cell (in twentieths of a point, 1 point = 20 twentieths)
        CTTblWidth cellHeight = ctTcPr.addNewTcW();
        cellHeight.setType(STTblWidth.DXA);
        cellHeight.setW(BigInteger.valueOf(pips)); // Example height (50 points)
        return table;
    }
    
    private XWPFDocument setDefaultFontAndSize(XWPFDocument document, String fontFamily, int fontSize) {
        // Create the fonts settings
        XWPFStyles styles = document.createStyles();
        CTHpsMeasure ctSize = null;
        
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii(fontFamily);
        fonts.setHAnsi(fontFamily);
        fonts.setCs(fontFamily);
        fonts.setEastAsia(fontFamily);
        
        styles.setDefaultFonts(fonts);
        
        try{
            for (CTFonts font : document.getStyle().getDocDefaults().getRPrDefault().getRPr().getRFontsArray()){
                System.out.println(font.getAscii());
                System.out.println(font.getHAnsi());
                System.out.println(font.getCs());
                System.out.println(font.getEastAsia());
            }
        }catch(XmlException ex){
            System.out.println(ex.getMessage() + "\n"
                    + "XmlException");
        }catch(IOException ex){
            System.out.println("IOException");
        }
            

        BigInteger bint = new BigInteger(Integer.toString(fontSize));
       // ctSize = bint.multiply(new BigInteger("2"));

        // Apply the font settings to the styles
        //document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
        //document.getStyle().getDocDefaults().getRPrDefault().getRPr().setSzArray(0,ctSize /** 2*/);
        //document.getStyle().getDocDefaults().getRPrDefault().getRPr().setSzCsArray(0,ctSize /** 2*/);
        
        //for ()
        return document;
    }
    
    private void setTextInCell(XWPFTableCell cell, String text){
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setText(text);

        // Set the font
        run.setFontFamily("Arial");
        run.setFontSize(10);
        verticallyAlignTextInCell(cell);
    }

    private void shadeCellOnEvenRows(XWPFTable table, int row, int column){
        XWPFTableCell cell = null;
        if(row % 2 == 0) {
            cell = table.getRow(row).getCell(column);
            cell.setColor("F2F2F2");
        }                ;
    }
    
    private void doPrintPatientMedicalHistoryRequest(){
        List<XWPFTableRow> rowList = null;
        XWPFDocument document = null;
        XWPFParagraph paragraph = null;
        XWPFTableRow row = null;
        XWPFRun run = null;
        XWPFTable table;
        CTTblWidth tableWidth = null;
        CTR ctr = null;
        CTTc ctTc = null;
        XWPFTableCell cell = null;
        int tableColumnCount = 6;
        int tableRowCount = 0;
        boolean debug = false;
        InputStream fis = getClass().getResourceAsStream("/PatientMedicalHistory.docx");
        try{
            document = new XWPFDocument(fis);
            table = document.getTableArray(0);
            tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            tableWidth.setW(BigInteger.valueOf(10600)); // 8000 in Twips (1/20 of a point)
            table = document.getTableArray(1);
            tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            tableWidth.setW(BigInteger.valueOf(10600));
            table = document.getTableArray(2);
            tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            tableWidth.setW(BigInteger.valueOf(10600));
            
            int pcRowCount = 0;
            fetchMedicalConditionsOnSystem();
            PrimaryCondition pc = getDescriptor()
                    .getControllerDescription().getPrimaryCondition();
            boolean isLastEntryPrimaryCondition = false;
            boolean isLastEntrySecondaryCondition = false;
            int rowCount = 0;
            pc = getDescriptor()
                    .getControllerDescription().getPrimaryCondition();
            for (Condition c : pc.get()){
                PrimaryCondition _pc = (PrimaryCondition)c;
                if (rowCount > 0) {
                    rowCount++;
                    table.createRow();
                    pcRowCount = rowCount;
                }
                cell = table.getRow(rowCount).getCell(0);
                setTextInCell(cell,c.getDescription());
                //cell.setText(c.getDescription());
                isLastEntryPrimaryCondition = true;
                if (!_pc.getSecondaryCondition().get().isEmpty()){
                    for(Condition _c : _pc.getSecondaryCondition().get()){
                        if (isLastEntryPrimaryCondition) {
                            cell = table.getRow(rowCount).getCell(1);
                            isLastEntryPrimaryCondition = false;
                        }else {
                            table.createRow();
                            cell = table.getRow(++rowCount).getCell(1);
                        }
                        setTextInCell(cell, _c.getDescription());
                        shadeCellOnEvenRows(table, rowCount, 1);
                        shadeCellOnEvenRows(table, rowCount, 2);
                        shadeCellOnEvenRows(table, rowCount, 3);
                        isLastEntrySecondaryCondition = true;
                    }
                    if (isLastEntrySecondaryCondition){
                        table.createRow();
                        cell = table.getRow(++rowCount).getCell(1);
                        setTextInCell(cell, "Other ...");
                        shadeCellOnEvenRows(table, rowCount, 1);
                        shadeCellOnEvenRows(table, rowCount, 2);
                        shadeCellOnEvenRows(table, rowCount, 3);
                        isLastEntrySecondaryCondition = false;
                        mergeCellsVertically(table, 0, pcRowCount, rowCount);
                        verticallyAlignTextInCell(table.getRow(pcRowCount).getCell(0));
                    }
                }else{
                    mergeCellsHorizontally(table, pcRowCount, 0, 1);
                    shadeCellOnEvenRows(table, pcRowCount, 0);
                    shadeCellOnEvenRows(table, pcRowCount, 2);
                    shadeCellOnEvenRows(table, pcRowCount, 3);
                }
            }
            table.createRow();
            cell = table.getRow(++rowCount).getCell(0);
            table.getRow(rowCount).setHeight(500);
            setTextInCell(cell, "Other ...");
            shadeCellOnEvenRows(table, rowCount, 0);
            mergeCellsHorizontally(table,rowCount,0 , 3);
            //XWPFTableRow newRow = table.createRow();
            
        //XWPFDocument document = new XWPFDocument();
        //document = setMargins(document,576);
        //document = setDefaultFontAndSize(document, "Ariel", 10);
        //XWPFTable table = createBasicTableGrid(document,tableColumnCount );
        //List<XWPFTableRow> rowList = table.getRows();
        //row = rowList.get(0);
        //List<XWPFTableCell> cellList =row.getTableCells();

        // Set table width
            //CTTblWidth tableWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            //tableWidth.setW(BigInteger.valueOf(10400)); // 8000 in Twips (1/20 of a point)
        /*
        row = table.getRow(0);
        row.setHeight(100);
        */
     
        //set header row height

            
            //mergeCellsHorizontally(table, 0, 0, 1);
            //mergeCellsHorizontally(table, 0, 2, 3);
            //mergeCellsHorizontally(table, 0, 4, 5);
            
            FileOutputStream out = new FileOutputStream("PatientMedicalHistory.docx");
            document.write(out);
            System.out.println(new File(".").getAbsolutePath());
            System.out.println("Word document with complex table created successfully!");
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        /*
        for (XWPFTableRow _row : table.getRows()) {
            for (XWPFTableCell _cell : _row.getTableCells()) {
                // Get the cell's CTTc (low-level XML representation)
                ctTc = _cell.getCTTc();
                // Add or get the paragraph inside the cell
                paragraph = _cell.getParagraphArray(0);
                if (paragraph == null) {
                    paragraph = _cell.addParagraph();
                }
                //run = paragraph.createRun();
                //ctr = run.getCTR();
                //run.setFontFamily("Arial");
                //run.setFontSize(10);
            }
        }
        cell = table.getRow(0).getCell(0);
        //run.setText("Medical Conditions");
        ctTc = cell.getCTTc();
        paragraph = cell.getParagraphArray(0);
        run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setText("Medical Condition");
        //run = paragraph.getRun(ctr);
        // Set horizontal alignment (center, right, both, left)
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Horizontal center alignment
        // Set vertical alignment (top, center, bottom)
        cell.setVerticalAlignment(XWPFVertAlign.CENTER); // Vertical center alignment
        
        cell = table.getRow(0).getCell(4);
        paragraph = cell.getParagraphArray(0);
        run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setText("Comments");
        ctTc = cell.getCTTc();
        // Set horizontal alignment (center, right, both, left)
        paragraph.setAlignment(ParagraphAlignment.CENTER); // Horizontal center alignment
        // Set vertical alignment (top, center, bottom)
        cell.setVerticalAlignment(XWPFVertAlign.CENTER); // Vertical center alignment
        */
        // Merge cells in the first row for a header
            
            //cell = table.getRow(0).getCell(0);
        
        /*
        for (int index = 0; index < 6; index++){
            cell = table.getRow(0).getCell(index);
            setCellProperties(cell, "FFFFFF", "0000FF", true);
        }
        */

        /*
        for (int row = 1; row < tableRowCount; row++) {
            for (int col = 0; col < 6; col++) {
                XWPFTableCell dataCell = table.getRow(row).getCell(col);
                dataCell.setText("Row " + row + ", Col " + col);
                if (row % 2 == 0) {
                    setCellProperties(dataCell, "F2F2F2", "000000", false); // Gray background for even rows
                }
            }
        }
        */
        // Save the document to a file
 
    }
    
    private XWPFTable createBasicTableGrid(XWPFDocument document, int tableColumnCount){
        int rowCount = 0;
        fetchMedicalConditionsOnSystem();
        PrimaryCondition pc = getDescriptor()
                .getControllerDescription().getPrimaryCondition();
        for (Condition c : pc.get()){
            PrimaryCondition _pc = (PrimaryCondition)c;
            if (!_pc.getSecondaryCondition().get().isEmpty())
                rowCount = rowCount +  _pc.getSecondaryCondition().get().size();
            else rowCount++; 
        }
        rowCount++;//includes the header row in the count
        XWPFTable table = document.createTable(rowCount,tableColumnCount);
        return table;
    }
    
    private static void setTextProperties(XWPFTableCell cell, String bgColor, String textColor, boolean isBold){
        cell.setColor(bgColor);

        // Set text properties
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setColor(textColor);
        run.setBold(isBold);
    }
    
    private static void setCellProperties(XWPFTableCell cell, String bgColor, String textColor, boolean isBold) {
        // Set background color
        cell.setColor(bgColor);

        // Set text properties
        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        if (paragraph == null){
            paragraph = cell.addParagraph();
        }
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Arial");
        run.setFontSize(10);
        run.setColor(textColor);
        run.setBold(isBold);
    }
    
    private void verticallyAlignTextInCell(XWPFTableCell cell){
        CTTc ctTc = cell.getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();
        ctTcPr.addNewVAlign().setVal(STVerticalJc.CENTER);
    }
    
    // Method to merge cells vertically
    private static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private static void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableCell cell = table.getRow(row).getCell(fromCol);
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        for (int colIndex = fromCol + 1; colIndex <= toCol; colIndex++) {
            cell = table.getRow(row).getCell(colIndex);
            cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        }
    }

    private static void setColumnWidth(XWPFTable table, int colIndex, int width) {
        for (XWPFTableRow row : table.getRows()) {
            XWPFTableCell cell = row.getCell(colIndex);
            CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
            cellWidth.setW(BigInteger.valueOf(width));
        }
    }
    
    private void fetchMedicalConditionsOnSystem(){
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        try{
            primaryCondition = primaryCondition.read();
            if (primaryCondition.get().isEmpty()){
                primaryCondition = extractMedicalHistoryFromTemplate();
                for(Condition condition : primaryCondition.get()){
                    PrimaryCondition pCondition = (PrimaryCondition)condition;
                    //pCondition.setPatient(patient);
                    Integer pConditionKey = pCondition.insert();
                    if (!pCondition.getSecondaryCondition().get().isEmpty()){
                        for (Condition c : pCondition.getSecondaryCondition().get()){
                            SecondaryCondition sCondition = (SecondaryCondition)c;
                            sCondition.setPrimaryCondition(new PrimaryCondition(pConditionKey));
                            sCondition.insert();
                        }
                    }
                }   
                setExtractedPrimaryConditionFromTemplate(primaryCondition);
            }
            getFatPrimaryConditions();
        }catch(StoreException ex){
            String message = ex.getMessage() ;
            displayErrorMessage(message, 
                    "Medical condition view controller error", 
                    JOptionPane.WARNING_MESSAGE);
 
        }catch (TemplateReaderException ex){

        }   
    }
    
    private void getFatPrimaryConditions()throws StoreException{
        PrimaryCondition primaryCondition = new PrimaryCondition();
        primaryCondition.setScope(Entity.Scope.ALL);
        primaryCondition = primaryCondition.read();
        SecondaryCondition sc = null;
        PrimaryCondition pc = null;
        for(Condition c : primaryCondition.get()){
            pc = (PrimaryCondition)c;
            sc = new SecondaryCondition(pc);
            sc.setScope(Entity.Scope.FOR_PRIMARY_CONDITION);
            sc = sc.read();
            pc.setSecondaryCondition(sc);
        }
        getDescriptor().getControllerDescription()
                .setPrimaryCondition(primaryCondition);
    }
}
