package src.model;
public class AccommodationExpense extends ExpenseClaim{
     public AccommodationExpense(int employeeId, double amount,String email, String description){
        super(employeeId,amount,email,description);
     }

     public String getType() {
        return "ACCOMMODATION";
     }
}
