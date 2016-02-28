package ntu.sce.ay16.rpc.CZ4013Server.RenameTester;

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

public class RenameRequestTester {
	private Thread serverThread = null;
	private File newFile = null;
	private File oldFile = null;

	private String oldPath = "test/old.txt";
	private String newPath = "test/new.txt";

	private int port = 8872;
	private String contents = "Test File Renaming";
	
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
		this.oldFile = Paths.get(oldPath).toFile();
		if(this.oldFile.exists()) oldFile.delete();
		oldFile.createNewFile();
		
		
		//Write the contents
		BufferedWriter bw = new BufferedWriter(new FileWriter(this.oldFile));
		bw.write(contents);
		bw.close();
		
		this.newFile = Paths.get(newPath).toFile();
		this.newFile.delete();
	}


	@Test
	public void test() throws Exception {

		Map<String,Object> p = new HashMap<String,Object>();
		p.put("time",System.currentTimeMillis());
		p.put("code", 4);
		p.put("old", oldPath);
		p.put("new", newPath);

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

		this.oldFile = Paths.get(oldPath).toFile();
		this.newFile = Paths.get(newPath).toFile();
		assertTrue(!oldFile.exists());
		assertTrue(newFile.exists());
		
		Scanner fileScanner = new Scanner(newFile);
		String content = fileScanner.useDelimiter("\\Z").next();
		fileScanner.close();

		assertTrue(content.equals(this.contents));
	}

	@After
	public void tearDown(){
		this.serverThread.interrupt();
		this.newFile.delete();
		this.oldFile.delete();
	}

}
