package policy;

import model.ExpenseLimits;

public class ManagerReimbursementCalculator implements ReimbursementCalculator {
    @Override
    public double calculate(double amount) {
        return Math.min(amount, ExpenseLimits.MAX_APPROVABLE_AMOUNT);
    }
}
