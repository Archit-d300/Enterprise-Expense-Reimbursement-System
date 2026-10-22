package approval;
import model.ExpenseClaim;
import model.ExpenseLimits;
public class ManagerApprovalHandler extends ExpenseHandler{
     @Override
    public boolean handle(ExpenseClaim claim) {

        if (claim.getAmount() <= ExpenseLimits.MAX_APPROVABLE_AMOUNT) {

            approve(claim);
            System.out.println("Manager Approval: PASSED");

            if (next != null) {
                return next.handle(claim);
            }

            return true;
        }

        reject(claim, "Amount exceeds manager approval limit.");
        return false;
    }

    private void approve(ExpenseClaim claim) {
        claim.setStatus("MANAGER_APPROVED");
        System.out.println("Manager Approval: ACCEPTED");
    }

    private void reject(ExpenseClaim claim, String reason) {
        claim.setStatus("REJECTED");
        claim.setRemarks(reason);
        System.out.println("Manager Approval: REJECTED");
        System.out.println("Reason: " + reason);
    }
}
