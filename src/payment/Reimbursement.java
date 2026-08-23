package payment;

import model.ExpenseClaim;

public class Reimbursement {

    private ExpenseClaim claim;
    private boolean employeeEligible;
    private boolean authorized;

    public Reimbursement(
            ExpenseClaim claim,
            boolean employeeEligible,
            boolean authorized) {

        this.claim = claim;
        this.employeeEligible = employeeEligible;
        this.authorized = authorized;
    }

    public ExpenseClaim getClaim() {
        return claim;
    }

    public double getAmount() {
        return claim.getAmount();
    }

    public int getEmployeeId() {
        return claim.getEmployeeId();
    }

    public boolean isEmployeeEligible() {
        return employeeEligible;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}