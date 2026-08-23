package policy;

public class EmployeeReimbursementCalculator implements ReimbursementCalculator {
    @Override
    public double calculate(double amount) {
        return Math.min(amount,5000.0);
    }
}