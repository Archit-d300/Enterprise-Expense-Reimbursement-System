package Notification;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailChannel implements NotificationChannel {

    private final String senderEmail;
    private final String appPassword;

    public EmailChannel(String senderEmail, String appPassword) {
        this.senderEmail = senderEmail;
        this.appPassword = appPassword;
    }

    @Override
    public void sendMessage(String to, String message) {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                senderEmail,
                                appPassword);
                    }
                });

        try {

            Message email = new MimeMessage(session);

            email.setFrom(new InternetAddress(senderEmail));

            email.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to));

            email.setSubject("Expense Reimbursement Notification");

            email.setText(message);

            Transport.send(email);

            System.out.println(
                    "Email successfully sent to: " + to);

        } catch (Exception e) {

            System.out.println(
                    "Failed to send email to: " + to);

            e.printStackTrace();
        }
    }
}