package payment;

import java.util.UUID;

public class RealPaymentService implements PaymentService {

    @Override
    public PaymentRecord processPayment(Reimbursement reimbursement) {

        // Simulate the actual payment transfer
        String transactionId =
                "TXN-" + UUID.randomUUID().toString().substring(0, 8);

        System.out.println(
                "Payment processed successfully for employee: "
                        + reimbursement.getEmployeeId()
        );

        System.out.println(
                "Amount transferred: ₹" + reimbursement.getAmount()
        );

        System.out.println(
                "Transaction ID: " + transactionId
        );

        return new PaymentRecord(
                "SUCCESS",
                transactionId,
                "Payment processed successfully"
        );
    }
}