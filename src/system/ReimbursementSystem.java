package system;

import db.PendingExpense;

public interface ReimbursementSystem {

    ClaimOutcome processClaim(PendingExpense pendingExpense);
}
