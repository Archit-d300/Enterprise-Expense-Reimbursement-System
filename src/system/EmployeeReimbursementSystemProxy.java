package system;

import db.PendingExpense;

public class EmployeeReimbursementSystemProxy implements ReimbursementSystem {

    private ReimbursementSystem realSystem;

    @Override
    public ClaimOutcome processClaim(PendingExpense pe) {

        if (!isValidRequest(pe)) {
            String reason = "Rejected by proxy: invalid or incomplete claim data.";
            System.out.println("[Proxy] " + reason);
            return new ClaimOutcome("REJECTED", reason, null);
        }

        System.out.println("[Proxy] Validated claim #" + pe.getId() + " - forwarding to EmployeeReimbursementSystem.");

        return getRealSystem().processClaim(pe);
    }

    private boolean isValidRequest(PendingExpense pe) {
        return pe != null
                && pe.getEmployeeEmail() != null && !pe.getEmployeeEmail().isBlank()
                && pe.getExpenseType() != null && !pe.getExpenseType().isBlank()
                && pe.getRole() != null && !pe.getRole().isBlank()
                && pe.getAmount() > 0;
    }

    private ReimbursementSystem getRealSystem() {
        if (realSystem == null) {
            realSystem = new EmployeeReimbursementSystem();
        }
        return realSystem;
    }
}
