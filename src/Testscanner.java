import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

public class Testscanner {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Runnable runnable = new PaulTest(true);
        new Thread(runnable).start();
        System.out.println("main finished");
    }
}
