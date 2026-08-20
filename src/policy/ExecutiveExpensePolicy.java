package policy;

public class ExecutiveExpensePolicy implements ExpensePolicy {
    @Override
    public boolean isAllowed(double amount) {
        return amount<=100000.0;
    }
}
