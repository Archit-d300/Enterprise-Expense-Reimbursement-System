package policy;

public class EmployeeExpenseFactory implements ExpensePolicyFactory {
    @Override
    public ExpensePolicy createExpensePolicy() {
        return new EmployeeExpensePolicy();
    }

    @Override
    public ApprovalPolicy createApprovalPolicy() {
        return new EmployeeApprovalPolicy();
    }

    @Override
    public ReimbursementCalculator createReimbursementCalculator() {
        return new EmployeeReimbursementCalculator();
    }
}