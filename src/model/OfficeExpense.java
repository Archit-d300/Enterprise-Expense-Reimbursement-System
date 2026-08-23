package model;
public class OfficeExpense extends ExpenseClaim{

    public OfficeExpense(int employeeId, double amount, String email, String description){
        super(employeeId,amount,email, description);
    }

    public String getType() {
        return "OFFICE";
    }
}