package observer;

import db.ExpenseClaimDAO;
import model.ExpenseClaim;

public class PersistenceObserver implements Observer {
    private final ExpenseClaimDAO dao = new ExpenseClaimDAO();

    @Override
    public void update(ExpenseClaim claim) {
        dao.saveOrUpdate(claim);
        System.out.println("DATABASE: Claim #" + claim.getId() + " synchronized with database.");
    }
}
