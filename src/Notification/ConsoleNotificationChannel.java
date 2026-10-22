package notification;

public final class ConsoleNotificationChannel implements NotificationChannel {
    @Override
    public void sendMessage(String to, String message) {
        System.out.println("[CONSOLE NOTIFICATION to " + to + "] " + message);
    }
}
