package factory;
import model.ExpenseClaim;

public abstract class ExpenseFactory {

    public abstract ExpenseClaim createExpense(int employeeId, double amount, String email, String description);

    public static ExpenseFactory forType(String expenseType) {
        if (expenseType == null) {
            throw new IllegalArgumentException("Expense type must not be null.");
        }

        switch (expenseType.toUpperCase()) {
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