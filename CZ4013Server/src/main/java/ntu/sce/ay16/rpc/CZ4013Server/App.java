package ntu.sce.ay16.rpc.CZ4013Server;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    static Logger logger = LogManager.getLogger(App.class.getName());    

    public static void main( String[] args )
    {
		Map<String,Object> p = new HashMap<String,Object>();
		p.put("time",System.currentTimeMillis());
		p.put("code", 1);
		p.put("path", "a.txt");
		
		System.out.println(p.get("sdf"));
    }
}
