package Notification;

public abstract class Notification {

    protected NotificationChannel channel;

    public Notification(NotificationChannel channel) {
        this.channel = channel;
    }

    public abstract void send(String to);
}