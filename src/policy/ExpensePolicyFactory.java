package policy;

public interface ExpensePolicyFactory {
    ExpensePolicy createExpensePolicy();
    ApprovalPolicy createApprovalPolicy();
    ReimbursementCalculator createReimbursementCalculator();
}
