import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Main {

    //DNS cache
    private static HashMap<String, String> dnsCache = new HashMap<String, String>();

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            System.out.println("Proxy's server is running");
            new HttpProxy(new ServerSocket(5513));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getDnsCache() {
        return dnsCache;
    }

}

class HttpProxy extends Thread {
    private ServerSocket severSock;

    public HttpProxy(ServerSocket httpProxySock) {
        severSock = httpProxySock;
        System.out.println("Proxy's server is listening on Port " + severSock.getLocalPort() + "\n");
        start();
    }

    public void run() {
        while (true) {
            try {
                new ProxyProcess(severSock.accept());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

class ProxyProcess extends Thread {
    private Socket clientSock;

    public ProxyProcess(Socket serverSock) throws IOException {
        //bind the socket to the Server
        clientSock = serverSock;
        start();
    }

    public void run() {
        byte[] buff = new byte[10000];
        String request = null;
        String serverIP = null;
        int len;
        try {
            System.out.println("Client port is " + clientSock.getPort());
            //get the stream from the client and put it in fromClient.
            DataInputStream fromClientStream = new DataInputStream(clientSock.getInputStream());
            //send the stream to client
            DataOutputStream toClientStream = new DataOutputStream(clientSock.getOutputStream());
            if (fromClientStream != null && toClientStream != null) {
                len = fromClientStream.read(buff, 0, 10000);
                //get the destination URL
                if (len > 0) {
                    request = new String(buff);
                    if (request.indexOf("http:") == -1 || request.indexOf("HTTP/1.1") == -1)
                        return;
                    request = request.substring(request.indexOf("http:") + 7, request.indexOf("HTTP/1.1") - 1);
                    if (request.indexOf("/") != -1)
                        request = request.substring(0, request.indexOf("/"));
                    System.out.println("request url is " + request);
                    Socket socketToWeb;
                    //find dns in localDNS cache
                    if (LocalDNSCache.existsIP(request) == true) {
                        serverIP = LocalDNSCache.getDNS(request);
                        //Proxy's client. Connect to destiantion URL on port 80
                        socketToWeb = new Socket(serverIP, 80);
                        System.out.println("IP in Local DNS cache " + request + " : " + serverIP);
                    } else {
                        //if not in localDNS cache, then use the url to access the web directly.
                        socketToWeb = new Socket(request, 80);
                        //socketToWeb contains the ip, then put it in DNS cache
                        LocalDNSCache.storeIP(request, socketToWeb.getInetAddress().toString());
                    }

                    //send the stream to the server
                    DataOutputStream toWebStream = new DataOutputStream(socketToWeb.getOutputStream());
                    //get the stream from the server
                    DataInputStream fromWebStream = new DataInputStream(socketToWeb.getInputStream());
                    //from proxy to web server
                    if (toWebStream != null && fromWebStream != null && socketToWeb != null) {
                        //retransmit the stream from the client to server
                        toWebStream.write(buff, 0, len);
                        toWebStream.flush();
                        // from server to proxy then to client
                        new StreamHandler(fromWebStream, toClientStream);
                        //from client to proxy then to server
                        new StreamHandler(fromClientStream, toWebStream);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}

class StreamHandler extends Thread {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public StreamHandler(DataInputStream input, DataOutputStream output) {
        inputStream = input;
        outputStream = output;
        start();
    }

    public void run() {
        byte[] buff = new byte[10000];
        int len = 0;
        while (true) {
            try {
                if (len == -1)
                    return;
                len = inputStream.read(buff, 0, 10000);
                if (len > 0) {
                    outputStream.write(buff, 0, len);
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

class LocalDNSCache {
    public static boolean existsIP(String url) {
        if (Main.getDnsCache().containsKey(url))
            return true;
        else return false;
    }

    public static String getDNS(String url) {
        if (Main.getDnsCache().containsKey(url)) {
            String temp = Main.getDnsCache().get(url);
            return temp.substring(temp.indexOf("/") + 1);
        } else {
            return url;
        }
    }

    public static void storeIP(String url, String ip) {
        Main.getDnsCache().put(url, ip);
    }
}

