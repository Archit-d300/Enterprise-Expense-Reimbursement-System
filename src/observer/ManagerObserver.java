package observer;

import model.ExpenseClaim;

public class ManagerObserver implements Observer {
    private boolean isManagerSubmitter = false;

    public void setManagerSubmitter(boolean isManagerSubmitter) {
        this.isManagerSubmitter = isManagerSubmitter;
    }

    @Override
    public void update(ExpenseClaim claim) {
        if (isManagerSubmitter) {
            System.out.println("EXECUTIVE NOTIFICATION: Claim #"+claim.getId() + 
                               " filed by Manager requires higher level approval -> " + claim.getStatus());
        } else {
            System.out.println("MANAGER NOTIFICATION: Claim #"+claim.getId() + 
                               " (Employee ID: " + claim.getEmployeeId() + 
                               ") status changed -> " + claim.getStatus());
        }
    }
}
