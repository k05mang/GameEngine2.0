package core.managers;

public class SceneManager {

	public static final MeshManager meshes = new MeshManager();
	public static final ResourceManager textures = new ResourceManager();
	public static final ResourceManager materials = new ResourceManager();
	public static final ConfigManager config = new ConfigManager("conf/config.json");
	
}
