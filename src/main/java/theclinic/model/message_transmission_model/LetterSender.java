/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author colin
 */
public class LetterSender implements ISendService{
    
    @Override
    public void send(RenderedMessage message) throws Exception{
        Path path = Path.of("letters/letter.docx");
        Files.writeString(path, message.getBody());
        Desktop.getDesktop().open(path.toFile());
    }
}
