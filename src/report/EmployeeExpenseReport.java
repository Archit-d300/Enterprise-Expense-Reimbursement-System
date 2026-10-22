package report;

import db.ProcessedExpense;
import java.util.List;

public final class EmployeeExpenseReport extends ExpenseReport {
    @Override
    protected void authorize(ReportRequest request) {
        if (request.getRequesterEmployeeId() <= 0) {
            throw new SecurityException("Employee report access requires an authenticated employee.");
        }
    }

    @Override
    protected List<ProcessedExpense> filter(List<ProcessedExpense> records, ReportRequest request) {
        return matching(records, record -> record.getEmployeeId() == request.getRequesterEmployeeId());
    }

    @Override
    protected String reportTitle() {
        return "Employee Expense Report";
    }
}
