public class AccommodationExpense extends ExpenseClaim{
     public AccommodationExpense(int employeeId, double amount,String email){
        super(employeeId,amount,email);
     }

     public String getType() {
        return "ACCOMMODATION";
     }
}
