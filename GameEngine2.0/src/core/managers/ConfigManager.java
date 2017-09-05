package core.managers;
import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConfigManager {

	private JsonObject configs;
	private static final String defaultConfig = "{\"Settings\":{}, \"System\":{\"bezier-resolution\":20}}";
	
	public ConfigManager(String configFile) {
		Gson parser = new Gson();
		try{
			File config = new File(configFile);
			if(config.exists()){
				FileReader json = new FileReader(config);
				configs = parser.fromJson(json, JsonObject.class);
			}else{
				//if the configuration file doesn't exist then load the default config
				configs = parser.fromJson(defaultConfig, JsonObject.class);
			}
		}catch(Exception e){
			//if we failed to load the configuration load the default
			configs = parser.fromJson(defaultConfig, JsonObject.class);
		}
	}
	
	/**
	 * Gets a named value from the configuration file. The value returned can be any JSON type.
	 * 
	 * @param name Name of the value to retrieve from the config file
	 * 
	 * @return JsonElement representing the value specified by the named value, if the value doesn't exist a null will be returned
	 */
	public JsonElement get(String name){
		return configs.get(name);
	}

	/**
	 * Gets a named value from the "Settings" section in the config file, specified by the given {@code name}
	 * 
	 * @param name Value to get from the "Settings" section of the config file
	 * 
	 * @return JsonPrimitive containing the data from the config file, 
	 * if the value is not found or "Settings" section doesn't exist, then a null will be returned
	 */
	public JsonPrimitive settings(String name){
		JsonObject settings = (JsonObject)configs.get("Settings");
		//check that both the settings section and the value specified exist
		if(settings != null && settings.has(name)){
			return (JsonPrimitive)settings.get(name);
		}
		
		return null;
	}
	
	/**
	 * Gets a named value from the "System" section in the config file, specified by the given {@code name}
	 * 
	 * @param name Value to get from the "System" section of the config file
	 * 
	 * @return
	 */
	public JsonElement system(String name){
		JsonObject system = (JsonObject)configs.get("System");
		
		if(system != null && system.has(name)){
			return (JsonElement)system.get(name);
		}
		
		return null;
	}
}
