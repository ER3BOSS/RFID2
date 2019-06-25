public class Main {
    public static void main(String[] args) {
        //Start the main routine in a Thread
        Runnable runnable = new CommunicationHandler();
        new Thread(runnable).start();
    }
}
