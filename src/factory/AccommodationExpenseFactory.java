package factory;
import model.ExpenseClaim;
import model.AccommodationExpense;

public class AccommodationExpenseFactory extends ExpenseFactory {
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        validateAmount(amount, "Accommodation");

        return new AccommodationExpense(employeeId, amount, email, description);
    }
}
