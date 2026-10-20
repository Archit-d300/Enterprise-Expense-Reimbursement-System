package system;

import db.PendingExpense;
import mediator.ExpenseClaimMediator;
import mediator.ExpenseClaimMediatorImpl;

public class EmployeeReimbursementSystem implements ReimbursementSystem {

    private final ExpenseClaimMediator mediator;

    public EmployeeReimbursementSystem() {
        this.mediator = new ExpenseClaimMediatorImpl();
    }

    @Override
    public ClaimOutcome processClaim(PendingExpense pe) {
        return mediator.processClaim(pe);
    }
}