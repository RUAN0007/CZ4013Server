package ntu.sce.ay16.rpc.CZ4013Server.AppendTester;

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

public class AppendNonExistFileTester {
	Thread serverThread = null;
	private File file = null;
	private String filePath = "test/append.txt";
	private int port = 8504;
	
	
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
		//Create the test file
		this.file = Paths.get(filePath).toFile();
		if(this.file.exists()) file.delete();

	}


	@Test
	public void test() throws Exception {

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

		assertTrue(!this.file.exists());
	}

	@After
	public void tearDown(){
		this.serverThread.interrupt();
	}

}
