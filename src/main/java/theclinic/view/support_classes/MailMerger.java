/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author colin
 */
public class MailMerger {
    private final String recallTemplate = "MasterRecallLetter.docx";

    public MailMerger() {
        boolean bResult;
        long iResult;
        String sResult = null;
        try{
            File file = new File(recallTemplate);
            bResult = file.exists();
            iResult = file.length();
            sResult = file.getAbsolutePath();
            //FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            InputStream fis = getClass().getResourceAsStream("/MasterRecallLetter.docx");
            XWPFDocument document = new XWPFDocument(fis); 
            // Iterate through paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String text = paragraph.getText();
                if (text!=null){
                    System.out.println(text);
                }/*
                // Iterate through runs in each paragraph
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    // Get text from each run
                    String text = run.getText(0);
                    if (text != null) {
                        System.out.println(text);
                    }
                }
*/
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
