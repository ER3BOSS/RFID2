import java.util.Vector;

public class MainClass {
    public static void main(String[] args){
        Vector<MoneyPiece> mp = new Vector<>();
        for (int i = 0; i< 10; i++){
            MoneyPiece maa = new MoneyPiece(i+1,i);
            mp.add(maa);
        }
        Purse mainPurse = new Purse("Lukas", mp);
        mainPurse.calculateNewValue();
    }
}