package approval;
import model.ExpenseClaim;
public class FinalApprovalHandler extends ExpenseHandler {
     @Override
    public boolean handle(ExpenseClaim claim) {

       if ("FINANCE_VERIFIED".equals(claim.getStatus())) {
            accept(claim);
            return true;
        }

        reject(claim, "Previous approval stages are not completed.");
        return false;
    }

    private void accept(ExpenseClaim claim) {
        claim.setStatus("APPROVED");

        System.out.println("Final Approval: ACCEPTED");
        System.out.println("Claim #" + claim.getId() + " is now APPROVED.");
    }

    private void reject(ExpenseClaim claim, String reason) {
        claim.setStatus("REJECTED");
        claim.setRemarks(reason);

        System.out.println("Final Approval: REJECTED");
        System.out.println("Reason: " + reason);
    }
}
