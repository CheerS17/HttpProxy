import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;

public class HTTPThread extends Thread {
	private Socket client, remote;
	private static int MAXtimeout = 1000;
	
	public HTTPThread(Socket client) {
		this.client = client;
	}
	@Override
	public void run() {
		try {
			this.client.setSoTimeout(MAXtimeout);
			this.client.setKeepAlive(false);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuffer buffer = new StringBuffer();
		/*
		 * read data from socket to br	
		 */
			String firstline = br.readLine(); // get first line
			
			String[] action = firstline.split(" "); // action format is like 'GET http://www.apple.com HTTP/1.1' 
			String address = action[1]; 
			if (address.startsWith("http://") == false) return; // address must start with 'http://'
			
			while (true) {
				String line = br.readLine();
				buffer.append(line);
				buffer.append('\n');
				if (line.equals("")) {
					break;
				}
			}
			// get the rest request
			
	//		System.out.println("URL: " + address);
			
			URL url = new URL(address);
			System.out.println("Host " + DNSCache.getAddr(url) + " " + "Port " + DNSCache.getPort(url));
			
			remote = new Socket(DNSCache.getAddr(url), DNSCache.getPort(url)); // send request to server
			
			this.remote.getOutputStream().write((firstline + "\n").getBytes());
			this.remote.getOutputStream().write(buffer.toString().getBytes());
			this.remote.setSoTimeout(MAXtimeout);
			
			copy(); // copy attached data
			
			this.client.getOutputStream().close();
			this.remote.getInputStream().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void copy() throws IOException {
		byte bytes[] = new byte[8096];
		int byteslen;
		while ((byteslen = this.remote.getInputStream().read(bytes)) > 0)
			this.client.getOutputStream().write(bytes, 0 , byteslen);
	}
}
