package model;

public abstract class User {
    private String id;
    private String name;
    private String role;

    public User(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id; 
    }
    public String getName() {
        return name; 
    }
    public String getRole() {
        return role; 
    }

    @Override
    public String toString() {
        return "User id: "+getId()+" Username: "+getName()+" Role: "+getRole();
    }
}