package singleton;

import policy.*;
import java.util.HashMap;
import java.util.Map;

public class ExpenseManagementSystem {
    private static ExpenseManagementSystem instance;
    private Map<String,User> userRegistry = new HashMap<>();

    private ExpenseManagementSystem() {

    }

    public static ExpenseManagementSystem getInstance() {
        if (instance == null) {
            instance = new ExpenseManagementSystem();
        }
        return instance;
    }

    public void registerUser(User user) {
        if (user != null) {
            userRegistry.put(user.getId(), user);
            System.out.println("Registered user "+user.getName()+" ("+user.getRole()+")");
        }
    }

    public ExpensePolicyFactory getPolicyFactory(String role) {
        if (role == null) 
            return new EmployeeExpenseFactory();
        
        switch (role.toUpperCase()) {
            case "MANAGER":
                return new ManagerExpenseFactory();
            case "EXECUTIVE":
            case "ADMIN":
                return new ExecutiveExpenseFactory();
            case "EMPLOYEE":
            default:
                return new EmployeeExpenseFactory();
        }
    }
}