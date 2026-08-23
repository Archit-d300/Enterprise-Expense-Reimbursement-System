package notification;

public class ApprovalNotification extends Notification {

    public ApprovalNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String to) {

        String message = "Your expense reimbursement request has been approved.";

        channel.sendMessage(to, message);
    }
}