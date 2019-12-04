package db;

import java.util.HashMap;
import java.util.Map;

import server.ServerThread;

public class ThreadPool {
	public static Map<String, ServerThread> threadpool = new HashMap<String, ServerThread>();
}
