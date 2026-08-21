package approval;
import model.ExpenseClaim;
public abstract class ExpenseHandler {
    protected ExpenseHandler next;
    
    public void setNext(ExpenseHandler next){
        this.next = next;
    }

    public abstract boolean handle(ExpenseClaim claim);

    }

