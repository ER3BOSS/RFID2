/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import static java.lang.Thread.sleep;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;


/**
 * @author Paul
 */
public class CommunicationHandler implements Runnable {
    private boolean config;

    private CommPortIdentifier serialPortId;
    private Enumeration enumComm;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Boolean serialPortGeoeffnet = false;

    private int baudrate = 115200;
    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;
    private String portName = TestClass.test();

    Purse purse = new Purse("Lukas", new ArrayList<MoneyPiece>());
    ArrayList<MoneyPiece> mpArray = new ArrayList<>();
    static private byte STX = 0x02;
    static private byte ETX = 0x03;

    private String result;

    //@param config: true for run forever, false for stop
    CommunicationHandler(boolean config) {
        this.config = config;
        oeffneSerialPort(portName);
        String s1 = "9941C34C000104E0";
        String s2 = "4341C34C000104E0";
        String s3 = "4441C34C000104E0";
        String s4 = "9941C34C000104E0";
        String s5 = "DC40C34C000104E0";
        mpArray.add(new MoneyPiece(500, s1));
        mpArray.add(new MoneyPiece(20, s2));
        mpArray.add(new MoneyPiece(2, s3));
        mpArray.add(new MoneyPiece(5, s4));
        mpArray.add(new MoneyPiece(1, s5));


    }



    @Override
    public void run() {
        while (config) {
            try {
                scan();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public String scan() throws InterruptedException {
        String uids = null;
        //warten ist nÃ¶tig, damit der Scemtek arbeiten kann
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
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("uids" + uids);
        return uids;
    }

    public void serialPortDatenVerfuegbar() {
        try {
            byte[] data = new byte[150];
            int num;
            while (inputStream.available() > 0) {
                num = inputStream.read(data, 0, data.length);
                System.out.println("Empfange: " + new String(data, 0, num));
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
        }
    }


    public void setConfig(boolean config) {
        this.config = config;
    }

    private boolean oeffneSerialPort(String portName) {
        Boolean foundPort = false;
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geÃ¶ffnet");
            return false;
        }
        System.out.println("Ã–ffne Serialport");
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
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
            serialPort = (SerialPort) serialPortId.open("Ã–ffnen und Senden", 500);
            System.out.println(serialPort.toString());
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
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf InputStream");
        }

        try {
            serialPort.addEventListener(new serialPortEventListener());
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException fÃ¼r Serialport");
        }
        serialPort.notifyOnDataAvailable(true);

        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
        }

        serialPortGeoeffnet = true;
        return true;
    }

    private void schliesseSerialPort() {
        if (serialPortGeoeffnet == true) {
            System.out.println("SchlieÃŸe Serialport");
            serialPort.close();
            serialPortGeoeffnet = false;
        } else {
            System.out.println("Serialport bereits geschlossen");
        }
    }

    /*private void sendeSerialPort(String nachricht) {
        byte[] fullCmd = calcScemtecFullCmd(nachricht.getBytes());
        System.out.println("Sende: " + nachricht);
        if (serialPortGeoeffnet != true)
            return;
        try {
            //outputStream.write(nachricht.getBytes());
            outputStream.write(fullCmd);
        } catch (IOException e) {
            System.out.println("Fehler beim Senden");
        }
    }*/

    void sendeSerialPort(String nachricht) {


        System.out.println("Sende: " + nachricht);

        //nachricht= nachricht +"\r";
        System.out.println("String nachricht: " + nachricht);
        byte[] cmd1 = nachricht.getBytes(StandardCharsets.US_ASCII);
        System.out.println("Bytes nachricht" + nachricht.getBytes());
        byte[] cmd = calcScemtecFullCmd(cmd1);

        if (serialPortGeoeffnet != true)
            return;
        try {

            outputStream.write(cmd);
            outputStream.flush();
            System.out.println("Sende: " + cmdToDecString(cmd));
        } catch (IOException e) {
            System.out.println("Fehler beim Senden");
        }
    }

    public static byte[] calcScemtecFullCmd(byte[] cmd) {

        byte bArr[] = new byte[cmd.length + 2]; // STX, cmd, ETX

        byte STX = 0x02;
        byte ETX = 0x03;

        bArr[0] = STX; // start with STX

        for (int i = 0; i < cmd.length; i++) {
            bArr[i + 1] = cmd[i]; // fill after STX
        }

        bArr[cmd.length + 1] = ETX; // end with ETX
        byte crc = calcScemtecCRC(bArr); // get CRC

        // new array with CRC
        byte bArr2[] = new byte[bArr.length + 1]; // STX, cmd, ETX, CRC

        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[i]; // copy
        }

        bArr2[bArr.length] = crc;
        //bArr2[bArr.length+1]=13;

        return bArr2;
    }

   /*private static byte[] calcScemtecFullCmd(byte[] cmd) {
        byte bArr[] = new byte[cmd.length + 2]; // STX, cmd, ETX
        bArr[0] = STX; // start with STX
        for (int i = 0; i < cmd.length; i++) {
            bArr[i + 1] = cmd[i]; // fill after STX
        }
        bArr[cmd.length + 1] = ETX; // end with ETX
        byte crc = calcScemtecCRC(bArr); // get CRC
        // new array with CRC
        byte bArr2[] = new byte[bArr.length + 1]; // STX, cmd, ETX, CRC
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[i]; // copy
        }
        bArr2[bArr.length] = crc;
        return bArr2;
    }*/

    private static byte calcScemtecCRC(byte[] bArr) {
        byte crc = 0x0; // initialize CRC
        for (int i = 0; i < bArr.length; i++) {
            crc ^= bArr[i]; // XOR
        }
        return crc;
    }

    public static String cmdToHexString(byte[] cmd) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < cmd.length - 1; i++) {
            buf.append(String.format("%02X", cmd[i]) + ",");
        }
        buf.append(String.format("%02X", cmd[cmd.length - 1]));
        return buf.toString();
    }

    class serialPortEventListener implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            System.out.println("serialPortEventlistener");
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
    }

    public static String cmdToDecString(byte[] cmd) {

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < cmd.length - 1; i++) {

            buf.append(String.format("%03d", cmd[i]) + ",");

        }
        buf.append(String.format("%03d", cmd[cmd.length - 1]));

        return buf.toString();
    }
}
