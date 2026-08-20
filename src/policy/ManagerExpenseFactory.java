package policy;

public class ManagerExpenseFactory implements ExpensePolicyFactory {
    @Override
    public ExpensePolicy createExpensePolicy() {
        return new ManagerExpensePolicy();
    }

    @Override
    public ApprovalPolicy createApprovalPolicy() {
        return new ManagerApprovalPolicy();
    }

    @Override
    public ReimbursementCalculator createReimbursementCalculator() {
        return new ManagerReimbursementCalculator();
    }
}
