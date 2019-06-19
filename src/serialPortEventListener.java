import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class serialPortEventListener implements SerialPortEventListener {

    InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };

    public serialPortEventListener(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void serialEvent(SerialPortEvent event) {
        //System.out.println("serialPortEventlistener");
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                serialPortDatenVerfuegbar();
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

    private void serialPortDatenVerfuegbar() {
        try {
            byte[] data = new byte[150];
            int num;
            while(inputStream.available() > 0) {
                num = inputStream.read(data, 0, data.length);
                System.out.println("Empfange: "+ new String(data, 0, num));
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
        }
    }
}