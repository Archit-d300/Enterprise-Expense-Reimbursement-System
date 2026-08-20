package policy;

public class ManagerReimbursementCalculator implements ReimbursementCalculator {
    @Override
    public double calculate(double amount) {
        return Math.min(amount,20000.0);
    }
}
