import java.util.ArrayList;

public class Purse {

    private double m_purseValue;
    private double oldValue;
    private ArrayList<MoneyPiece> piecesInPurse = new ArrayList<>();

    public Purse() {
    }

    void setPiecesInPurse(ArrayList<MoneyPiece> piecesInPurse) {
        this.piecesInPurse = piecesInPurse;
    }

    void calculateNewValue() {
        oldValue = m_purseValue;
        m_purseValue = 0;
        for (MoneyPiece piece : piecesInPurse) {
            m_purseValue += piece.getMoneyValue();
        }
        double transactionValue = (oldValue - m_purseValue) * -1;

        System.out.println("Alter Wert: " + oldValue + " ,, Jetziger Wert " + m_purseValue + ",, Veränderung: " + transactionValue);
    }

}

