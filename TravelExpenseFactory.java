public class TravelExpenseFactory extends ExpenseFactory {
    
    public ExpenseClaim createExpense(int employeeId, double amount, String email){
        if(amount <= 0){
            throw new IllegalArgumentException("Travel expense must be greater than 0.");
        }

        return new TravelExpense(employeeId, amount, email);
    }
}
