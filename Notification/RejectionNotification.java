public class RejectionNotification extends Notification {

    public RejectionNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String to) {

        String message =
                "Your expense reimbursement request has been rejected.";

        channel.sendMessage(to, message);
    }
}