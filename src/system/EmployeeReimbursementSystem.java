package system;

import model.ExpenseClaim;

import factory.ExpenseFactory;

import approval.ExpenseHandler;
import approval.Chain;

import payment.ReimbursementProcessor;
import payment.PaymentRecord;

import policy.User;
import policy.UserFactory;
import policy.ExpensePolicyFactory;
import policy.PolicyDecision;
import policy.PolicyEvaluator;

import observer.ExpenseClaimSubject;

import notification.NotificationChannel;
import notification.NotificationDispatcher;
import notification.EmailChannel;

import singleton.ExpenseManagementSystem;

import db.PendingExpense;


public class EmployeeReimbursementSystem implements ReimbursementSystem {

   
    private final ExpenseManagementSystem system;

    private final ExpenseClaimSubject subject;

    private final NotificationChannel notificationChannel;

    public EmployeeReimbursementSystem() {
        this.system = ExpenseManagementSystem.getInstance();
        this.subject = ExpenseClaimSubject.withDefaultObservers();
        this.notificationChannel = EmailChannel.fromEnvironment();
    }

    @Override
    public ClaimOutcome processClaim(PendingExpense pe) {

        User user = UserFactory.createUser(pe.getRole(), String.valueOf(pe.getEmployeeId()), pe.getEmployeeName());
        system.registerUser(user);

        ExpensePolicyFactory policyFactory = system.getPolicyFactory(pe.getRole());
        PolicyDecision decision = PolicyEvaluator.evaluate(policyFactory, pe.getAmount());
        System.out.println("Role-based policy [" + pe.getRole() + "] requires extra approval for this amount: "
                + decision.requiresApproval());

        ExpenseFactory expenseFactory = ExpenseFactory.forType(pe.getExpenseType());
        ExpenseClaim claim = expenseFactory.createExpense(pe.getEmployeeId(), pe.getAmount(), pe.getEmployeeEmail(), pe.getDescription());
        claim.submit();

        String finalStatus;
        String remarks;
        String transactionId = null;

        if (!decision.isAllowed()) {
            claim.setStatus("REJECTED");
            claim.setRemarks(decision.getReason());
            System.out.println(decision.getReason());

            subject.notifyObservers(claim, user);
            finalStatus = NotificationDispatcher.REJECTED;
            remarks = decision.getReason();

        } else {
            ExpenseHandler chain = Chain.createChain();
            boolean chainApproved = chain.handle(claim);

            subject.notifyObservers(claim, user);

            if (chainApproved && "APPROVED".equals(claim.getStatus())) {

                System.out.println("Reimbursement Calculator (role policy) eligible amount: Rs " + decision.getEligibleAmount());

                ReimbursementProcessor processor = new ReimbursementProcessor();
                PaymentRecord record = processor.processReimbursement(claim);

                subject.notifyObservers(claim, user);

                transactionId = record.getTransactionId();
                remarks = record.getReason();
                finalStatus = "REIMBURSED".equals(claim.getStatus())
                        ? NotificationDispatcher.APPROVED_REIMBURSED
                        : NotificationDispatcher.PAYMENT_FAILED;

            } else {
                finalStatus = NotificationDispatcher.REJECTED;
                remarks = claim.getRemarks() != null ? claim.getRemarks() : "Rejected during approval chain.";
            }
        }

      
        NotificationDispatcher.dispatch(finalStatus, pe.getEmployeeEmail(), notificationChannel);

        return new ClaimOutcome(finalStatus, remarks, transactionId);
    }
}
