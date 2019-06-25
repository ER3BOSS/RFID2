import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResponseParser {
    //parses the a String Array of IDs and calculates their representative money value
    static void parseStringValue(ArrayList<String> arrayList, CommunicationHandler communicationHandler) throws IOException {
        ArrayList<MoneyPiece> finalPieces = new ArrayList<>();
        finalPieces.clear();
        boolean mpFound = false;
        Double value = .0;
        for (String s : arrayList) {
            for (MoneyPiece mp : communicationHandler.mpArray) {
                String moneyID = mp.getMoneyId();
                if (moneyID.equals(s)) {
                    finalPieces.add(mp);
                    mpFound = true;
                }
            }
            while (!mpFound) {
                System.out.println("Ein neuer Schein wurde gefunden mit der ID: " + s);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                try {
                    System.out.println("Wie ist der Wert des Scheins?");
                    value = Double.parseDouble(br.readLine());
                    mpFound = true;
                } catch (NumberFormatException ignored) {
                }
            }
            communicationHandler.mpArray.add(new MoneyPiece(value, s));
        }
        communicationHandler.purse.setPiecesInPurse(finalPieces);
        communicationHandler.purse.calculateNewValue();
    }

    //parses the a given String for the containing IDs
    static ArrayList<String> parseResponse(String response) {
        ArrayList<String> idList = new ArrayList<>();
        if (response.contains("6C21")) { //remove the command echo from the response
            response = response.substring(6);
        }
        int count = Integer.parseInt(response.substring(0, 4)); //get the number of found IDs
        response = response.substring(4); //remove the count from the string

        //parse String for IDs (each ID is 16 Chars long)
        for (int i = 0; i < count; i++) {
            String id = response.substring(0, 16);
            response = response.substring(16); //remove the ID you just parsed from the String
            idList.add(id);
        }

        return idList;
    }
}
