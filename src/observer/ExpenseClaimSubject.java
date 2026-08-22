package observer;

import java.util.ArrayList;
import java.util.List;
import model.ExpenseClaim;
import policy.User;
import policy.Manager;


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

    public void notifyObservers(ExpenseClaim claim, User submitter) {
        boolean isManager = submitter instanceof Manager;

        for (Observer observer : observers) {
            if (isManager && observer instanceof EmployeeObserver) {
                continue; 
            }

            if (observer instanceof ManagerObserver) {
                ((ManagerObserver) observer).setManagerSubmitter(isManager);
            }

            observer.update(claim);
        }
    }
}
