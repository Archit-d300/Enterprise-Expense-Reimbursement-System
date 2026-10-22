package factory;
import model.ExpenseClaim;
import java.util.Locale;

public abstract class ExpenseFactory {

    public abstract ExpenseClaim createExpense(int employeeId, double amount, String email, String description);

    protected final void validateAmount(double amount, String expenseName) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(expenseName + " expense must be a finite amount greater than 0.");
        }
    }

    public static ExpenseFactory forType(String expenseType) {
        if (expenseType == null) {
            throw new IllegalArgumentException("Expense type must not be null.");
        }

        switch (expenseType.toUpperCase(Locale.ROOT)) {
            case "TRAVEL":
                return new TravelExpenseFactory();
            case "FOOD":
                return new FoodExpenseFactory();
            case "ACCOMMODATION":
                return new AccommodationExpenseFactory();
            case "OFFICE":
                return new OfficeExpenseFactory();
            default:
                throw new IllegalArgumentException("Unknown expense type: " + expenseType);
        }
    }
}