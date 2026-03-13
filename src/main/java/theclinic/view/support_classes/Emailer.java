/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.view.support_classes;

/**
 *
 * @author colin
 */
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Emailer {

    public Emailer(String s) {
        // SMTP server configuration
        String smtpServer = "smtp.gmail.com";
        String username = "colin.hewlett.solutions@gmail.com";
        String password = "ch19450907A@";

        // Sender and recipient email addresses
        String senderEmail = "colin.hewlett.solutions@gmail.com";
        String recipientEmail = "colin.hewlett.solutions@gmail.com";

        // Email content
        String subject = "Hello from JavaMail";
        String body = "This is a test email.";

        try {
            // Set the SMTP server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");

            // Create a session with the SMTP server and authenticate
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Create the email message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

