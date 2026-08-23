package src.factory;
import src.model.ExpenseClaim;
import src.model.OfficeExpense;

public class OfficeExpenseFactory extends ExpenseFactory{
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        if(amount <= 0){
            throw new IllegalArgumentException("Office expense must be greater than 0.");
        }

        return new OfficeExpense(employeeId, amount, email, description);
    }
}
