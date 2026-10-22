package factory;
import model.ExpenseClaim;
import model.FoodExpense;

public class FoodExpenseFactory extends ExpenseFactory {
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        validateAmount(amount, "Food");

        return new FoodExpense(employeeId, amount,email, description);
    }
}
