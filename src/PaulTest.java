/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import static java.lang.Thread.sleep;

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
public class PaulTest implements Runnable {
    private boolean config;

    private CommPortIdentifier serialPortId;
    private Enumeration enumComm;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Boolean serialPortGeoeffnet = false;

    private int baudrate = 9600;
    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parity = SerialPort.PARITY_NONE;
    private String portName = "/dev/ttyS0";

    static private byte STX = 0x02;
    static private byte ETX = 0x03;

    private String result;

    //@param config: true for run forever, false for stop
    PaulTest(boolean config) {
        this.config = config;
        oeffneSerialPort(portName);

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
        sleep(2000);
        //lese gescannte Tags aus
        sendeSerialPort("6C21");

        sleep(2000);
        sleep(2000);

        uids = outputStream.toString();
        uids = uids.substring(8);
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(PaulTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        //uids = result;
        System.out.println(uids);
        return uids;
    }

    private void serialPortDatenVerfuegbar() {
        System.out.println("hier");
        if (config) {
            System.out.println("jup");
            try {
                byte[] data = new byte[150];
                int num;
                while (inputStream.available() > 0) {
                    System.out.println("jipi");
                    num = inputStream.read(data, 0, data.length);
                    result = new String(data, 0, num);
                    System.out.println("Empfange: " + new String(data, 0, num));
                }
            } catch (IOException ex) {
                Logger.getLogger(PaulTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setConfig(boolean config) {
        this.config = config;
    }

    private boolean oeffneSerialPort(String portName) {
        Boolean foundPort = false;
        /*if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geÃ¶ffnet");
            return false;
        }
        System.out.println("Ã–ffne Serialport");*/
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            foundPort = true;
            break;
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
            serialPort.addEventListener(new PaulTest.serialPortEventListener());
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

    private void sendeSerialPort(String nachricht) {
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
    }


    private static byte[] calcScemtecFullCmd(byte[] cmd) {
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
    }

    private static byte calcScemtecCRC(byte[] bArr) {
        byte crc = 0x0; // initialize CRC
        for (int i = 0; i < bArr.length; i++) {
            crc ^= bArr[i]; // XOR
        }
        System.out.println(crc);
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
}
