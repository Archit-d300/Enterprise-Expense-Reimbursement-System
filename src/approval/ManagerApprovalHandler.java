package approval;
import model.ExpenseClaim;
public class ManagerApprovalHandler extends ExpenseHandler{
     @Override
    public boolean handle(ExpenseClaim claim) {

        if (claim.getAmount() <= 50000) {

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
        System.out.println("Manager Approval: REJECTED");
        System.out.println("Reason: " + reason);
    }
}
