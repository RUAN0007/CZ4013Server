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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadHandler implements RequestHandler {
	static Logger logger = LogManager.getLogger(ReadHandler.class.getName());    

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, InetAddress client) {

		//Check for code field
		if(request.get("code") == null){
			return Util.errorPacket(Util.missingFieldMsg(new String[]{"code"}));
		}
		if(!(request.get("code") instanceof Integer)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("code", "integer"));
		}
		int code = (Integer)request.get("code");

		if(code != 1){
			String msg = Util.inconsistReqCodeMsg("Read", 1);
			logger.fatal(msg);
			return Util.errorPacket(msg);
		}

		//Check for path field
		if(request.get("path") == null){
			return Util.errorPacket(Util.missingFieldMsg(new String[]{"path"}));
		}
		if(!(request.get("path") instanceof String)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("path", "String"));
		}

		String file = (String)request.get("path");

		
		String content = null;
		long modificationTime = 0L;
		
		try{
			Path filePath = Paths.get(file);
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
		
		Map<String,Object> reply = new HashMap<>();
		reply.put("status"	, Integer.valueOf(1));
		reply.put("modification", modificationTime);
		reply.put("path", file);
		reply.put("content", content);		
		
		return reply;
	}

}
