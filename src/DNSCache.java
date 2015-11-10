import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DNSCache {
	static Map<String, Host> cache = new HashMap<String, Host>();
	static String IP, urlhost;
	static int urlport;
	static private Host host;
	private static int MAXt = 30000; // 30 seconds in millisecond
	
	static public String getAddr(URL url) {
		urlhost = url.getHost(); 
		/*
		 *  get host address in this url
		 */
		urlport = (url.getPort() > -1) ? url.getPort() : 80; 
		/*
		 * get port number in this url
		 * if url == -1
		 * then we set it 80(default)
		 */
		if (cache.containsKey(urlhost)) { 
		//	System.out.println("Got " + urlhost + " in cache.");
			/*
			 * url exist in cache
			 */
			host = cache.get(urlhost);
			if (System.currentTimeMillis() - host.getlivetime() > MAXt) 
				cache.remove(urlhost);
			/*
			 *  if this item live more than 30 seconds then remove it
			 */
			return host.getIPAddr(); // return host address in cache
		}
		else { // url not exist in cache
			try {
				IP = InetAddress.getByName(urlhost).toString();
				String[] tmp = IP.split("/");
				IP = tmp[1];
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			cache.put(urlhost, new Host(IP, urlport)); // add url to cache
			return urlhost; // return this url host address
		}
	}
	
	static public int getPort(URL url) {
		urlhost = url.getHost();
		/*
		 *  get host address in this url
		 */
		urlport = (url.getPort() > -1) ? url.getPort() : 80;
		/*
		 * get port number in this url
		 * if url == -1
		 * then we set it 80(default)
		 */
		if (cache.containsKey(urlhost)) {
			/*
			 * url exist in cache
			 */
			host = cache.get(urlhost);
			if (System.currentTimeMillis() - host.getlivetime() > MAXt)
				cache.remove(urlhost);
			/*
			 *  if this item live more than 30 seconds then remove it
			 */
			return host.getportNo(); // return port number in cache
		}
		else { // url not exist in cache
			try {
				IP = InetAddress.getByName(urlhost).toString();
				String[] tmp = IP.split("/");
				IP = tmp[1];
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			cache.put(urlhost, new Host(IP, urlport)); // add url to cache
			return urlport; // return this url port number
		}
	}
}
