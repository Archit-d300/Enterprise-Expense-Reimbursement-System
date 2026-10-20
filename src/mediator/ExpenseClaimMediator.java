package mediator;

import db.PendingExpense;
import system.ClaimOutcome;

public interface ExpenseClaimMediator {
    ClaimOutcome processClaim(PendingExpense pendingExpense);
}