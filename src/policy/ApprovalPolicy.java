package policy;

public interface ApprovalPolicy {
    boolean requiresApproval(double amount);
}