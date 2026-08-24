package db;

public class PendingExpense {

    private final int id;
    private final int employeeId;
    private final String employeeName;
    private final String employeeEmail;
    private final String role;
    private final String expenseType;
    private final double amount;
    private final String description;

    public PendingExpense(int id, int employeeId, String employeeName, String employeeEmail,
                           String role, String expenseType, double amount, String description) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.role = role;
        this.expenseType = expenseType;
        this.amount = amount;
        this.description = description;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeEmail() { return employeeEmail; }
    public String getRole() { return role; }
    public String getExpenseType() { return expenseType; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "PendingExpense{id=" + id + ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", role='" + role + '\'' +
                ", expenseType='" + expenseType + '\'' +
                ", amount=" + amount + '}';
    }
}
