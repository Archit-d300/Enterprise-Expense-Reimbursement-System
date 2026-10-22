package payment;

import model.ExpenseClaim;

public class Reimbursement {

    private ExpenseClaim claim;
    private double amount;
    private boolean employeeEligible;
    private boolean authorized;

    public Reimbursement(
            ExpenseClaim claim,
            double amount,
            boolean employeeEligible,
            boolean authorized) {

        this.claim = claim;
        this.amount = amount;
        this.employeeEligible = employeeEligible;
        this.authorized = authorized;
    }

    public ExpenseClaim getClaim() {
        return claim;
    }

    public double getAmount() {
        return amount;
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