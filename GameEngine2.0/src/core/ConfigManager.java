package core;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConfigManager {

	private JsonObject configs;
	
	public ConfigManager(String configFile) {
		try{
			FileReader json = new FileReader(new File(configFile));
			configs = new Gson().fromJson(json, JsonObject.class);
		}catch(IOException e){
			
		}
	}

	/**
	 * Gets the value of the settings specified by the given {@code name}
	 * 
	 * @param name Value to get from the settings section of the config file
	 * 
	 * @return String representing the data from the config file
	 */
	public String settings(String name){
		return ((JsonPrimitive)((JsonObject)configs.get("Settings")).get(name)).getAsString();
	}
}
