package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateHandler implements RequestHandler {
	
	static Logger logger = LogManager.getLogger(UpdateHandler.class.getName());    
	
	private Map<Path, Set<MonitoringClientInfo>> monitoringInfo = 
			new HashMap<>();
	
	
	private RequestHandler nextRqHdler = null;
	
	public UpdateHandler(RequestHandler nextRqHdler) {
		super();
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
		
		if(request.get("path") == null){
			return Util.generateError(Util.missingFieldMsg(new String[]{"path"}));
		}
		if(!(request.get("path") instanceof String)){
			return Util.generateError(Util.inconsistentFieldTypeMsg("path", "String"));
		}
		
		
		Path filePath = null;
		try{
			filePath = Paths.get((String)request.get("path"));
		}catch(InvalidPathException e){
			String msg = Util.invalidPathMsg((String)request.get("path"));
			logger.error(msg);
			return Util.generateError(msg);
		}
		
		String file = (String)request.get("path");
		String content = null;
		long modificationTime;
		try {
			File reqFile = filePath.toFile();
			Scanner fileScanner = new Scanner(reqFile);
			content = fileScanner.useDelimiter("\\Z").next();
			modificationTime = reqFile.lastModified();
			fileScanner.close();
		} catch (FileNotFoundException e) {
			String msg = Util.nonExistFileMsg(file);
			logger.error(msg);
			return Util.generateError(msg);
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
				Util.sendMsg(clientAddr, clientPort, callbackMsg);
			}
		}
		
		
		logger.exit();
		return nextReply;
	}
	
	
}
