package payment;

public class PaymentServiceProxy implements PaymentService {

    private RealPaymentService realPaymentService;

    public PaymentServiceProxy() {
        this.realPaymentService = new RealPaymentService();
    }

    @Override
    public PaymentRecord processPayment(Reimbursement reimbursement) {

        // Check 1: Claim must be approved
        if (!isApproved(reimbursement)) {
            return failedPayment(
                    "Payment rejected: Expense claim is not approved."
            );
        }

        // Check 2: Employee must be eligible
        if (!isEligible(reimbursement)) {
            return failedPayment(
                    "Payment rejected: Employee is not eligible."
            );
        }

        // Check 3: Amount must be valid
        if (!isAmountValid(reimbursement)) {
            return failedPayment(
                    "Payment rejected: Invalid reimbursement amount."
            );
        }

        // Check 4: Payment must be authorized
        if (!isAuthorized(reimbursement)) {
            return failedPayment(
                    "Payment rejected: Payment is not authorized."
            );
        }

        // All checks passed
        System.out.println("Payment Proxy: All validation checks passed.");

        return realPaymentService.processPayment(reimbursement);
    }

    private boolean isApproved(Reimbursement reimbursement) {

        return reimbursement != null
                && reimbursement.getClaim() != null
                && "APPROVED".equals(
                        reimbursement.getClaim().getStatus()
                );
    }

    private boolean isEligible(Reimbursement reimbursement) {

        return reimbursement != null
                && reimbursement.isEmployeeEligible();
    }

    private boolean isAmountValid(Reimbursement reimbursement) {

        if (reimbursement == null) {
            return false;
        }

        double amount = reimbursement.getAmount();


        return amount > 0 && amount <= 100000;
    }

    private boolean isAuthorized(Reimbursement reimbursement) {

        return reimbursement != null
                && reimbursement.isAuthorized();
    }

    private PaymentRecord failedPayment(String reason) {

        System.out.println(reason);

        return new PaymentRecord(
                "FAILED",
                null,
                reason
        );
    }
}