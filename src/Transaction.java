import java.util.Date;

public class Transaction {
    double oldValue;
    double newValue;
    double diffrence;
    Date date;

    public Transaction(double oldValue, double newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        diffrence = this.oldValue - this.newValue;
        date = new Date();
    }
}
