import java.util.Vector;

public class Purse {

    String m_owner;
    double m_purseValue;
    Vector<MoneyPiece> m_piecesInPurse;

    public Purse(String owner, Vector<MoneyPiece> piecesInPurse){
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
