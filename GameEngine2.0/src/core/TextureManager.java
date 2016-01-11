package core;

import java.io.File;
import java.util.HashMap;

import mesh.Renderable;
import textures.Texture;
import textures.enums.InternalFormat;
import textures.enums.TextureType;
import textures.loaders.ImageLoader;

public class TextureManager extends ResourceManager{
	
	public TextureManager(){
		super();
	}
	
	public void put(String id, Texture texture){
		resources.put(id, texture);
	}
	
//	public void load(String file){
//		load(new File(file));
//	}
//	
//	public void load(File file){
//		if(file.isDirectory()){
//			parseDirectory(file);
//		}else{
//			ImageLoader.load(InternalFormat.RGBA8, TextureType._2D, file);
//		}
//	}
//	
//	private void parseDirectory(File dir){
//		if(!dir.isDirectory()){
//			ImageLoader.load(InternalFormat.RGBA8, TextureType._2D, dir);
//		}else{
//			for(File file : dir.listFiles()){
//				parseDirectory(file);
//			}
//		}
//	}
}
