package policy;

public class EmployeeExpensePolicy implements ExpensePolicy {
    @Override
    public boolean isAllowed(double amount) {
        return amount<=5000.0;
    }
}
