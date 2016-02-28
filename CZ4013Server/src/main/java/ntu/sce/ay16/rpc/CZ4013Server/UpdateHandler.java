package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateHandler implements RequestHandler {
	
	static Logger logger = LogManager.getLogger(UpdateHandler.class.getName());    
	
	private Map<Path, Set<MonitoringClientInfo>> monitoringInfo = 
			null;
	
	
	private RequestHandler nextRqHdler = null;

	

	public UpdateHandler(Map<Path, Set<MonitoringClientInfo>> monitoringInfo, RequestHandler nextRqHdler) {
		super();
		this.monitoringInfo = monitoringInfo;
		this.nextRqHdler = nextRqHdler;
	}



	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, InetAddress client) {
		logger.entry();
		
		Map<String,Object> nextReply = this.nextRqHdler.handleRequest(request, client);
		
		int code = (Integer)nextReply.get("status");
		if(code == 0){
			return nextReply;
		}
		
		List<String> missingFields = new LinkedList<String>();
		if(request.get("path") == null){
			 missingFields.add("path");
		}
		if(missingFields.size() > 0){
			return Util.errorPacket(Util.missingFieldMsg(missingFields));
		}
		if(!(request.get("path") instanceof String)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("path", "String"));
		}
		
		String file = (String)request.get("path");
		String content = null;
		long modificationTime;
		Path filePath = null;
		try{
			 filePath = Paths.get(file);
			File reqFile = filePath.toFile();
			Scanner fileScanner = new Scanner(reqFile);
			content = fileScanner.useDelimiter("\\Z").next();
			modificationTime = reqFile.lastModified();
			fileScanner.close();
		}catch(InvalidPathException e){
			String msg = Util.invalidPathMsg(file);
			logger.error(msg);
			return Util.errorPacket(msg);
		}catch (FileNotFoundException e) {
			String msg = Util.nonExistFileMsg(file);
			logger.error(msg);
			return Util.errorPacket(msg);
		}
		
		for(MonitoringClientInfo clientInfo: this.monitoringInfo.get(filePath)){
			InetAddress clientAddr = clientInfo.getClientAddr();
			int clientPort = clientInfo.getClientPort();
			long expiration = clientInfo.getExpiration();
			
			if(System.currentTimeMillis() < expiration){
				Map<String,Object> callbackMsg = new HashMap<>();
				callbackMsg.put("status"	, Integer.valueOf(1));
				callbackMsg.put("time", modificationTime);
				callbackMsg.put("path", (String)request.get("path"));
				callbackMsg.put("modifier", client.getHostAddress());
				callbackMsg.put("content", content);
				Util.sendPacket(clientAddr, clientPort, callbackMsg);
			}
		}
		
		
		logger.exit();
		return nextReply;
	}
	
	
}
