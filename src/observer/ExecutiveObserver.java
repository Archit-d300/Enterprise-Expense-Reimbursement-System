package observer;

import model.ExpenseClaim;

public class ExecutiveObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("EXECUTIVE NOTIFICATION: Claim #" + claim.getId() + 
                           " (Submitted by Management) requires higher-level approval -> " + claim.getStatus());
    }
}
