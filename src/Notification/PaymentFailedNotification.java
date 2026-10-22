package notification;

public class PaymentFailedNotification extends Notification {

    public PaymentFailedNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String to) {
        channel.sendMessage(to, "Your expense was approved, but the reimbursement payment could not be processed.");
    }
}
