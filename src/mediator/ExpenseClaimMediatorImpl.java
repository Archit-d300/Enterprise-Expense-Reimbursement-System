package mediator;

import java.util.Objects;

import approval.Chain;
import approval.ExpenseHandler;
import db.PendingExpense;
import factory.ExpenseFactory;
import model.ExpenseClaim;
import Notification.EmailChannel;
import Notification.NotificationChannel;
import Notification.NotificationDispatcher;
import observer.ExpenseClaimSubject;
import payment.PaymentRecord;
import payment.ReimbursementProcessor;
import policy.ExpensePolicyFactory;
import policy.PolicyDecision;
import policy.PolicyEvaluator;
import policy.User;
import policy.UserFactory;
import singleton.ExpenseManagementSystem;
import system.ClaimOutcome;

public class ExpenseClaimMediatorImpl implements ExpenseClaimMediator {

    private final ExpenseManagementSystem system;
    private final ExpenseClaimSubject subject;
    private final NotificationChannel notificationChannel;
    private final ExpenseHandler approvalChain;

    public ExpenseClaimMediatorImpl() {
        this.system = ExpenseManagementSystem.getInstance();
        this.subject = ExpenseClaimSubject.withDefaultObservers();
        this.notificationChannel = EmailChannel.fromEnvironment();
        this.approvalChain = Chain.createChain();
    }

    @Override
    public ClaimOutcome processClaim(PendingExpense originalPendingExpense) {
        PendingExpense pendingExpense = originalPendingExpense.cloneExpense();

        User user = UserFactory.createUser(
                pendingExpense.getRole(),
                String.valueOf(pendingExpense.getEmployeeId()),
                pendingExpense.getEmployeeName()
        );

        system.registerUser(user);

        ExpensePolicyFactory policyFactory = system.getPolicyFactory(pendingExpense.getRole());
        PolicyDecision decision = PolicyEvaluator.evaluate(policyFactory, pendingExpense.getAmount());

        System.out.println("Role-based policy [" + pendingExpense.getRole()
                + "] requires extra approval for this amount: " + decision.requiresApproval());

        ExpenseFactory expenseFactory = ExpenseFactory.forType(pendingExpense.getExpenseType());
        ExpenseClaim claim = expenseFactory.createExpense(
                pendingExpense.getEmployeeId(),
                pendingExpense.getAmount(),
                pendingExpense.getEmployeeEmail(),
                pendingExpense.getDescription()
        );
        claim.submit();

        subject.notifyObservers(claim, user);

        if (!decision.isAllowed()) {
            claim.setStatus("REJECTED");
            claim.setRemarks(decision.getReason());

            System.out.println(decision.getReason());

            subject.notifyObservers(claim, user);

            String finalStatus = NotificationDispatcher.REJECTED;
            String remarks = decision.getReason();

            NotificationDispatcher.dispatch(finalStatus, pendingExpense.getEmployeeEmail(), notificationChannel);

            return new ClaimOutcome(finalStatus, remarks, null);
        }

        boolean chainApproved = approvalChain.handle(claim);

        subject.notifyObservers(claim, user);

        if (chainApproved && "APPROVED".equals(claim.getStatus())) {
            System.out.println("Reimbursement Calculator (role policy) eligible amount: Rs "
                    + decision.getEligibleAmount());

            ReimbursementProcessor processor = new ReimbursementProcessor();
            PaymentRecord record = processor.processReimbursement(claim);

            subject.notifyObservers(claim, user);

            String transactionId = record.getTransactionId();
            String remarks = record.getReason();
            String finalStatus = "REIMBURSED".equals(claim.getStatus())
                    ? NotificationDispatcher.APPROVED_REIMBURSED
                    : NotificationDispatcher.PAYMENT_FAILED;

            NotificationDispatcher.dispatch(finalStatus, pendingExpense.getEmployeeEmail(), notificationChannel);

            return new ClaimOutcome(finalStatus, remarks, transactionId);
        }

        String finalStatus = NotificationDispatcher.REJECTED;
        String remarks = Objects.requireNonNullElse(claim.getRemarks(), "Rejected during approval chain.");

        NotificationDispatcher.dispatch(finalStatus, pendingExpense.getEmployeeEmail(), notificationChannel);

        return new ClaimOutcome(finalStatus, remarks, null);
    }
}