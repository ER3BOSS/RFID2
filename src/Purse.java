import java.util.ArrayList;

public class Purse {

    String m_owner;
    double m_purseValue;
    ArrayList<MoneyPiece> m_piecesInPurse = new ArrayList();

    public Purse(String owner, ArrayList<MoneyPiece> piecesInPurse){
        m_owner = owner;
        m_piecesInPurse = piecesInPurse;


    }

    public void calculateNewValue(){
        m_purseValue = 0;
        for (MoneyPiece m :m_piecesInPurse) {
            m_purseValue += m.getM_value();
        }
    }

}

