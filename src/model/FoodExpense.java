package src.model;
public class FoodExpense extends ExpenseClaim{

    public FoodExpense(int employeeId, double amount, String email, String description){
        super(employeeId,amount,email,description);
    }

    public String getType() {
        return "FOOD";
    }
}