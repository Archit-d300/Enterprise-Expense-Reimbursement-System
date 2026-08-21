package observer;

import model.ExpenseClaim;

public class EmployeeObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("EMPLOYEE NOTIFICATION: Claim status changed -> " +claim.getStatus());
    }
}
