import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import static java.lang.Thread.sleep;

public class CommunicationHandler implements Runnable {
    Purse purse = new Purse();
    ArrayList<MoneyPiece> mpArray = new ArrayList<>();
    InputStream inputStream;
    private CommPortIdentifier serialPortId;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private Boolean serialPortOpen = false;

    CommunicationHandler() {
        //should return the correct portName automatically
        String portName = ConnectionHandler.getPortName();

        //error check
        if (portName == null){
            System.out.println("couldn't receive port name");
            return;
        }

        openSerialPort(portName);
        
        //create some initial money pieces here:
        mpArray.add(new MoneyPiece(500, "9841C34C000104E0"));
        mpArray.add(new MoneyPiece(20, "4341C34C000104E0"));
        mpArray.add(new MoneyPiece(2, "4441C34C000104E0"));
        mpArray.add(new MoneyPiece(5, "9941C34C000104E0"));
        mpArray.add(new MoneyPiece(1, "DC40C34C000104E0"));
    }

    //establish a valid connection to the serial port and set all initial parameters
    private void openSerialPort(String portName) {

        Boolean foundPort = false;

        if (serialPortOpen) {
            System.out.println("serial port already open");
            return;
        }

        System.out.println("open serial port");

        //Try to find the Port
        Enumeration enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (portName.contentEquals(serialPortId.getName())) {
                foundPort = true;
                break;
            }
        }
        if (!foundPort) {
            System.out.println("serial port not found: " + portName);
            return;
        }

        //check if Port is free
        try {
            serialPort = (SerialPort) serialPortId.open("open and send", 500);
            System.out.println(serialPort.toString());
        } catch (PortInUseException e) {
            System.out.println("port already taken");
        }

        //check OutputStream
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            System.out.println("OutputStream cant be accessed");
        }

        //check InputStream
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("InputStream cant be accessed");
        }

        //Try to attach the Event listener for incoming messages
        try {
            serialPort.addEventListener(new CustomSerialPortEventListener(this));
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException -> serial port");
        }

        //activate listener
        serialPort.notifyOnDataAvailable(true);

        try { // try to set serial port parameters
            int baudRate = 115200; //Todo: This may change depending on your device, a better implementation would retry with a few different rates
            int dataBits = SerialPort.DATABITS_8;
            int stopBits = SerialPort.STOPBITS_1;
            int parity = SerialPort.PARITY_NONE;
            serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("couldn't set serial port parameters");
        }

        serialPortOpen = true; //done
    }

    @Override
    public void run() {
        while (true) { //do as long as possible
            try {
                requestData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void requestData() throws InterruptedException {
        //Sleeps are important to allow for response time

        //create index
        sendCommandToSerial("6C20s");
        sleep(500);
        //request scanned tags
        sendCommandToSerial("6C21");

        sleep(3000);
    }

    private void sendCommandToSerial(String command) {
        byte[] cmd = command.getBytes(StandardCharsets.US_ASCII);

        //correct the format
        cmd = calcCmd(cmd);

        if (!serialPortOpen)
            return;
        try {
            outputStream.write(cmd); //send it away!
            outputStream.flush(); //clear the output stream
        } catch (IOException e) {
            System.out.println("an error occurred while trying to send a command");
        }
    }

    //creates the correct command
    private static byte[] calcCmd(byte[] cmd) {

        byte bArr[] = new byte[cmd.length + 2]; // STX, cmd, ETX

        byte STX = 0x02;
        byte ETX = 0x03;

        bArr[0] = STX; // start with STX

        // fill after STX
        System.arraycopy(cmd, 0, bArr, 1, cmd.length);

        bArr[cmd.length + 1] = ETX; // end with ETX
        byte crc = calcCRC(bArr); // get CRC

        // new array with CRC
        byte bArr2[] = new byte[bArr.length + 1]; // STX, cmd, ETX, CRC

        //copy
        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);

        bArr2[bArr.length] = crc;

        return bArr2;
    }

    //calculates the checksum
    private static byte calcCRC(byte[] bArr) {
        byte crc = 0x0; // initialize CRC
        for (byte aBArr : bArr) {
            crc ^= aBArr; // XOR
        }
        return crc;
    }
}
