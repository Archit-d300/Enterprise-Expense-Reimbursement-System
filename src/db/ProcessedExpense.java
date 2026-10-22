package db;

public final class ProcessedExpense {
    private final int pendingId;
    private final int employeeId;
    private final String employeeName;
    private final String role;
    private final String department;
    private final String expenseType;
    private final double amount;
    private final String description;
    private final String finalStatus;
    private final String remarks;
    private final String transactionId;
    private final String processedAt;

    public ProcessedExpense(int pendingId, int employeeId, String employeeName, String role,
                            String department, String expenseType, double amount, String description,
                            String finalStatus, String remarks, String transactionId, String processedAt) {
        this.pendingId = pendingId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.role = role;
        this.department = department;
        this.expenseType = expenseType;
        this.amount = amount;
        this.description = description;
        this.finalStatus = finalStatus;
        this.remarks = remarks;
        this.transactionId = transactionId;
        this.processedAt = processedAt;
    }

    public int getPendingId() { return pendingId; }
    public int getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getExpenseType() { return expenseType; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getFinalStatus() { return finalStatus; }
    public String getRemarks() { return remarks; }
    public String getTransactionId() { return transactionId; }
    public String getProcessedAt() { return processedAt; }
}
