package factory;
import model.ExpenseClaim;
import model.FoodExpense;

public class FoodExpenseFactory extends ExpenseFactory {
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        if(amount <= 0){
            throw new IllegalArgumentException("Food expense must be greater than 0.");
        }

        return new FoodExpense(employeeId, amount,email, description);
    }
}
