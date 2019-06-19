
import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;


import static java.lang.Thread.sleep;
//import java.util.TooManyListenersException;

//import OeffnenUndSenden.serialPortEventListener;



public class EinfachSenden implements Runnable {

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Runnable runnable = new EinfachSenden();

        new Thread(runnable).start();

    }

    /**
     *
     */

    CommPortIdentifier serialPortId;
    Enumeration enumComm;
    SerialPort serialPort;
    OutputStream outputStream;
    // InputStream inputStream;
    Boolean serialPortGeoeffnet = false;

    int baudrate = 115200;
    int dataBits = SerialPort.DATABITS_8;
    int stopBits = SerialPort.STOPBITS_1;
    int parity = SerialPort.PARITY_NONE;
    String portName = TestClass.test();

    static private byte STX = 0x02;
    static private byte ETX = 0x03;

    InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };

    int secondsRuntime = 20;

    public EinfachSenden()
    {
        System.out.println("Konstruktor: EinfachSenden");
    }

    public void run()
    {
        Integer secondsRemaining = secondsRuntime;
        if (!oeffneSerialPort(portName))
            return;

        sendeSerialPort("F00001");

        while (secondsRemaining > 0) {
            System.out.println("Sekunden verbleiben: " + secondsRemaining.toString() );
            secondsRemaining--;
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) { }
            //sendMsgToSerial("Testnachricht\n");
            scan();
        }
        schliesseSerialPort();
    }

    public String scan()
    {
        String uids = null;
        try {
            //warten ist nötig, damit der Scemtek arbeiten kann
            //sleep(1000);
            //erstelle Index
            sendeSerialPort("6C20s");
            sleep(500);
            //lese gescannte Tags aus
            sendeSerialPort("6C21");

            sleep(500);
            sleep(2000);

            uids = outputStream.toString();
            uids = uids.substring(8);
        } catch (InterruptedException ex) {
            Logger.getLogger(EinfachSenden.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(EinfachSenden.class.getName()).log(Level.SEVERE, null, ex);
        }
        //uids = result;
        System.out.println(uids);
        return uids;
    }

    private void sendeSerialPort(String nachricht)
    {
        byte[] fullCmd = calcScemtecFullCmd(nachricht.getBytes());
        System.out.println("Sende: " + nachricht);
        if (!serialPortGeoeffnet)
            return;
        try {
            //outputStream.write(nachricht.getBytes());
            outputStream.write(fullCmd);
        } catch (IOException e) {
            System.out.println("Fehler beim Senden");
        }
    }


    private static byte[] calcScemtecFullCmd( byte[] cmd )
    {
        byte bArr[] = new byte[cmd.length + 2]; // STX, cmd, ETX
        bArr[0] = STX; // start with STX
        for (int i = 0; i < cmd.length; i++ ) {
            bArr[i+1] = cmd[i]; // fill after STX
        }
        bArr[cmd.length + 1] = ETX; // end with ETX
        byte crc = calcScemtecCRC( bArr ); // get CRC
        // new array with CRC
        byte bArr2[] = new byte[bArr.length + 1]; // STX, cmd, ETX, CRC
        for (int i = 0; i < bArr.length; i++ ) {
            bArr2[i] = bArr[i]; // copy
        }
        bArr2[bArr.length] = crc;
        return bArr2;
    }

    boolean oeffneSerialPort(String portName)
    {
        Boolean foundPort = false;
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geöffnet");
            return false;
        }
        System.out.println("Öffne Serialport");
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while(enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (portName.contentEquals(serialPortId.getName())) {
                foundPort = true;
                break;
            }
        }
        if (foundPort != true) {
            System.out.println("Serialport nicht gefunden: " + portName);
            return false;
        }
        try {
            serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
        } catch (PortInUseException e) {
            System.out.println("Port belegt");
        }
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf OutputStream");
        }
		try {
			inputStream = serialPort.getInputStream();
            serialPort.addEventListener(new serialPortEventListener(inputStream));
		} catch (IOException e) {
			System.out.println("Keinen Zugriff auf InputStream");
		} catch (TooManyListenersException e) {
            e.printStackTrace();
        }
       /* try {
			serialPort.addEventListener(new serialPortEventListener());
		} catch (TooManyListenersException e) {
			System.out.println("TooManyListenersException für Serialport");
		} */
		serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        } catch(UnsupportedCommOperationException e) {
            System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
        }

        serialPortGeoeffnet = true;
        return true;
    }

    void schliesseSerialPort()
    {
        if (serialPortGeoeffnet) {
            System.out.println("Schließe Serialport");
            serialPort.close();
            serialPortGeoeffnet = false;
        } else {
            System.out.println("Serialport bereits geschlossen");
        }
    }

    private static byte calcScemtecCRC( byte[] bArr )
    {
        byte crc = 0x0; // initialize CRC
        for (int i = 0; i < bArr.length; i++ ) {
            crc = (byte)(0xff & ((int)crc) ^ ((int)bArr[i]));
        }
        return crc;
    }

    public static String cmdToHexString( byte[] cmd )
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < cmd.length - 1; i++ )
        {
            buf.append(String.format( "%02X", cmd[i] ) + "," );
        }
        buf.append( String.format( "%02X", cmd[cmd.length - 1] ) );
        return buf.toString();
    }

    private void sendMsgToSerial(String nachricht)
    {
        System.out.println("Sende: " + nachricht);
        if (!serialPortGeoeffnet)
            return;
        try {
            outputStream.write(nachricht.getBytes());
        } catch (IOException e) {
            System.out.println("Fehler beim Senden");
        }
    }

}