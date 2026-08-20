public class TravelExpense extends ExpenseClaim {
    
    public TravelExpense(int employeeId, double amount, String email){
        super(employeeId, amount, email);
    }

    public String getType(){
        return "TRAVEL";
    }
    
}