package policy;

public class ExecutiveExpenseFactory implements ExpensePolicyFactory {
    @Override
    public ExpensePolicy createExpensePolicy() {
        return new ExecutiveExpensePolicy();
    }

    @Override
    public ApprovalPolicy createApprovalPolicy() {
        return new ExecutiveApprovalPolicy();
    }

    @Override
    public ReimbursementCalculator createReimbursementCalculator() {
        return new ExecutiveReimbursementCalculator();
    }
}
