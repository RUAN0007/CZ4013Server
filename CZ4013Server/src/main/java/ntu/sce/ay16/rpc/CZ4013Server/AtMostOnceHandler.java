package ntu.sce.ay16.rpc.CZ4013Server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AtMostOnceHandler implements RequestHandler {
	static Logger logger = LogManager.getLogger(AtMostOnceHandler.class.getName());    
	
	//key: Client IP + Request Timestamp; value; original cache
	private Map<String, Map<String,Object>> responseCache = null;
	private RequestHandler nextRqHdler = null;
	
	public AtMostOnceHandler(Map<String, Map<String, Object>> responseCache, RequestHandler nextRqHdler) {
		super();
		this.responseCache = responseCache;
		this.nextRqHdler = nextRqHdler;
	}

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, InetAddress client) {
		logger.entry();
				
		if(request.get("time") == null){
			return Util.generateError(Util.missingFieldMsg(new String[]{"time"}));
		}
		if(!(request.get("time") instanceof Long)){
			return Util.generateError(Util.wrongFieldType("time", "long"));
		}
		
		long requestTime = (Long)request.get("time");
		
		//Search in the cache
		String key = client.getHostName() + "." + requestTime;
		Map<String,Object> preReply = this.responseCache.get(key);
		
		if(preReply != null) return preReply;
		
		Map<String,Object> nextResponse = this.nextRqHdler.handleRequest(request, client);
		
		//Update the cache using the client ip and timestamp
		this.responseCache.put(key, nextResponse);
		
		logger.exit();
		return nextResponse;
	}

}
