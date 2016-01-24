package core;

import java.util.ArrayList;
import java.util.HashMap;

public class ResourceManager {
	protected HashMap<String, Resource> resources;
	
	public ResourceManager(){
		resources = new HashMap<String, Resource>();
	}
	
	public void put(String id, Resource res){
		resources.put(id,  res);
	}
	
	public Resource get(String id){
		return resources.get(id);
	}
	
	public void remove(String id){
		Resource removed = resources.remove(id);
		if(removed != null){
			removed.delete();
		}
	}
	
	public void removeAll(){
		for(Resource remove : resources.values()){
			remove.delete();
		}
		resources.clear();
	}
	
	public ArrayList<String> getIds(){
		return new ArrayList<String>(resources.keySet());
	}
	
	public ArrayList<Resource> getResources(){
		return new ArrayList<Resource>(resources.values());
	}
	
	public int numResource(){
		return resources.size();
	}
}
