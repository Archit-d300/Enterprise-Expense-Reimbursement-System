import java.util.Arrays;
import java.util.List;

import db.DatabaseManager;
import db.PendingExpense;
import model.ExpenseClaim;
import model.ExpenseClaimBuilder;
import model.ExpenseLimits;
import notification.NotificationChannel;
import payment.PaymentRecord;
import payment.PaymentServiceProxy;
import payment.Reimbursement;
import report.DepartmentExpenseReport;
import report.EmployeeExpenseReport;
import report.FinanceExpenseReport;
import report.ReportRequest;
import singleton.ExpenseManagementSystem;
import system.EmployeeReimbursementSystem;
import system.ReimbursementSystem;
import system.EmployeeReimbursementSystemProxy;
import system.ClaimOutcome;

public class Main {

    public static void main(String[] args) {
        boolean runDemo = Arrays.stream(args)
                .anyMatch(arg -> arg.equalsIgnoreCase("--demo") || arg.equalsIgnoreCase("demo"));
        if (runDemo) {
            runDemo();
            return;
        }

        boolean resetDemo = Arrays.stream(args)
                .anyMatch(arg -> arg.equalsIgnoreCase("--reset") || arg.equalsIgnoreCase("reset"));

        DatabaseManager db = new DatabaseManager("expense_system.db");
        db.connect();

        try {
            if (resetDemo) {
                db.resetDatabase();
            }

            db.initializeSchema();
            db.seedSampleDataIfEmpty();

            List<PendingExpense> pendingClaims = db.fetchPending();

            if (pendingClaims.isEmpty()) {
                System.out.println("No pending expense claims found in the database.");
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
        } finally {
            db.close();
        }
    }

    private static void runDemo() {
        System.out.println("=== Enterprise Expense Reimbursement System Demo ===");

        DatabaseManager database = new DatabaseManager(":memory:");
        database.connect();
        try {
            database.initializeSchema();

            System.out.println("\n--- Builder and expense factory ---");
            ExpenseClaim createdWithBuilder = new ExpenseClaimBuilder()
                    .setType("TRAVEL")
                    .setEmployeeId(201)
                    .setEmail("demo.employee@example.com")
                    .setRole("EMPLOYEE")
                    .setDepartment("Engineering")
                    .setAmount(12000)
                    .setDescription("Client visit travel")
                    .setReceiptReference("demo/receipts/travel-201.pdf")
                    .build();
            createdWithBuilder.submit();

            System.out.println("\n--- Prototype ---");
            ExpenseClaim previousClaim = new ExpenseClaimBuilder()
                    .setType("TRAVEL")
                    .setEmployeeId(201)
                    .setEmail("demo.employee@example.com")
                    .setRole("EMPLOYEE")
                    .setDepartment("Engineering")
                    .setAmount(10000)
                    .setDescription("Prior month client visit")
                    .setReceiptReference("demo/receipts/travel-previous.pdf")
                    .build();
            previousClaim.setStatus("REIMBURSED");
            ExpenseClaim duplicatedClaim = previousClaim.cloneExpense();
            duplicatedClaim.setAmount(14000);
            duplicatedClaim.setDescription("This month's client visit travel");
            duplicatedClaim.setReceiptReference("demo/receipts/travel-current.pdf");
            duplicatedClaim.submit();

            System.out.println("Builder created claim #" + createdWithBuilder.getId()
                    + " with status " + createdWithBuilder.getStatus());
            System.out.println("Prototype copied claim #" + previousClaim.getId()
                    + " into new claim #" + duplicatedClaim.getId()
                    + " with fresh status " + duplicatedClaim.getStatus()
                    + "; original remains " + previousClaim.getStatus());

            NotificationChannel demoNotifications = (to, message) ->
                    System.out.println("[DEMO NOTIFICATION to " + to + "] " + message);
            ReimbursementSystem processor = new EmployeeReimbursementSystemProxy(
                    new EmployeeReimbursementSystem(demoNotifications));
            process(database, processor, createdWithBuilder, "Demo Employee");
            process(database, processor, duplicatedClaim, "Demo Employee");

            System.out.println("\n--- Role policies, approval chain, observers, and notifications ---");
            process(database, processor, new ExpenseClaimBuilder()
                    .setType("FOOD")
                    .setEmployeeId(202)
                    .setEmail("demo.manager@example.com")
                    .setRole("MANAGER")
                    .setDepartment("Operations")
                    .setAmount(4000)
                    .setDescription("Vendor meeting meal")
                    .setReceiptReference("demo/receipts/food-202.pdf")
                    .build(), "Demo Manager");
            process(database, processor, new ExpenseClaimBuilder()
                    .setType("ACCOMMODATION")
                    .setEmployeeId(203)
                    .setEmail("demo.finance@example.com")
                    .setRole("FINANCE")
                    .setDepartment("Finance")
                    .setAmount(1500)
                    .setDescription("Audit visit accommodation")
                    .setReceiptReference("demo/receipts/hotel-203.pdf")
                    .build(), "Demo Finance Officer");
            process(database, processor, new ExpenseClaimBuilder()
                    .setType("OFFICE")
                    .setEmployeeId(204)
                    .setEmail("demo.executive@example.com")
                    .setRole("EXECUTIVE")
                    .setDepartment("Administration")
                    .setAmount(5000)
                    .setDescription("Executive office equipment")
                    .setReceiptReference("demo/receipts/office-204.pdf")
                    .build(), "Demo Executive");

            System.out.println("\n--- Role policy limit rejection ---");
            process(database, processor, new ExpenseClaimBuilder()
                    .setType("TRAVEL")
                    .setEmployeeId(206)
                    .setEmail("demo.employee@example.com")
                    .setRole("EMPLOYEE")
                    .setDepartment("Sales")
                    .setAmount(ExpenseLimits.MAX_APPROVABLE_AMOUNT + 1)
                    .setDescription("Claim above the policy limit")
                    .setReceiptReference("demo/receipts/over-limit-206.pdf")
                    .build(), "Demo Employee");

            System.out.println("\n--- Receipt validation rejection ---");
            ExpenseClaim missingReceipt = new ExpenseClaimBuilder()
                    .setType("OFFICE")
                    .setEmployeeId(201)
                    .setEmail("demo.employee@example.com")
                    .setRole("EMPLOYEE")
                    .setDepartment("Engineering")
                    .setAmount(250)
                    .setDescription("Receipt intentionally omitted")
                    .build();
            missingReceipt.submit();
            process(database, processor, missingReceipt, "Demo Employee");

            System.out.println("\n--- Payment proxy authorization check ---");
            ExpenseClaim unauthorizedClaim = new ExpenseClaimBuilder()
                    .setEmployeeId(205)
                    .setEmail("demo.employee@example.com")
                    .setAmount(300)
                    .setDescription("Unauthorized payment check")
                    .setReceiptReference("demo/receipts/unauthorized-205.pdf")
                    .build();
            unauthorizedClaim.submit();
            unauthorizedClaim.setStatus("APPROVED");
            PaymentRecord failedPayment = new PaymentServiceProxy().processPayment(
                    new Reimbursement(unauthorizedClaim, 300, true, false));
            System.out.println("Payment proxy result: " + failedPayment.getStatus());

            System.out.println("\nSingleton registry reuses one instance: "
                    + (ExpenseManagementSystem.getInstance() == ExpenseManagementSystem.getInstance()));

            System.out.println("\n--- Template Method reports ---");
            System.out.println("\n--- Employee report (employee 201) ---");
            System.out.print(new EmployeeExpenseReport().generateReport(
                    database, new ReportRequest(201, "EMPLOYEE", null)));
            System.out.println("--- Department report (Engineering) ---");
            System.out.print(new DepartmentExpenseReport().generateReport(
                    database, new ReportRequest(900, "MANAGER", "Engineering")));
            System.out.println("--- Finance report ---");
            System.out.print(new FinanceExpenseReport().generateReport(
                    database, new ReportRequest(901, "FINANCE", null)));
        } finally {
            database.close();
        }
    }

    private static void process(DatabaseManager database, ReimbursementSystem processor,
                                ExpenseClaim claim, String employeeName) {
        PendingExpense newExpense = new PendingExpense(
                -1, claim.getEmployeeId(), employeeName, claim.getEmail(), claim.getRole(),
                claim.getType(), claim.getAmount(), claim.getDescription(),
                claim.getDepartment(), claim.getReceiptReference());
        int id = database.insertPending(newExpense);
        PendingExpense storedExpense = new PendingExpense(
                id, newExpense.getEmployeeId(), newExpense.getEmployeeName(), newExpense.getEmployeeEmail(),
                newExpense.getRole(), newExpense.getExpenseType(), newExpense.getAmount(),
                newExpense.getDescription(), newExpense.getDepartment(), newExpense.getReceiptReference());
        ClaimOutcome outcome = processor.processClaim(storedExpense);
        database.insertProcessed(storedExpense, outcome.getFinalStatus(),
                outcome.getRemarks(), outcome.getTransactionId());
        database.markPendingProcessed(id, "PROCESSED");
        System.out.println("Demo persisted claim #" + id + " as " + outcome.getFinalStatus());
    }
}
