package factory;
import model.ExpenseClaim;
import model.OfficeExpense;

public class OfficeExpenseFactory extends ExpenseFactory{
    public ExpenseClaim createExpense(int employeeId, double amount, String email, String description){
        validateAmount(amount, "Office");

        return new OfficeExpense(employeeId, amount, email, description);
    }
}
