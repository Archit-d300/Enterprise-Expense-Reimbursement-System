import approval.ApprovalMediator;
import approval.Chain;
import factory.ExpenseFactory;
import model.ExpenseClaim;
import model.ExpenseClaimBuilder;
import model.ExpenseLimits;
import notification.NotificationChannel;
import notification.NotificationDispatcher;
import payment.PaymentRecord;
import payment.PaymentServiceProxy;
import payment.Reimbursement;
import policy.EmployeeExpenseFactory;
import policy.ExecutiveExpenseFactory;
import policy.ManagerExpenseFactory;
import policy.PolicyDecision;
import policy.PolicyEvaluator;
import singleton.ExpenseManagementSystem;
import system.EmployeeReimbursementSystemProxy;
import system.EmployeeReimbursementSystem;
import system.ClaimOutcome;
import db.PendingExpense;
import db.DatabaseManager;
import report.DepartmentExpenseReport;
import report.EmployeeExpenseReport;
import report.FinanceExpenseReport;
import report.ReportRequest;

public final class PatternIntegrationTest {

    public static void main(String[] args) {
        testPolicyCapsAndApprovalThresholds();
        testMediatorAndChain();
        testBuilderUsesExpenseFactory();
        testPaymentProxyChecks();
        testPaymentFailureNotification();
        testRequestProxyValidation();
        testEndToEndProcessing();
        testSingletonIdentity();
        testPrototypeClone();
        testReportsAndPermissions();
        System.out.println("Pattern integration tests passed.");
    }

    private static void testPolicyCapsAndApprovalThresholds() {
        Object[][] roles = {
                { new EmployeeExpenseFactory(), 1000.0 },
                { new ManagerExpenseFactory(), 5000.0 },
                { new ExecutiveExpenseFactory(), 20000.0 }
        };

        for (Object[] role : roles) {
            PolicyDecision atCap = PolicyEvaluator.evaluate(
                    (policy.ExpensePolicyFactory) role[0], ExpenseLimits.MAX_APPROVABLE_AMOUNT);
            PolicyDecision aboveCap = PolicyEvaluator.evaluate(
                    (policy.ExpensePolicyFactory) role[0], ExpenseLimits.MAX_APPROVABLE_AMOUNT + 1);
            check(atCap.isAllowed(), "role policy must allow the shared cap");
            check(atCap.getEligibleAmount() == ExpenseLimits.MAX_APPROVABLE_AMOUNT,
                    "calculator must use the shared cap");
            check(!aboveCap.isAllowed(), "role policy must reject above the shared cap");
            check(!PolicyEvaluator.evaluate(
                    (policy.ExpensePolicyFactory) role[0], (double) role[1]).requiresApproval(),
                    "amount at approval threshold must not require extra approval");
            check(PolicyEvaluator.evaluate(
                    (policy.ExpensePolicyFactory) role[0], (double) role[1] + 1).requiresApproval(),
                    "amount above approval threshold must require extra approval");
        }
    }

    private static void testMediatorAndChain() {
        ExpenseClaim noExtraApproval = claim(1000);
        check(new ApprovalMediator().process(noExtraApproval, false),
                "claim below threshold should pass receipt validation and skip extra approvals");
        check("APPROVED".equals(noExtraApproval.getStatus()), "claim should reach approved status");

        ExpenseClaim needsApproval = claim(1001);
        check(new ApprovalMediator().process(needsApproval, true),
                "claim requiring approval should pass the chain");
        check("APPROVED".equals(needsApproval.getStatus()), "chain should finalize approval");

        ExpenseClaim overStageLimit = claim(ExpenseLimits.MAX_APPROVABLE_AMOUNT + 1);
        check(!Chain.createChain().handle(overStageLimit), "chain should reject above its manager limit");

        ExpenseClaim missingReceipt = new ExpenseClaimBuilder()
                .setEmployeeId(1)
                .setAmount(100)
                .setEmail("employee@example.com")
                .build();
        missingReceipt.submit();
        check(!new ApprovalMediator().process(missingReceipt, false),
                "receipt validation must run even when extra approval is skipped");
    }

    private static void testBuilderUsesExpenseFactory() {
        ExpenseClaim built = new ExpenseClaimBuilder()
                .setType("travel")
                .setEmployeeId(17)
                .setAmount(250)
                .setEmail("employee@example.com")
                .setReceiptReference("receipts/claim-17.pdf")
                .build();
        check("TRAVEL".equals(built.getType()), "builder should create requested expense type");

        expectThrows(IllegalArgumentException.class, () -> new ExpenseClaimBuilder()
                .setType("UNKNOWN")
                .setEmployeeId(1)
                .setAmount(10)
                .setEmail("employee@example.com")
                .build());
        expectThrows(IllegalArgumentException.class, () -> new ExpenseClaimBuilder()
                .setEmployeeId(1)
                .setAmount(Double.NaN)
                .setEmail("employee@example.com")
                .build());
        expectThrows(IllegalArgumentException.class, () -> ExpenseFactory.forType("OFFICE")
                .createExpense(1, Double.NaN, "a@example.com", "test"));
    }

