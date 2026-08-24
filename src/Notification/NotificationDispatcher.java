package notification;

public final class NotificationDispatcher {

    public static final String APPROVED_REIMBURSED = "APPROVED_REIMBURSED";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String REJECTED = "REJECTED";

    private NotificationDispatcher() {
       
    }

    public static void dispatch(String finalStatus, String recipient, NotificationChannel channel) {

        Notification notification;

        switch (finalStatus) {
            case APPROVED_REIMBURSED:
                notification = new ReimbursementNotification(channel);
                break;
            case REJECTED:
            case PAYMENT_FAILED:
                notification = new RejectionNotification(channel);
                break;
            default:
                throw new IllegalArgumentException("Unknown final status: " + finalStatus);
        }

        notification.send(recipient);
    }
}
