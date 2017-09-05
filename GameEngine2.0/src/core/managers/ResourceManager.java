package core.managers;

import java.util.ArrayList;
import java.util.HashMap;

import core.Resource;

public class ResourceManager<T extends Resource> {
	protected HashMap<String, T> resources;
	
	public ResourceManager(){
		resources = new HashMap<String, T>();
	}
	
	public void put(String id, T res){
		resources.put(id,  res);
	}
	
	public T get(String id){
		return resources.get(id);
	}
	
	public void remove(String id){
		T removed = resources.remove(id);
		if(removed != null){
			removed.delete();
		}
	}
	
	public void removeAll(){
		for(T remove : resources.values()){
			remove.delete();
		}
		resources.clear();
	}
	
	public ArrayList<String> getIds(){
		return new ArrayList<String>(resources.keySet());
	}
	
	public ArrayList<T> getResources(){
		return new ArrayList<T>(resources.values());
	}
	
	public int numResources(){
		return resources.size();
	}
}
