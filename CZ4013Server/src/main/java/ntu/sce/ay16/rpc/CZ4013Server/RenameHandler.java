package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RenameHandler implements RequestHandler {
	static Logger logger = LogManager.getLogger(RenameHandler.class.getName());    

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
			String msg = Util.inconsistReqCodeMsg("Rename", 4);
			logger.fatal(msg);
			return Util.errorPacket(msg);
		}

		//Check for old field
		if(request.get("old") == null){
			return Util.errorPacket(Util.missingFieldMsg(new String[]{"old"}));
		}
		if(!(request.get("old") instanceof String)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("old", "String"));
		}

		//Check for new field
		if(request.get("new") == null){
			return Util.errorPacket(Util.missingFieldMsg(new String[]{"new"}));
		}
		if(!(request.get("new") instanceof String)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("new", "String"));
		}

		String oldFileName = (String)request.get("old");
		String newFileName = (String)request.get("new");


		File oldFile = Paths.get(oldFileName).toFile();
		
		if(!oldFile.exists()){
			String msg = Util.nonExistFileMsg(oldFileName);
			logger.error(msg);
			return Util.errorPacket(msg);
		}
		
		File newFile = Paths.get(newFileName).toFile();
		if(newFile.exists()){
			String msg = "Renamed file " + newFile + " already exists";
			logger.error(msg);
			return Util.errorPacket(msg);
		}
		
		if(oldFile.renameTo(newFile)){
			String msg = "Renaming file " + oldFileName + " to " + newFileName + " succeeded.";
			return Util.successPacket(msg);
		}else{
			String msg = "Renaming file " + oldFileName + " to " + newFileName + " failed.";
			return Util.errorPacket(msg);
		}

	}

}
