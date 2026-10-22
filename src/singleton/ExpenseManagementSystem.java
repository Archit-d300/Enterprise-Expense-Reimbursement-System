package singleton;

import policy.*;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class ExpenseManagementSystem {
    private final Map<String, User> userRegistry = new ConcurrentHashMap<>();

    private ExpenseManagementSystem() {

    }

    public static ExpenseManagementSystem getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class InstanceHolder {
        private static final ExpenseManagementSystem INSTANCE = new ExpenseManagementSystem();
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
        
        switch (role.toUpperCase(Locale.ROOT)) {
            case "MANAGER":
            case "FINANCE":
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