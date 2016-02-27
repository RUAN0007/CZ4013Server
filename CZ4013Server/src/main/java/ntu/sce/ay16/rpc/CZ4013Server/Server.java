package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
	static Logger logger = LogManager.getLogger(Server.class.getName());    

	public static int AT_MOST_ONCE = 1;
	public static int AT_LEAST_ONCE = 2;
	private int port = 8888;
	private int semantics = AT_MOST_ONCE;

	public Server(int port, int semantics){
		this.port = port;
		this.semantics = semantics;
	}

	public Server(int port){
		this.port = port;
	}

	public void start(){
		logger.entry();
		try(DatagramSocket dgs = new DatagramSocket(this.port)){

			while(true){
				byte[] buffer = new byte[1024];
				DatagramPacket requestPacket = 
						new DatagramPacket(buffer,buffer.length);
				logger.trace("Waiting for request at port" + this.port);
				dgs.receive(requestPacket);
				byte[] data = Arrays.copyOf(requestPacket.getData(), requestPacket.getLength());
				InetAddress clientAddr = requestPacket.getAddress();
				int  clientPort = requestPacket.getPort();
				Map<String,Object> request = null;

				//Try to unmarshal the received packet
				try{
					request = Util.unmarshal(data);
				}catch(Exception e){
					String msg = Util.failUnMarshalMsg(data);
					msg += " Reason: " + e.getMessage();
					logger.error(msg);

					Util.sendMsg(clientAddr, clientPort, Util.generateError(msg));
					continue;
				}

				logger.info("Received Request " + request.toString());
				
				//Switch the request code
				



			}//End of while
		} catch (SocketException e1) {
			logger.fatal(e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.fatal(e1.getMessage());
			e1.printStackTrace();
		}
		logger.exit();
	}


	
	private static void configureRequestHandler(){
		
	}



}
