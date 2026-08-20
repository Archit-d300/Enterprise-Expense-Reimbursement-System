package policy;

public interface ExpensePolicy {
    boolean isAllowed(double amount);
}