    private static void testPaymentProxyChecks() {
        ExpenseClaim approved = claim(100);
        approved.setStatus("APPROVED");
        PaymentServiceProxy proxy = new PaymentServiceProxy();
        PaymentRecord valid = proxy.processPayment(new Reimbursement(approved, 100, true, true));
        check("SUCCESS".equals(valid.getStatus()), "valid approved reimbursement should pay");

        check("FAILED".equals(proxy.processPayment(
                new Reimbursement(approved, 100, false, true)).getStatus()),
                "ineligible employee should be rejected");
        check("FAILED".equals(proxy.processPayment(
                new Reimbursement(approved, 100, true, false)).getStatus()),
                "unauthorized reimbursement should be rejected");
        check("FAILED".equals(proxy.processPayment(
                new Reimbursement(approved, Double.NaN, true, true)).getStatus()),
                "non-finite reimbursement amount should be rejected");
    }

    private static void testPaymentFailureNotification() {
        CapturingChannel channel = new CapturingChannel();
        NotificationDispatcher.dispatch(NotificationDispatcher.PAYMENT_FAILED,
                "employee@example.com", channel);
        check(channel.message != null && channel.message.contains("could not be processed"),
                "payment failure should send payment-specific notification");
        check(!channel.message.contains("request has been rejected"),
                "payment failure must not be described as a claim rejection");
    }

    private static void testRequestProxyValidation() {
        CapturingChannel channel = new CapturingChannel();
        EmployeeReimbursementSystemProxy proxy = new EmployeeReimbursementSystemProxy(
                new EmployeeReimbursementSystem(channel));
        PendingExpense invalidType = new PendingExpense(
                1, 1, "Employee", "employee@example.com", "EMPLOYEE", "OTHER", 10, "test",
                "Engineering", "receipts/1.pdf");
        ClaimOutcome outcome = proxy.processClaim(invalidType);
        check("REJECTED".equals(outcome.getFinalStatus()),
                "proxy should reject unsupported expense types without throwing");
        PendingExpense missingReceipt = new PendingExpense(
                2, 1, "Employee", "employee@example.com", "EMPLOYEE", "OFFICE", 10, "test",
                "Engineering", "");
        ClaimOutcome missingReceiptOutcome = proxy.processClaim(missingReceipt);
        check("REJECTED".equals(missingReceiptOutcome.getFinalStatus())
                        && missingReceiptOutcome.getRemarks().contains("Receipt reference"),
                "mediator should reject a claim without an actual receipt reference");
    }

    private static void testEndToEndProcessing() {
        CapturingChannel channel = new CapturingChannel();
        EmployeeReimbursementSystem realSystem = new EmployeeReimbursementSystem(channel);
        EmployeeReimbursementSystemProxy proxy = new EmployeeReimbursementSystemProxy(realSystem);

        PendingExpense belowThreshold = new PendingExpense(
                2, 2, "Employee", "employee@example.com", "EMPLOYEE", "OFFICE", 1000, "stationery",
                "Engineering", "receipts/2.pdf");
        ClaimOutcome directApproval = proxy.processClaim(belowThreshold);
        check("APPROVED_REIMBURSED".equals(directApproval.getFinalStatus()),
                "claim below approval threshold should still be receipt-checked and reimbursed");
        check(directApproval.getTransactionId() != null,
                "successful end-to-end processing should return its transaction id");

        PendingExpense requiresChain = new PendingExpense(
                3, 3, "Employee", "employee@example.com", "EMPLOYEE", "FOOD", 2000, "team meal",
                "Engineering", "receipts/3.pdf");
        ClaimOutcome chainedApproval = proxy.processClaim(requiresChain);
        check("APPROVED_REIMBURSED".equals(chainedApproval.getFinalStatus()),
                "claim above approval threshold should pass the approval chain and be reimbursed");
        check(channel.message != null && channel.message.contains("processed successfully"),
                "successful end-to-end processing should dispatch reimbursement notification");

        PendingExpense missingReceipt = new PendingExpense(
                4, 4, "Employee", "employee@example.com", "EMPLOYEE", "OFFICE", 100, "no receipt",
                "Engineering", "");
        ClaimOutcome rejected = proxy.processClaim(missingReceipt);
        check("REJECTED".equals(rejected.getFinalStatus())
                        && rejected.getTransactionId() == null
                        && rejected.getRemarks().contains("Receipt reference"),
                "receipt-stage rejection must stop before payment and preserve its reason");
    }

