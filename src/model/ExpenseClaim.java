package model;
import java.time.LocalDateTime;

public abstract class ExpenseClaim {
    private static int counter = 0;

    private final int id;
    private int employeeId;
    private double amount;
    private String status;
    private String email;
    private String description;
    private String remarks;
    private LocalDateTime timestamp; 

    public ExpenseClaim() {
        this.id = counter++;
        this.status = "CREATED";
    }

    public ExpenseClaim(int employeeId, double amount, String email, String description) {
        this.id = counter++; 
        this.employeeId = employeeId;
        this.amount = amount;
        this.email = email;
        this.description = description;
        this.status = "CREATED";
    }
    
    public void submit() {
        this.status = "SUBMITTED";
        this.timestamp = LocalDateTime.now();
    }

    public abstract String getType();

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getEmail() { return email; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRemarks() { return remarks; }

    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setEmail(String email) { this.email = email; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}