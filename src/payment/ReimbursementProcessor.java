package payment;

import model.ExpenseClaim;

public class ReimbursementProcessor {

    private PaymentService paymentService;

    public ReimbursementProcessor() {
        // Use the Proxy instead of directly using RealPaymentService.
        this.paymentService = new PaymentServiceProxy();
    }

    public PaymentRecord processReimbursement(ExpenseClaim claim) {

        if (claim == null) {
            return new PaymentRecord(
                    "FAILED",
                    null,
                    "Payment failed: Expense claim is null."
            );
        }

        System.out.println("\n--- Reimbursement Processing Started ---");

        System.out.println(
                "Employee: " + claim.getEmployeeId()
        );

        System.out.println(
                "Amount: ₹" + claim.getAmount()
        );

        System.out.println(
                "Current claim status: " + claim.getStatus()
        );

        /*
         * For the normal integrated demo, the employee is assumed
         * to be eligible and the payment is authorized.
         *
         * The Proxy still performs both checks.
         */
        Reimbursement reimbursement =
                new Reimbursement(claim, true, true);

        PaymentRecord record =
                paymentService.processPayment(reimbursement);

        if ("SUCCESS".equals(record.getStatus())) {

            claim.setStatus("REIMBURSED");

            System.out.println(
                    "Reimbursement completed successfully."
            );

        } else {

            claim.setStatus("PAYMENT_FAILED");

            System.out.println(
                    "Reimbursement failed."
            );
        }

        return record;
    }
}