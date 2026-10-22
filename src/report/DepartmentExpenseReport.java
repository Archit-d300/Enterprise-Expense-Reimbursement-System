package report;

import db.ProcessedExpense;
import java.util.List;
import java.util.Set;

public final class DepartmentExpenseReport extends ExpenseReport {
    private static final Set<String> AUTHORIZED_ROLES = Set.of("MANAGER", "ADMIN", "EXECUTIVE");

    @Override
    protected void authorize(ReportRequest request) {
        if (!AUTHORIZED_ROLES.contains(request.getRequesterRole())) {
            throw new SecurityException("Only managers, administrators, or executives may view department reports.");
        }
        if (request.getDepartment() == null || request.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department report requires a department filter.");
        }
    }

    @Override
    protected List<ProcessedExpense> filter(List<ProcessedExpense> records, ReportRequest request) {
        return matching(records,
                record -> request.getDepartment().equalsIgnoreCase(record.getDepartment()));
    }

    @Override
    protected String reportTitle() {
        return "Department Expense Report";
    }
}
