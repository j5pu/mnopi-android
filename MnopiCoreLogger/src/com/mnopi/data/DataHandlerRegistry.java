package com.mnopi.data;

import java.util.HashMap;

/**
 * Registry for data handlers admitted by the application logger service
 *  
 * @author Alfredo Lainez
 *
 */
public class DataHandlerRegistry {
	
	private HashMap<String, DataHandler> registry = new HashMap<String, DataHandler>();
	
	/**
	 * Binds new data handler using the given key
	 * @param key
	 * @param handler
	 */
	public void bind(String key, DataHandler handler) {
		registry.put(key, handler);
	}

	/**
	 * Unbinds the specified handler
	 * @param key
	 */
	public void unbind(String key) {
		registry.remove(key);
	}
	
	public DataHandler lookup(String key) {
		return registry.get(key);
	}

}
