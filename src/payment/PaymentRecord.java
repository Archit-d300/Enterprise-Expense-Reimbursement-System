package payment;

import java.time.LocalDateTime;

public class PaymentRecord {

    private String status;
    private String transactionId;
    private String reason;
    private LocalDateTime timestamp;

    public PaymentRecord(String status, String transactionId, String reason) {
        this.status = status;
        this.transactionId = transactionId;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PaymentRecord{" +
                "status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}