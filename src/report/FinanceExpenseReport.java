package report;

import db.ProcessedExpense;
import java.util.List;
import java.util.Set;

public final class FinanceExpenseReport extends ExpenseReport {
    private static final Set<String> AUTHORIZED_ROLES = Set.of("FINANCE", "ADMIN", "EXECUTIVE");

    @Override
    protected void authorize(ReportRequest request) {
        if (!AUTHORIZED_ROLES.contains(request.getRequesterRole())) {
            throw new SecurityException("Only finance officers, administrators, or executives may view finance reports.");
        }
    }

    @Override
    protected List<ProcessedExpense> filter(List<ProcessedExpense> records, ReportRequest request) {
        return matching(records, record -> "APPROVED_REIMBURSED".equals(record.getFinalStatus())
                || "PAYMENT_FAILED".equals(record.getFinalStatus()));
    }

    @Override
    protected String reportTitle() {
        return "Finance Expense Report";
    }
}
