package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RenameHandler implements RequestHandler {
	static Logger logger = LogManager.getLogger(RenameHandler.class.getName());    

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, InetAddress client) {

		List<String> missingFields = new LinkedList<String>();
		if(request.get("code") == null){
			missingFields.add("code");
		}
		if(request.get("old") == null){
			missingFields.add("old");
		}
		if(request.get("new") == null){
			missingFields.add("new");
		}
		if(missingFields.size() > 0){
			return Util.errorPacket(Util.missingFieldMsg(missingFields));
		}
		
		
		
		if(!(request.get("code") instanceof Integer)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("code", "integer"));
		}
		int code = (Integer)request.get("code");

		if(code != 1){
			String msg = Util.inconsistentReqCodeMsg("Rename", 4);
			logger.fatal(msg);
			return Util.errorPacket(msg);
		}

		//Check for old field
		
		if(!(request.get("old") instanceof String)){
			return Util.errorPacket(Util.inconsistentFieldTypeMsg("old", "String"));
		}

		//Check for new field

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
