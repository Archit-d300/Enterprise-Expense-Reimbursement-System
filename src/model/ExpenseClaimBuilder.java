package model;

import factory.ExpenseFactory;
import java.util.Locale;

public class ExpenseClaimBuilder {
    private String type = "OFFICE";
    private int employeeId;
    private double amount;
    private String email = "";
    private String description = "N/A";
    private String remarks;
    private String receiptReference;
    private String department = "GENERAL";
    private String role = "EMPLOYEE";

    public ExpenseClaimBuilder setType(String type) {
        if (type != null) {
            this.type = type.trim().toUpperCase(Locale.ROOT);
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

    public ExpenseClaimBuilder setReceiptReference(String receiptReference) {
        this.receiptReference = receiptReference;
        return this;
    }

    public ExpenseClaimBuilder setDepartment(String department) {
        this.department = department;
        return this;
    }

    public ExpenseClaimBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public ExpenseClaim build() {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be greater than 0.");
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Expense amount must be finite and greater than 0.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee email is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Expense description is required.");
        }
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee role is required.");
        }

        ExpenseClaim claim = ExpenseFactory.forType(type)
                .createExpense(employeeId, amount, email, description);

        if (this.remarks != null) {
            claim.setRemarks(this.remarks);
        }
        claim.setReceiptReference(receiptReference);
        claim.setDepartment(department.trim());
        claim.setRole(role.trim().toUpperCase(Locale.ROOT));

        return claim;
    }
}