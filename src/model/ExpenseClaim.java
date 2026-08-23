package src.model;
import java.time.LocalDateTime;

public abstract class ExpenseClaim {
    private static int counter = 0;

    private final int id;
    private int employeeId;
    private double amount;
    private String status;
    private String email;
    private String description;
    private LocalDateTime timestamp; 

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

    public int getId() {return id;}

    public int getEmployeeId() { return employeeId;}

    public double getAmount() {return amount; }

    public String getStatus() { return status;}

    public String getEmail() { return email;}

    public String getDescription(){return description;}

    public void setStatus(String status) { 
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}