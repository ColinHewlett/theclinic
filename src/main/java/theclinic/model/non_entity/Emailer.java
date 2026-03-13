/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.non_entity;

import theclinic.model.exceptions.EmailException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author colin
 */
public class Emailer {

    public void send(String fromEmail, String toEmail, String smtpHost, Integer smtpPort, String emailMessage )throws EmailException{
        fetchEmailPassword();

         // Email config

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.getSMTPHost());
        props.put("mail.smtp.port", this.getSMTPPort());

        Session session = Session.getInstance(props,
            new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(getFromEmail(), getEmailPassword());
                }
            });
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(getFromEmail()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getToEmail()));
            message.setSubject("Secure Email");
            message.setText("This email was sent using an encrypted password stored in a KeyStore.");
        }catch(MessagingException ex){
            String msg = ex.getMessage() +"\n"
                    + "EmailException raised in Emailer::send() method";
            throw new EmailException(msg, EmailException.ExceptionType.EMAIL_MESSAGING_EXCEPTION);
        }

    }
    
    private void fetchEmailPassword()throws EmailException{
        try{
            // Load keystore
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(Files.newInputStream(new File("keystore.jks").toPath()), "keystorepass".toCharArray());

        // Retrieve AES key
        
            KeyStore.ProtectionParameter entryPassword =
                    new KeyStore.PasswordProtection("entrypass".toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("aesKeyAlias", entryPassword);
            SecretKey secretKey = secretKeyEntry.getSecretKey();

            // Decrypt password
            byte[] encrypted = Files.readAllBytes(new File("encrypted-password.bin").toPath());
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setEmailPassword(new String(cipher.doFinal(encrypted))); 
        }catch( BadPaddingException |
                CertificateException |
                IOException |
                KeyStoreException |
                NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                IllegalBlockSizeException |
                UnrecoverableEntryException ex){
            String msg = ex.getMessage() + "\n"
                    + "EmailPasswordDecryptionException raised in Emaile::fetchEmailPassword()";
            throw new EmailException(msg,EmailException.ExceptionType.EMAIL_PASSWORD_DECRYPTION_ERROR);          
        }
    }
    
    private String emailPassword = null;
    private void setEmailPassword(String value){
        emailPassword = value;
    }
    private String getEmailPassword(){
        return emailPassword;
    }
    
    private String fromEmail = null;
    private void setFromEmail(String value){
        fromEmail = value;
    }
    private String getFromEmail(){
        return fromEmail;
    }
    
    private String toEmail = null;
    private void setToEmail(String value){
        toEmail = value;
    }
    private String getToEmail(){
        return toEmail;
    }
    
    private String smtpHost = null;
    private void setSMTPHost(String value){
        smtpHost = value;
    }
    private String getSMTPHost(){
        return smtpHost;
    }
    
    private String emailEmailMessage = null;
    private void setEmailMessage(String value){
        emailEmailMessage = value;
    }
    private String getEmailMessage(){
        return emailEmailMessage;
    }
    
    private Integer smtpPort = null;
    private void setSMTPPort(Integer value){
        smtpPort = value;
    }
    private Integer getSMTPPort(){
        return smtpPort;
    }
}
