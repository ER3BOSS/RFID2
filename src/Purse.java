import java.util.ArrayList;

public class Purse {

    private String moneyOwner;
    private ArrayList<MoneyPiece> piecesInPurse = new ArrayList();
    double m_purseValue;
    double oldValue;

    public Purse(String owner, ArrayList<MoneyPiece> piecesInPurse) {
        moneyOwner = owner;
        this.piecesInPurse = piecesInPurse;
        oldValue = 0;
    }

    public void setPiecesInPurse(ArrayList<MoneyPiece> piecesInPurse) {
        this.piecesInPurse = piecesInPurse;
    }

    public void calculateNewValue() {
        oldValue = m_purseValue;
        m_purseValue = 0;
        for (MoneyPiece piece : piecesInPurse) {
            m_purseValue += piece.getMoneyValue();
        }
        double ausgegeben = (oldValue - m_purseValue) * -1;

        System.out.println("Alter Wert: " + oldValue + " ,, Jetziger Wert " + m_purseValue + ",, Veränderung: " + ausgegeben);
    }

}

