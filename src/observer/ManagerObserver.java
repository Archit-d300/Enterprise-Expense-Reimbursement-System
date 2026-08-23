package observer;

import model.ExpenseClaim;

public class ManagerObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("MANAGER NOTIFICATION: Claim #" + claim.getId() + 
                           " (Employee ID: " + claim.getEmployeeId() + 
                           ") status changed -> " + claim.getStatus());
    }
}