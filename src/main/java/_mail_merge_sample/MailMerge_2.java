/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _mail_merge_sample;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


/**
 *
 * @author colin
 */
public class MailMerge_2 {
    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("path/to/template.docx");
            XWPFDocument document = new XWPFDocument(fis); 
            // Iterate through paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                // Iterate through runs in each paragraph
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    // Get text from each run
                    String text = run.getText(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
