public class Testscanner {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Runnable runnable = new CommunicationHandler(true);
        new Thread(runnable).start();
        System.out.println("main finished");
    }
}
