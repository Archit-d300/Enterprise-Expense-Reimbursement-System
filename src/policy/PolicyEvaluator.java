package policy;


public final class PolicyEvaluator {

    private PolicyEvaluator() {
        
    }

    public static PolicyDecision evaluate(ExpensePolicyFactory policyFactory, double amount) {

        ExpensePolicy expensePolicy = policyFactory.createExpensePolicy();
        ApprovalPolicy approvalPolicy = policyFactory.createApprovalPolicy();
        ReimbursementCalculator calculator = policyFactory.createReimbursementCalculator();

        boolean allowed = expensePolicy.isAllowed(amount);
        boolean requiresApproval = approvalPolicy.requiresApproval(amount);
        double eligibleAmount = calculator.calculate(amount);

        String reason = allowed
                ? null
                : "Amount exceeds the role-based expense policy limit.";

        return new PolicyDecision(allowed, requiresApproval, eligibleAmount, reason);
    }
}
