import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class HTTPProxy {
	private Integer portNo; // 127.0.0.1
	private String IPAddr; // 6101
	
	public HTTPProxy(Integer portNo, String IPAddr) {
		this.portNo = portNo; 
		this.IPAddr = IPAddr;
	}
	public Integer getportNo() { 
		return this.portNo;
	}
	public String getIPAddr() {
		return this.IPAddr;
	}
	public void Start() {
		try {
			ServerSocket proxySocket = new ServerSocket(); // new server socket
			proxySocket.bind(new InetSocketAddress(IPAddr, portNo)); // bind socket with port
			while (true) {
				new HTTPThread(proxySocket.accept()).start(); // new a thread
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}