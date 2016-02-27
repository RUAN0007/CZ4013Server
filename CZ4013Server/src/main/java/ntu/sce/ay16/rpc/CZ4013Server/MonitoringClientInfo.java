package ntu.sce.ay16.rpc.CZ4013Server;

import java.net.InetAddress;

public class MonitoringClientInfo{
	private InetAddress clientAddr;
	private int clientPort;
	private long expiration;
	public MonitoringClientInfo(InetAddress clientAddr, int clientPort, long expiration) {
		super();
		this.clientAddr = clientAddr;
		this.clientPort = clientPort;
		this.expiration = expiration;
	}
	public InetAddress getClientAddr() {
		return clientAddr;
	}
	public int getClientPort() {
		return clientPort;
	}
	public long getExpiration() {
		return expiration;
	}
	
	
}

