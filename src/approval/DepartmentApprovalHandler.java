package approval;
import model.ExpenseClaim;
public class DepartmentApprovalHandler extends ExpenseHandler{
    private final double departmentBudget = 100000;

    @Override
    public boolean handle(ExpenseClaim claim) {

        if (claim.getAmount() <= departmentBudget) {
            approve(claim);

            if (next != null) {
                return next.handle(claim);
            }

            return true;
        }

        reject(claim, "Claim exceeds department budget.");
        return false;
    }

    private void approve(ExpenseClaim claim) {
        claim.setStatus("DEPARTMENT_APPROVED");
        System.out.println("Department Approval: ACCEPTED");
    }

    private void reject(ExpenseClaim claim, String reason) {
        claim.setStatus("REJECTED");
        claim.setRemarks(reason);
        System.out.println("Department Approval: REJECTED");
        System.out.println("Reason: " + reason);
    }
}
