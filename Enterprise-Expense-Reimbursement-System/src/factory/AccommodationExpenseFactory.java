package src.factory;
import src.model.ExpenseClaim;
import src.model.AccommodationExpense;

public class AccommodationExpenseFactory extends ExpenseFactory {
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        if(amount <= 0){
            throw new IllegalArgumentException("Accommodation expense must be greater than 0.");
        }

        return new AccommodationExpense(employeeId, amount, email, description);
    }
}
