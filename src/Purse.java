import java.util.ArrayList;

public class Purse {

    private String moneyOwner;
    private ArrayList<MoneyPiece> piecesInPurse = new ArrayList();

    public Purse(String owner, ArrayList<MoneyPiece> piecesInPurse){
        moneyOwner = owner;
        this.piecesInPurse = piecesInPurse;
    }

    public void calculateNewValue(){
        double m_purseValue = 0;
        for (MoneyPiece piece : piecesInPurse) {
            m_purseValue += piece.getMoneyValue();
        }
    }

}

