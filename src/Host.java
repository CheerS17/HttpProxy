public class Host {
	/*
	 *  define host class stored in cache
	 */
	private String IPAddr; 
	private int portNo;
	private long livetime;
	public Host(String IPAddr, int portNo) {
		this.IPAddr = IPAddr;
		this.portNo = portNo;
		this.livetime = System.currentTimeMillis();
	}
	public String getIPAddr() {
		return this.IPAddr;
	}
	public int getportNo() {
		return this.portNo;
	}
	public long getlivetime() {
		return this.livetime;
	}
}
