public class OfficeExpense extends ExpenseClaim{

    public OfficeExpense(int employeeId, double amount, String email){
        super(employeeId,amount,email);
    }

    public String getType() {
        return "OFFICE";
    }
}