    private static void testPrototypeClone() {
        PendingExpense source = new PendingExpense(
                4, 20, "Employee", "employee@example.com", "EMPLOYEE", "OFFICE", 25, "supplies",
                "Engineering", "receipts/4.pdf");
        PendingExpense clone = source.cloneExpense();
        check(clone != source && clone.getEmployeeId() == source.getEmployeeId()
                        && clone.getAmount() == source.getAmount()
                        && "Engineering".equals(clone.getDepartment())
                        && "receipts/4.pdf".equals(clone.getReceiptReference())
                        && clone.getId() == -1,
                "prototype should return a separate copy of the expense data");

        ExpenseClaim original = claim(300);
        original.setStatus("REIMBURSED");
        original.setRemarks("Old payment");
        ExpenseClaim duplicated = original.cloneExpense();
        check(duplicated.getId() != original.getId()
                        && "CREATED".equals(duplicated.getStatus())
                        && duplicated.getRemarks() == null
                        && duplicated.getAmount() == original.getAmount(),
                "claim prototype should copy reusable fields but reset its lifecycle state");
        duplicated.setAmount(400);
        check(original.getAmount() == 300, "editing the clone must not change the source claim");
    }

    private static void testSingletonIdentity() {
        check(ExpenseManagementSystem.getInstance() == ExpenseManagementSystem.getInstance(),
                "expense management system should return the same singleton instance");
    }

    private static void testReportsAndPermissions() {
        DatabaseManager database = new DatabaseManager(":memory:");
        database.connect();
        try {
            database.initializeSchema();
            PendingExpense engineering = new PendingExpense(
                    -1, 11, "Employee One", "one@example.com", "EMPLOYEE", "OFFICE", 125,
                    "engineering supplies", "Engineering", "receipts/11.pdf");
            int engineeringId = database.insertPending(engineering);
            PendingExpense storedEngineering = withId(engineering, engineeringId);
            database.insertProcessed(storedEngineering, "APPROVED_REIMBURSED", "paid", "TXN-11");
            database.markPendingProcessed(engineeringId, "PROCESSED");

            PendingExpense operations = new PendingExpense(
                    -1, 22, "Employee Two", "two@example.com", "EMPLOYEE", "FOOD", 75,
                    "operations lunch", "Operations", "receipts/22.pdf");
            int operationsId = database.insertPending(operations);
            PendingExpense storedOperations = withId(operations, operationsId);
            database.insertProcessed(storedOperations, "REJECTED", "policy rejection", null);
            database.markPendingProcessed(operationsId, "PROCESSED");

            String employee = new EmployeeExpenseReport().generateReport(
                    database, new ReportRequest(11, "EMPLOYEE", null));
            check(employee.contains("engineering supplies") && !employee.contains("operations lunch"),
                    "employee report must contain only the requester's records");

            String department = new DepartmentExpenseReport().generateReport(
                    database, new ReportRequest(900, "MANAGER", "Engineering"));
            check(department.contains("engineering supplies") && !department.contains("operations lunch"),
                    "department report must apply the requested department filter");
            expectThrows(SecurityException.class, () -> new DepartmentExpenseReport().generateReport(
                    database, new ReportRequest(11, "EMPLOYEE", "Engineering")));

            String finance = new FinanceExpenseReport().generateReport(
                    database, new ReportRequest(901, "FINANCE", null));
            check(finance.contains("engineering supplies") && !finance.contains("operations lunch")
                            && finance.contains("Total reimbursed: Rs 125.00"),
                    "finance report should summarize payment outcomes, not rejected claims");
            expectThrows(SecurityException.class, () -> new FinanceExpenseReport().generateReport(
                    database, new ReportRequest(11, "EMPLOYEE", null)));
            check(database.fetchProcessedExpenses().size() == 2,
                    "report generation must not modify stored records");
        } finally {
            database.close();
        }
    }

    private static PendingExpense withId(PendingExpense expense, int id) {
        return new PendingExpense(id, expense.getEmployeeId(), expense.getEmployeeName(),
                expense.getEmployeeEmail(), expense.getRole(), expense.getExpenseType(),
                expense.getAmount(), expense.getDescription(), expense.getDepartment(),
                expense.getReceiptReference());
    }

    private static ExpenseClaim claim(double amount) {
        ExpenseClaim claim = new ExpenseClaimBuilder()
                .setType("OFFICE")
                .setEmployeeId(1)
                .setAmount(amount)
                .setEmail("employee@example.com")
                .setReceiptReference("receipts/test.pdf")
                .build();
        claim.submit();
        return claim;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectThrows(Class<? extends Throwable> expected, Runnable action) {
        try {
            action.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("Expected " + expected.getSimpleName() + " but got " + actual, actual);
        }
        throw new AssertionError("Expected " + expected.getSimpleName() + " to be thrown.");
    }

    private static final class CapturingChannel implements NotificationChannel {
        private String message;

        @Override
        public void sendMessage(String to, String message) {
            this.message = message;
        }
    }
}
