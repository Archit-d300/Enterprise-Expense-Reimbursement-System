package model;

public class ExpenseClaimBuilder {
    private String type = "OFFICE";
    private int employeeId;
    private double amount;
    private String email = "";
    private String description = "N/A";
    private String remarks;
    private String status;

    public ExpenseClaimBuilder setType(String type) {
        if (type != null) {
            this.type = type.toUpperCase().trim();
        }
        return this;
    }

    public ExpenseClaimBuilder setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public ExpenseClaimBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseClaimBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public ExpenseClaimBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ExpenseClaimBuilder setRemarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public ExpenseClaimBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public ExpenseClaim build() {
        ExpenseClaim claim;

        switch (type) {
            case "FOOD":
                claim = new FoodExpense(employeeId, amount, email, description);
                break;
            case "TRAVEL":
                claim = new TravelExpense(employeeId, amount, email, description);
                break;
            case "ACCOMMODATION":
                claim = new AccommodationExpense(employeeId, amount, email, description);
                break;
            case "OFFICE":
            default:
                claim = new OfficeExpense(employeeId, amount, email, description);
                break;
        }

        if (this.remarks != null) {
            claim.setRemarks(this.remarks);
        }
        if (this.status != null) {
            claim.setStatus(this.status);
        }

        return claim;
    }
}