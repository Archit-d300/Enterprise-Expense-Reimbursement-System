public class FoodExpense extends ExpenseClaim{

    public FoodExpense(int employeeId, double amount, String email){
        super(employeeId,amount,email);
    }

    public String getType() {
        return "FOOD";
    }
}