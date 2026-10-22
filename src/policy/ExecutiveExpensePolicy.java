package policy;

import model.ExpenseLimits;

public class ExecutiveExpensePolicy implements ExpensePolicy {
    @Override
    public boolean isAllowed(double amount) {
        return Double.isFinite(amount) && amount > 0 && amount <= ExpenseLimits.MAX_APPROVABLE_AMOUNT;
    }
}
