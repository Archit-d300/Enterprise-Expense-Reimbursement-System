package observer;

import model.ExpenseClaim;

public class EmployeeObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("EMPLOYEE NOTIFICATION: Claim #"+claim.getId() + 
                               " (Employee ID: " + claim.getEmployeeId() +
                               ") status changed -> " + claim.getStatus());
    }
}
