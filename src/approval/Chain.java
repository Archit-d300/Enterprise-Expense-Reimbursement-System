package approval;

public class Chain {

    public static ExpenseHandler createChain() {

        ExpenseHandler receipt = new ReceiptValidationHandler();
        ExpenseHandler manager = new ManagerApprovalHandler();
        ExpenseHandler department = new DepartmentApprovalHandler();
        ExpenseHandler finance = new FinanceVerificationHandler();
        ExpenseHandler finalApproval = new FinalApprovalHandler();

        receipt.setNext(manager);
        manager.setNext(department);
        department.setNext(finance);
        finance.setNext(finalApproval);

        return receipt;
    }

}