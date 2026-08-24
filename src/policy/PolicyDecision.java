package policy;

public class PolicyDecision {

    private final boolean allowed;
    private final boolean requiresApproval;
    private final double eligibleAmount;
    private final String reason;

    public PolicyDecision(boolean allowed, boolean requiresApproval, double eligibleAmount, String reason) {
        this.allowed = allowed;
        this.requiresApproval = requiresApproval;
        this.eligibleAmount = eligibleAmount;
        this.reason = reason;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public boolean requiresApproval() {
        return requiresApproval;
    }

    public double getEligibleAmount() {
        return eligibleAmount;
    }

    public String getReason() {
        return reason;
    }
}
