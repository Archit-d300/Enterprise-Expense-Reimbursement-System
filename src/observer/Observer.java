package observer;

import model.ExpenseClaim;

public interface Observer {
    void update(ExpenseClaim claim);
}
