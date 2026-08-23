package factory;
import model.ExpenseClaim;

public abstract class ExpenseFactory {

    public abstract ExpenseClaim createExpense(int employeeId, double amount, String email, String description);
}