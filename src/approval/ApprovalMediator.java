package approval;

import model.ExpenseClaim;

public final class ApprovalMediator {

    public boolean process(ExpenseClaim claim, boolean requiresApproval) {
        if (claim == null) {
            throw new IllegalArgumentException("Expense claim must not be null.");
        }

        if (requiresApproval) {
            return Chain.createChain().handle(claim);
        }

        boolean receiptValidated = new ReceiptValidationHandler().handle(claim);
        if (receiptValidated) {
            claim.setStatus("APPROVED");
            System.out.println("Approval Mediator: no additional approval required.");
        }
        return receiptValidated;
    }
}
