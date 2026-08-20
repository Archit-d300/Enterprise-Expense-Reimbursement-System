public class EmailNotification implements NotificationChannel{

    @Override
    public void sendMessage(String to, String message){
        System.out.println("Email Notification sent to "+to);
        System.out.println("Message:\n"+message);
    }
}
