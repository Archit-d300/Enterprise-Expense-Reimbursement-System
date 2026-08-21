package observer;

import java.util.ArrayList;
import java.util.List;
import model.ExpenseClaim;

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

    public void notifyObservers(ExpenseClaim claim) {
        for (Observer o : observers) {
            o.update(claim);
        }
    }
}
