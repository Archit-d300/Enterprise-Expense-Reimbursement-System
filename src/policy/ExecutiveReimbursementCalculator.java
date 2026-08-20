package policy;

public class ExecutiveReimbursementCalculator implements ReimbursementCalculator {
    @Override
    public double calculate(double amount) {
        return Math.min(amount,100000.0);
    }
}
