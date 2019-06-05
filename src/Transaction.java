import java.util.Date;

public class Transaction {
    double m_oldValue;
    double m_newValue;
    double m_diffrence;
    Date m_date;

    public Transaction(double oldValue, double newValue) {
        this.m_oldValue = oldValue;
        this.m_newValue = newValue;
        m_diffrence = m_oldValue - m_newValue;
        m_date = new Date();
    }
}
