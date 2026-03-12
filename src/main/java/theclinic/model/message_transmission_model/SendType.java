/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package theclinic.model.message_transmission_model;

/**
 *
 * @author colin
 */
public enum SendType {
    EMAIL("recall_email.ftl", "pdf"),
    LETTER("recall_letter.ftl", "pdf"),
    SMS("recall_sms.ftl", "txt");
    
    private final String templateName;
    private final String archiveExtension;
    
    SendType(String templateName, String archiveExtension){
        this.templateName = templateName;
        this.archiveExtension = archiveExtension;
    }
    
    public String getArchiveExtension(){
        return archiveExtension;
    }
    
    public String getTemplateName(){
        return templateName;
    }
}
