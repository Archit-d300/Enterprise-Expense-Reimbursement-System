package notification;

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

    public static NotificationChannel fromEnvironment() {
        String enabled = System.getenv("EXPENSE_SMTP_ENABLED");
        String senderEmail = System.getenv("EXPENSE_SMTP_EMAIL");
        String appPassword = System.getenv("EXPENSE_SMTP_APP_PASSWORD");

        if (!"true".equalsIgnoreCase(enabled)
                || senderEmail == null || senderEmail.isBlank()
                || appPassword == null || appPassword.isBlank()) {
            System.out.println("SMTP email is disabled or not fully configured; notifications will be shown in the console.");
            return new ConsoleNotificationChannel();
        }

        return new EmailChannel(senderEmail, appPassword);
    }

    @Override
    public void sendMessage(String to, String message) {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        properties.put("mail.smtp.connectiontimeout", "8000");
        properties.put("mail.smtp.timeout", "8000");
        properties.put("mail.smtp.writetimeout", "8000");

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

        } catch (MessagingException e) {
            System.out.println("Email notification failed for " + to + ": " + e.getMessage());
            System.out.println("Falling back to console notification.");
            new ConsoleNotificationChannel().sendMessage(to, message);
        }
    }
}