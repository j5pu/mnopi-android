package com.mnopi.data;

import android.accounts.Account;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for data handlers admitted by the application logger service
 *  
 * @author Alfredo Lainez
 *
 */
public class DataHandlerRegistry {

    private static final Map<String, DataHandlerRegistry> instances =
            new HashMap<String, DataHandlerRegistry>();
    private static boolean isUsed = false;

    private Map<String, DataHandler> registry = new HashMap<String, DataHandler>();
    private boolean enabled = true;

    private DataHandlerRegistry(){
        //no implementation
    }

    /**
     * Check if the registries are being used or there is need
     * to recreate them again
     * @return
     */
    public static synchronized boolean isUsed() {
        return isUsed;
    }

    /**
     * Gets instance from multiton, creating it if it doesn't exist
     * @param key
     * @return
     */
    public static synchronized DataHandlerRegistry getInstance(String key){

        DataHandlerRegistry instance = instances.get(key);

        if (instance == null){
            instance = new DataHandlerRegistry();
            instances.put(key, instance);
        }

        isUsed = true;
        return instance;
    }
	
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

	/**
	 * Returns the keys for the registered handlers
	 * @return Set of Strings
	 */
	public Set<String> getHandlersKeys() {
		return registry.keySet();
	}

    /**
     * Calls save on the specified handler
     * @param key key of the handler
     * @param bundle service information
     * @return
     */
    public void saveData(String key, Bundle bundle) {
        if (enabled) {
            DataHandler handler = (DataHandler) registry.get(key);
            if (handler != null) {
                handler.saveData(bundle);
            }
        }
    }

    /**
     * Calls send on the specified handler
     * @param key key of the handler
     */
    public void sendData(String key, Account account) throws Exception {
        if (enabled) {
            registry.get(key).sendData(account);
        }
    }

    /**
     * Sends to the server all the information managed by the handlers
     */
    public void sendAll(Account account) throws Exception {
        DataHandler handler;
        if (enabled) {
            for (String handlerKey : this.getHandlersKeys()) {
                handler = registry.get(handlerKey);
                handler.sendData(account);
            }
        }
    }

    /**
     * Unbind all handlers from the registry
     */
    public void unbindAll() {
        registry.clear();
    }

    /**
     *
     * @param key
     */
    public DataHandler lookup(String key) {
        return registry.get(key);
    }

    /**
     * If the registry is disabled it won't allow data reception or delivery from any of
     * the handlers registered
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * If the registry is disabled it won't allow data reception or delivery from any of
     * the handlers registered
     * @return
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Clears all registries and set the multiton as unused
     */
    public static void clearRegistries() {
        instances.clear();
        isUsed = false;
    }

}
