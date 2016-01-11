package mesh.loaders;

import java.io.File;

//import org.lwjgl.opengl.GL;

public class ModelLoader {
	
	public static void load(String file){
		load(new File(file));
	}
	
	public static void load(File file){
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				GL.createCapabilities();
//				MeshLoader loader = null;
//				if(file.getName().contains(".obj")){
//					loader = new OBJLoader(file);
//				}
//				loader.load();
//			}
//		}).start();
		
		MeshLoader loader = null;
		if(file.getName().contains(".obj")){
			loader = new OBJLoader(file);
		}
		loader.load();
	}
}
