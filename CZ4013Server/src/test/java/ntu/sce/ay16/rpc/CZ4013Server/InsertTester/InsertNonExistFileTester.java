package ntu.sce.ay16.rpc.CZ4013Server.InsertTester;

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

public class InsertNonExistFileTester {
	private Thread serverThread = null;
	private File file = null;
	private String filePath = "test/insert.txt";
	private int port = 8601;
	
	
	@Before
	public void setUp() throws IOException, InterruptedException{
		this.serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				new Server(port).start();				
			}
		});
		this.serverThread.start();
		
		Thread.sleep(2000);
		this.file = Paths.get(filePath).toFile();
		file.delete();

	}


	@Test
	public void test() throws Exception {

		Map<String,Object> p = new HashMap<String,Object>();
		p.put("time",System.currentTimeMillis());
		p.put("code", 2);
		p.put("offset", 3);
		p.put("path", filePath);
		p.put("insertion", "zyx");
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
		
		assertTrue((Integer) response.get("status") == 0);
		assertTrue(response.get("message") != null);
		
		this.file = Paths.get(filePath).toFile();
;
		assertTrue(!this.file.exists());
	}

	@After
	public void tearDown(){
		this.serverThread.interrupt();
	}

}
