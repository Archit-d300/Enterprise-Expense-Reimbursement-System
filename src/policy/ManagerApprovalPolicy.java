package policy;

public class ManagerApprovalPolicy implements ApprovalPolicy {
    @Override
    public boolean requiresApproval(double amount) {
        return amount>5000.0;
    }
}
