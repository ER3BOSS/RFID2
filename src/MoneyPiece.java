class MoneyPiece {

    private double moneyValue;
    private String moneyId;

    MoneyPiece(double value, String id) {
        moneyId = id;
        moneyValue = value;
    }

    double getMoneyValue() {
        return moneyValue;
    }

    String getMoneyId() {
        return moneyId;
    }
}
