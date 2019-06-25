import gnu.io.SerialPortEvent;
import java.io.IOException;

class CustomSerialPortEventListener implements gnu.io.SerialPortEventListener {
    private CommunicationHandler communicationHandler;

    CustomSerialPortEventListener(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    //creates the necessary listener
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                incomingData();
                break;
            case SerialPortEvent.BI:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.FE:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            case SerialPortEvent.PE:
            case SerialPortEvent.RI:
            default:
        }
    }

    //gets called by the DATA_AVAILABLE Event
    private void incomingData() {
        try {
            byte[] data = new byte[150];
            int num;
            while (communicationHandler.inputStream.available() > 0) {
                num = communicationHandler.inputStream.read(data, 0, data.length);
                String response = new String(data, 0, num);
                if (response.length() > 16) { // if it is a valid Response (an ID is at least 16 Chars long)
                    ResponseParser.parseStringValue(ResponseParser.parseResponse(response), communicationHandler); //parse it!
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while trying to read incoming data");
        }
    }
}
