package observer;

import model.ExpenseClaim;

public class ManagerObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("MANAGER NOTIFICATION: A claim you handle changed status -> " + claim);
    }
}
