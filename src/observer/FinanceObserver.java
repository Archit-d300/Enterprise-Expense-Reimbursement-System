package observer;

import model.ExpenseClaim;

public class FinanceObserver implements Observer {
    @Override
    public void update(ExpenseClaim claim) {
        System.out.println("FINANCE NOTIFICATION: Claim status updated for finance processing -> " + claim);
    }
}
