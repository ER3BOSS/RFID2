public class MoneyPiece {

    double moneyValue;
    String moneyId;

    public MoneyPiece(double value, String id){
        moneyId = id;
        moneyValue = value;
    }

    public double getMoneyValue() {
        return moneyValue;
    }
}
