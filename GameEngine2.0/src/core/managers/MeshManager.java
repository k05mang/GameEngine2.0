package core.managers;

import glMath.Transform;

import java.util.ArrayList;
import java.util.HashMap;

import textures.Texture;
import mesh.Mesh;

public class MeshManager extends ResourceManager{
	
	public MeshManager(){
		super();
	}
	
	public void transformMesh(String id, Transform transformation){
//		if(resources.get(id) != null){
//			((Mesh)resources.get(id)).transform(transformation);
//		}
	}
	
	@Override
	public Mesh get(String id){
		return (Mesh)super.get(id);
	}
}
