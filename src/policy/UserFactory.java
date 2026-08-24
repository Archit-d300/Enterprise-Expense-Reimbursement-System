package policy;

public final class UserFactory {

    private UserFactory() {
        
    }

    public static User createUser(String role, String id, String name) {
        if (role == null) {
            return new Employee(id, name);
        }

        switch (role.toUpperCase()) {
            case "MANAGER":
                return new Manager(id, name);
            case "FINANCE":
                return new FinanceOfficer(id, name);
            case "ADMIN":
            case "EXECUTIVE":
                return new Administrator(id, name);
            case "EMPLOYEE":
            default:
                return new Employee(id, name);
        }
    }
}
