package ntu.sce.ay16.rpc.CZ4013Server.MonitorTester;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ntu.sce.ay16.rpc.CZ4013Server.Server;
import ntu.sce.ay16.rpc.CZ4013Server.Util;

public class MonitorExpirationTester {
	private Thread serverThread = null;
	private File file = null;
	private String filePath = "test/monitor.txt";
	private int port = 8405;
	String contents = "abcdefghi";
	

	@Before
	public void setUp() throws IOException, InterruptedException{
		this.serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				new Server(port,Server.AT_MOST_ONCE).start();				
			}
		});
		this.serverThread.start();

		Thread.sleep(2000);
		//Create the test file
		this.file = Paths.get(filePath).toFile();
		if(this.file.exists()) file.delete();
		file.createNewFile();


		//Write the contents
		BufferedWriter bw = new BufferedWriter(new FileWriter(this.file));
		bw.write(contents);
		bw.close();

	}


	@Test
	public void test() throws Exception {
		int cbPort = 9000;
		long duration = 1000L;
		long waiting = 2000L;
		int timeout = 3000;

		Map<String,Object> p = new HashMap<String,Object>();
		p.put("time",System.currentTimeMillis());
		p.put("code", 3);
		p.put("path", filePath);
		p.put("duration", Long.valueOf(duration));
		p.put("port", cbPort);

		byte[] b = Util.marshal(p);

		DatagramSocket dgs = new DatagramSocket();
		InetAddress serverAddr = InetAddress.getLocalHost();
		DatagramPacket request = 
				new DatagramPacket(b, b.length, serverAddr, port);
		dgs.send(request);

		System.out.println("Send to server: " + p);
		
		byte[] buffer = new byte[1024];
		DatagramPacket reply = 
				new DatagramPacket(buffer,buffer.length);
		dgs.receive(reply);
		byte[] data = Arrays.copyOf(reply.getData(), reply.getLength());
		
		Map<String,Object> response = Util.unmarshal(data);
		
		assertTrue((Integer) response.get("status") == 1);
		assertTrue(response.get("message") != null);
		Long expiration = (Long)response.get("end");
		assertTrue(System.currentTimeMillis() < expiration);
		assertTrue(expiration <= System.currentTimeMillis() + duration);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(waiting);
					Map<String,Object> p = new HashMap<String,Object>();
					p.put("time",System.currentTimeMillis());
					p.put("code", 5);
					p.put("path", filePath);
					p.put("append", "xyz");
					byte[] b = Util.marshal(p);

					DatagramSocket dgs = new DatagramSocket();
					InetAddress serverAddr = InetAddress.getLocalHost();
					DatagramPacket request = 
							new DatagramPacket(b, b.length, serverAddr, port);
					dgs.send(request);

					System.out.println("Append Request Send to server: " + p);

					byte[] buffer = new byte[1024];
					DatagramPacket reply = 
							new DatagramPacket(buffer,buffer.length);
					dgs.receive(reply);
					byte[] data = Arrays.copyOf(reply.getData(), reply.getLength());

					Map<String,Object> response = Util.unmarshal(data);

					assertTrue((Integer) response.get("status") == 1);
					assertTrue(response.get("message") != null);
					
					File file = Paths.get(filePath).toFile();
					Scanner fileScanner = new Scanner(file);
					String content = fileScanner.useDelimiter("\\Z").next();
					assertTrue(content.equals("abcdefghixyz"));
					fileScanner.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}).start();
		
		
		DatagramSocket cbSoc = new DatagramSocket(cbPort);

		buffer = new byte[1024];
		reply = 
				new DatagramPacket(buffer,buffer.length);
		cbSoc.setSoTimeout(timeout);
		
		try{
			cbSoc.receive(reply);
			fail("Shall not reach here as no reply shall make");
		}catch(Exception e){
			assertTrue(e instanceof java.net.SocketTimeoutException);
		}
	
		cbSoc.close();
	}

	@After
	public void tearDown(){
		this.serverThread.interrupt();
//		this.file.delete();
	}

}
