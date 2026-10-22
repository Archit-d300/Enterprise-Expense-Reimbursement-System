package approval;
import model.ExpenseClaim;
public class ReceiptValidationHandler extends ExpenseHandler {

    @Override
    public boolean handle(ExpenseClaim claim) {
        if (claim.getReceiptReference() == null || claim.getReceiptReference().trim().isEmpty()) {
            reject(claim, "Receipt reference is missing.");
            return false;
        }

        approve(claim);

        if (next != null) {
            return next.handle(claim);
        }

        return true;
    }

    private void approve(ExpenseClaim claim) {
        claim.setStatus("RECEIPT_VALIDATED");
        System.out.println("Receipt Validation: ACCEPTED");
    }

    private void reject(ExpenseClaim claim, String reason) {
        claim.setStatus("REJECTED");
        claim.setRemarks(reason);
        System.out.println("Receipt Validation: REJECTED");
        System.out.println("Reason: " + reason);
    }
    
}
