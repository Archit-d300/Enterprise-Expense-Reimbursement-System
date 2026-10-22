package report;

import db.DatabaseManager;
import db.ProcessedExpense;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class ExpenseReport {
    public final String generateReport(DatabaseManager database, ReportRequest request) {
        if (database == null || request == null) {
            throw new IllegalArgumentException("Database and report request are required.");
        }
        authorize(request);
        List<ProcessedExpense> records = filter(database.fetchProcessedExpenses(), request);
        return format(records, summarize(records));
    }

    protected abstract void authorize(ReportRequest request);

    protected abstract List<ProcessedExpense> filter(
            List<ProcessedExpense> records, ReportRequest request);

    protected String reportTitle() {
        return getClass().getSimpleName();
    }

    private ReportSummary summarize(List<ProcessedExpense> records) {
        double totalAmount = 0;
        double reimbursedAmount = 0;
        int paymentFailures = 0;
        for (ProcessedExpense record : records) {
            totalAmount += record.getAmount();
            if ("APPROVED_REIMBURSED".equals(record.getFinalStatus())) {
                reimbursedAmount += record.getAmount();
            } else if ("PAYMENT_FAILED".equals(record.getFinalStatus())) {
                paymentFailures++;
            }
        }
        return new ReportSummary(records.size(), totalAmount, reimbursedAmount, paymentFailures);
    }

    private String format(List<ProcessedExpense> records, ReportSummary summary) {
        StringBuilder output = new StringBuilder();
        output.append("=== ").append(reportTitle()).append(" ===\n");
        output.append("Claims: ").append(summary.claimCount).append('\n');
        output.append("Total claimed: Rs ").append(money(summary.totalAmount)).append('\n');
        output.append("Total reimbursed: Rs ").append(money(summary.reimbursedAmount)).append('\n');
        output.append("Payment failures: ").append(summary.paymentFailures).append('\n');
        output.append("Details:\n");
        if (records.isEmpty()) {
            output.append("(no matching processed claims)\n");
        } else {
            for (ProcessedExpense record : records) {
                output.append('#').append(record.getPendingId())
                        .append(" | ").append(record.getProcessedAt())
                        .append(" | ").append(record.getDepartment())
                        .append(" | ").append(record.getExpenseType())
                        .append(" | Rs ").append(money(record.getAmount()))
                        .append(" | ").append(record.getFinalStatus())
                        .append(" | ").append(record.getDescription() == null ? "" : record.getDescription())
                        .append('\n');
            }
        }
        return output.toString();
    }

    private String money(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }

    protected final List<ProcessedExpense> matching(
            List<ProcessedExpense> records, RecordFilter filter) {
        List<ProcessedExpense> result = new ArrayList<>();
        for (ProcessedExpense record : records) {
            if (filter.matches(record)) {
                result.add(record);
            }
        }
        return result;
    }

    protected interface RecordFilter {
        boolean matches(ProcessedExpense record);
    }

    private static final class ReportSummary {
        private final int claimCount;
        private final double totalAmount;
        private final double reimbursedAmount;
        private final int paymentFailures;

        private ReportSummary(int claimCount, double totalAmount, double reimbursedAmount, int paymentFailures) {
            this.claimCount = claimCount;
            this.totalAmount = totalAmount;
            this.reimbursedAmount = reimbursedAmount;
            this.paymentFailures = paymentFailures;
        }
    }
}
