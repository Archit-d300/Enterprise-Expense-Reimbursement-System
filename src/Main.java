import java.util.Arrays;
import java.util.List;

import db.DatabaseManager;
import db.PendingExpense;

import system.ReimbursementSystem;
import system.EmployeeReimbursementSystemProxy;
import system.ClaimOutcome;

public class Main {

    public static void main(String[] args) {

        boolean resetDemo = Arrays.stream(args)
                .anyMatch(arg -> arg.equalsIgnoreCase("--reset") || arg.equalsIgnoreCase("reset"));

        DatabaseManager db = new DatabaseManager("expense_system.db");
        db.connect();

        if (resetDemo) {
            db.resetDatabase();
        }

        db.initializeSchema();
        db.seedSampleDataIfEmpty();

        List<PendingExpense> pendingClaims = db.fetchPending();

        if (pendingClaims.isEmpty()) {
            System.out.println("No pending expense claims found in the database.");
            db.close();
            return;
        }

        System.out.println("Found " + pendingClaims.size() + " pending claim(s) to process.\n");

        ReimbursementSystem reimbursementSystem = new EmployeeReimbursementSystemProxy();

        int approvedCount = 0;
        int rejectedCount = 0;
        int paymentFailedCount = 0;

        for (PendingExpense pe : pendingClaims) {

            System.out.println("=========================================================");
            System.out.println("Pending DB Row #" + pe.getId() + "  ->  " + pe);

            ClaimOutcome outcome = reimbursementSystem.processClaim(pe);

            db.insertProcessed(pe, outcome.getFinalStatus(), outcome.getRemarks(), outcome.getTransactionId());
            db.markPendingProcessed(pe.getId(), "PROCESSED");

            System.out.println("Final status: " + outcome.getFinalStatus() + " | Remarks: " + outcome.getRemarks());

            switch (outcome.getFinalStatus()) {
                case "APPROVED_REIMBURSED":
                    approvedCount++;
                    break;
                case "PAYMENT_FAILED":
                    paymentFailedCount++;
                    break;
                default:
                    rejectedCount++;
                    break;
            }
        }

        System.out.println("=========================================================");
        System.out.println("Run complete.");
        System.out.println("Approved & Reimbursed: " + approvedCount);
        System.out.println("Payment Failed:        " + paymentFailedCount);
        System.out.println("Rejected:              " + rejectedCount);
        System.out.println("See the 'processed_expenses' table in expense_system.db for full details.");

        db.close();
    }
}
