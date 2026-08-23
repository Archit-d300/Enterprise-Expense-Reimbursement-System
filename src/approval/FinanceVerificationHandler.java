package approval;
import model.ExpenseClaim;
public class FinanceVerificationHandler extends ExpenseHandler {
     private final double financeLimit = 200000;

    @Override
    public boolean handle(ExpenseClaim claim) {

        if (claim.getAmount() > 0 && claim.getAmount() <= financeLimit) {

            accept(claim);

            if (next != null) {
                return next.handle(claim);
            }

            return true;
        }

        reject(claim, "Claim failed finance verification.");
        return false;
    }

    private void accept(ExpenseClaim claim) {
        claim.setStatus("FINANCE_VERIFIED");
        System.out.println("Finance Verification: ACCEPTED");
    }

    private void reject(ExpenseClaim claim, String reason) {
        claim.setStatus("REJECTED");
        System.out.println("Finance Verification: REJECTED");
        System.out.println("Reason: " + reason);
    }
}
