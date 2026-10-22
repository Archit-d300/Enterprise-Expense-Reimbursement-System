package factory;
import model.ExpenseClaim;
import model.TravelExpense;

public class TravelExpenseFactory extends ExpenseFactory {
    
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        validateAmount(amount, "Travel");

        return new TravelExpense(employeeId, amount, email, description);
    }
}
