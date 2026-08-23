package src.factory;
import src.model.ExpenseClaim;
import src.model.TravelExpense;

public class TravelExpenseFactory extends ExpenseFactory {
    
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        if(amount <= 0){
            throw new IllegalArgumentException("Travel expense must be greater than 0.");
        }

        return new TravelExpense(employeeId, amount, email, description);
    }
}
