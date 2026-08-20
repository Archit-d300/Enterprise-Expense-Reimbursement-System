package policy;

public class ExecutiveApprovalPolicy implements ApprovalPolicy {
    @Override
    public boolean requiresApproval(double amount) {
        return amount>20000.0;
    }
}
