import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private static final Integer PORT = 6101;
    private static final String PROXYSERVER = "127.0.0.1";
    private static HTTPProxy proxy;

    /*
     * define our proxy
     */
    public static void main(String[] args) {
        proxy = new HTTPProxy(PORT, PROXYSERVER);
        System.out.println("Start Proxy: " + proxy.getIPAddr() + "(server) " + proxy.getportNo() + "(port)");
        proxy.Start();

    }
}
