import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

class ConnectionHandler {
    static String getPortName() {
        String serialName = null;
        CommPortIdentifier serialPortId;
        Enumeration enumComm;
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serialName = serialPortId.getName();
                System.out.println(serialPortId.getName());
            }
        }
        return serialName;
    }


}