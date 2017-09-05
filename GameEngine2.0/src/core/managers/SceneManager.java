package core.managers;

import mesh.Material;
import mesh.Mesh;
import shaders.ShaderProgram;
import textures.Texture;

public class SceneManager {

	public static final ResourceManager<Mesh> meshes = new ResourceManager<Mesh>();
	public static final ResourceManager<Texture> textures = new ResourceManager<Texture>();
	public static final ResourceManager<Material> materials = new ResourceManager<Material>();
	public static final ConfigManager config = new ConfigManager("conf/config.json");
	public static final ShaderProgramManager shaderPrograms = new ShaderProgramManager();
	
}
