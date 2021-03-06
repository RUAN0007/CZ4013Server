package ntu.sce.ay16.rpc.CZ4013Server;

import java.net.InetAddress;
/**
 * An instance of MonitoringClientInfo is a record for the clients that monitoring a certain file. 
 * The record consists of a client's IP, port number and the time when monitoring expires.
 * @author ruanpingcheng
 *
 */
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
	@Override
	public String toString() {
		return "MonitoringClientInfo [clientAddr=" + clientAddr + ", clientPort=" + clientPort + ", expiration="
				+ expiration + "]";
	}
	
	
	
}

