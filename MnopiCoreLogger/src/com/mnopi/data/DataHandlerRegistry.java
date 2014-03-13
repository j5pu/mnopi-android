package com.mnopi.data;

import java.util.HashMap;

public class DataHandlerRegistry {
	
	private HashMap<String, DataHandler> registry = new HashMap<String, DataHandler>();
	
	public void bind(String key, DataHandler handler) {
		registry.put(key, handler);
	}
	
	public void unbind(String key) {
		registry.remove(key);
	}
	
	public DataHandler lookup(String key) {
		return registry.get(key);
	}

}
