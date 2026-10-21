package approval;

import java.util.List;
import model.ExpenseClaim;

public final class ApprovalMediator {

    private final List<Stage> stages;
    private boolean approved;

    public ApprovalMediator() {
        stages = List.of(
                new ReceiptStage(this),
                new ManagerStage(this),
                new DepartmentStage(this),
                new FinanceStage(this),
                new FinalStage(this));
    }

    public boolean process(ExpenseClaim claim) {
        approved = false;
        stages.get(0).review(claim);
        return approved;
    }

    void stagePassed(Stage sender, ExpenseClaim claim) {
        int next = stages.indexOf(sender) + 1;
        if (next < stages.size()) {
            stages.get(next).review(claim);   
        } else {
            approved = true;                  
        }
    }

    void stageRejected(Stage sender, ExpenseClaim claim) {
        approved = false;                
    }


    abstract static class Stage {
        protected final ApprovalMediator mediator;
        private final String name;

        Stage(ApprovalMediator mediator, String name) {
            this.mediator = mediator;
            this.name = name;
        }

        abstract void review(ExpenseClaim claim);

        protected void accept(ExpenseClaim claim, String newStatus) {
            claim.setStatus(newStatus);
            System.out.println(name + ": ACCEPTED");
        }

        protected void pass(ExpenseClaim claim) {
            mediator.stagePassed(this, claim);
        }

        protected void reject(ExpenseClaim claim, String reason) {
            claim.setStatus("REJECTED");
            claim.setRemarks(reason);
            System.out.println(name + ": REJECTED");
            System.out.println("Reason: " + reason);
            mediator.stageRejected(this, claim);
        }
    }

    static class ReceiptStage extends Stage {
        ReceiptStage(ApprovalMediator m) { super(m, "Receipt Validation"); }

        @Override
        void review(ExpenseClaim claim) {
            if (claim.getEmail() == null || claim.getEmail().trim().isEmpty()) {
                reject(claim, "Employee email/receipt information is missing.");
                return;
            }
            accept(claim, "RECEIPT_VALIDATED");
            pass(claim);
        }
    }

    static class ManagerStage extends Stage {
        ManagerStage(ApprovalMediator m) { super(m, "Manager Approval"); }

        @Override
        void review(ExpenseClaim claim) {
            if (claim.getAmount() <= 50000) {
                accept(claim, "MANAGER_APPROVED");
                System.out.println("Manager Approval: PASSED");
                pass(claim);
                return;
            }
            reject(claim, "Amount exceeds manager approval limit.");
        }
    }

    static class DepartmentStage extends Stage {
        DepartmentStage(ApprovalMediator m) { super(m, "Department Approval"); }

        @Override
        void review(ExpenseClaim claim) {
            if (claim.getAmount() <= 100000) {
                accept(claim, "DEPARTMENT_APPROVED");
                pass(claim);
                return;
            }
            reject(claim, "Claim exceeds department budget.");
        }
    }

    static class FinanceStage extends Stage {
        FinanceStage(ApprovalMediator m) { super(m, "Finance Verification"); }

        @Override
        void review(ExpenseClaim claim) {
            if (claim.getAmount() > 0 && claim.getAmount() <= 200000) {
                accept(claim, "FINANCE_VERIFIED");
                pass(claim);
                return;
            }
            reject(claim, "Claim failed finance verification.");
        }
    }

    static class FinalStage extends Stage {
        FinalStage(ApprovalMediator m) { super(m, "Final Approval"); }

        @Override
        void review(ExpenseClaim claim) {
            if ("FINANCE_VERIFIED".equals(claim.getStatus())) {
                accept(claim, "APPROVED");
                System.out.println("Claim #" + claim.getId() + " is now APPROVED.");
                pass(claim);
                return;
            }
            reject(claim, "Previous approval stages are not completed.");
        }
    }
}
