package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModificationTimeHandler implements RequestHandler {
	static Logger logger = LogManager.getLogger(ModificationTimeHandler.class.getName());    

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, InetAddress client) {

		//Check for code field
		if(request.get("code") == null){
			return Util.generateError(Util.missingFieldMsg(new String[]{"code"}));
		}
		if(!(request.get("code") instanceof Integer)){
			return Util.generateError(Util.inconsistentFieldTypeMsg("time", "integer"));
		}
		int code = (Integer)request.get("code");

		if(code != 0){
			String msg = Util.inconsistReqCodeMsg("ModificationTime", code);
			logger.fatal(msg);
			return Util.generateError(msg);
		}

		//Check for path field
		if(request.get("path") == null){
			return Util.generateError(Util.missingFieldMsg(new String[]{"path"}));
		}
		if(!(request.get("path") instanceof String)){
			return Util.generateError(Util.inconsistentFieldTypeMsg("path", "String"));
		}

		String file = (String)request.get("path");
		Long modificationTime = 0L;

		try{
			Path filePath = Paths.get(file);
			File reqFile = filePath.toFile();

			modificationTime = reqFile.lastModified();
		}catch(InvalidPathException e){
			String msg = Util.invalidPathMsg(file);
			logger.error(msg);
			return Util.generateError(msg);
		}


		Map<String,Object> reply = new HashMap<String,Object>();
		reply.put("status", Integer.valueOf(1));
		reply.put("path", file);
		reply.put("modification", modificationTime);
		return reply;
	}

}
