package src.model;
public class TravelExpense extends ExpenseClaim {
    
    public TravelExpense(int employeeId, double amount, String email, String description){
        super(employeeId, amount, email, description);
    }

    public String getType(){
        return "TRAVEL";
    }
    
}