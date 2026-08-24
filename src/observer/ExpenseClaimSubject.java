package observer;

import java.util.ArrayList;
import java.util.List;
import model.ExpenseClaim;
import policy.User;
import policy.Manager;
import policy.Administrator;

public class ExpenseClaimSubject {
    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public static ExpenseClaimSubject withDefaultObservers() {
        ExpenseClaimSubject subject = new ExpenseClaimSubject();
        subject.addObserver(new EmployeeObserver());
        subject.addObserver(new ManagerObserver());
        subject.addObserver(new FinanceObserver());
        subject.addObserver(new ExecutiveObserver());
        return subject;
    }

    public void notifyObservers(ExpenseClaim claim, User submitter) {
        boolean isManager = submitter instanceof Manager;
        boolean isAdmin = submitter instanceof Administrator;

        for (Observer observer : observers) {
            if ((isManager || isAdmin) && observer instanceof EmployeeObserver) {
                continue; 
            }

            if ((isManager || isAdmin) && observer instanceof ManagerObserver) {
                continue; 
            }

            if (!isManager && !isAdmin && observer instanceof ExecutiveObserver) {
                continue; 
            }

            observer.update(claim);
        }
    }
}