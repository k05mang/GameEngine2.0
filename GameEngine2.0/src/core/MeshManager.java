package core;

import glMath.Transform;

import java.util.ArrayList;
import java.util.HashMap;

import textures.Texture;
import mesh.Renderable;

public class MeshManager extends ResourceManager{
	
	public MeshManager(){
		super();
	}
	
	public void put(String id, Renderable mesh){
		resources.put(id, mesh);
	}
	
	public void transformMesh(String id, Transform transformation){
		if(resources.get(id) != null){
			((Renderable)resources.get(id)).transform(transformation);
		}
	}
}
