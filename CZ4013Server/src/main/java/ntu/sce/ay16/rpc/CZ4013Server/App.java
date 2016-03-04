package ntu.sce.ay16.rpc.CZ4013Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
		logger.entry();
		if(args.length == 0){
			new Server().start();
		}else if(args.length == 1){
			int port = Integer.parseInt(args[0]);
			new Server(port).start();
		}else if(args.length == 2){
			int  semantics = Integer.parseInt(args[1]);
			int port = Integer.parseInt(args[0]);
			new Server(port,semantics).start();
		}else if(args.length == 3){
			int  semantics = Integer.parseInt(args[1]);
			int port = Integer.parseInt(args[0]);
			int lostReplyCount = Integer.parseInt(args[2]);
			Util.lostReplyCount = lostReplyCount;
			new Server(port,semantics).start();

		}else if(args.length == 4){
			int  semantics = Integer.parseInt(args[1]);
			int port = Integer.parseInt(args[0]);
			int lostReplyCount = Integer.parseInt(args[2]);
			Util.lostReplyCount = lostReplyCount;
			int replyDelaySec = Integer.parseInt(args[3]);
			Util.replyDelaySec = replyDelaySec;
			new Server(port,semantics).start();

		}
		logger.exit();
	}
}
