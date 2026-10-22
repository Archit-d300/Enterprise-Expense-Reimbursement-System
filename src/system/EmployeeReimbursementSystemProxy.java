package system;

import db.PendingExpense;
import java.util.Locale;
import java.util.Set;
import java.util.Objects;

public class EmployeeReimbursementSystemProxy implements ReimbursementSystem {

    private static final Set<String> VALID_ROLES = Set.of(
            "EMPLOYEE", "MANAGER", "FINANCE", "ADMIN", "EXECUTIVE");
    private static final Set<String> VALID_EXPENSE_TYPES = Set.of(
            "TRAVEL", "FOOD", "ACCOMMODATION", "OFFICE");

    private ReimbursementSystem realSystem;

    public EmployeeReimbursementSystemProxy() {
    }

    public EmployeeReimbursementSystemProxy(ReimbursementSystem realSystem) {
        this.realSystem = Objects.requireNonNull(realSystem, "realSystem");
    }

    @Override
    public ClaimOutcome processClaim(PendingExpense pe) {

        if (!isValidRequest(pe)) {
            String reason = "Rejected by proxy: invalid or incomplete claim data.";
            System.out.println("[Proxy] " + reason);
            return new ClaimOutcome("REJECTED", reason, null);
        }

        System.out.println("[Proxy] Validated claim #" + pe.getId() + " - forwarding to EmployeeReimbursementSystem.");

        return getRealSystem().processClaim(pe);
    }

    private boolean isValidRequest(PendingExpense pe) {
        return pe != null
                && pe.getEmployeeId() > 0
                && pe.getEmployeeName() != null && !pe.getEmployeeName().isBlank()
                && pe.getEmployeeEmail() != null && !pe.getEmployeeEmail().isBlank()
                && pe.getDepartment() != null && !pe.getDepartment().isBlank()
                && isSupported(pe.getExpenseType(), VALID_EXPENSE_TYPES)
                && isSupported(pe.getRole(), VALID_ROLES)
                && Double.isFinite(pe.getAmount()) && pe.getAmount() > 0;
    }

    private boolean isSupported(String value, Set<String> supportedValues) {
        return value != null && supportedValues.contains(value.toUpperCase(Locale.ROOT));
    }

    private ReimbursementSystem getRealSystem() {
        if (realSystem == null) {
            realSystem = new EmployeeReimbursementSystem();
        }
        return realSystem;
    }
}
