package system;


public class ClaimOutcome {

    private final String finalStatus;
    private final String remarks;
    private final String transactionId;

    public ClaimOutcome(String finalStatus, String remarks, String transactionId) {
        this.finalStatus = finalStatus;
        this.remarks = remarks;
        this.transactionId = transactionId;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
