package policy;

public class EmployeeApprovalPolicy implements ApprovalPolicy {
    @Override
    public boolean requiresApproval(double amount) {
        return amount>1000.0;
    }
}
