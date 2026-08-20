package policy;

public class ManagerExpensePolicy implements ExpensePolicy {
    @Override
    public boolean isAllowed(double amount) {
        return amount<=20000.0;
    }
}
