public class ReimbursementNotification extends Notification {

    public ReimbursementNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String to) {

        String message =
                "Your expense reimbursement has been processed successfully.";

        channel.sendMessage(to, message);
    }
}