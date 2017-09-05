package renderers;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.ArrayList;
import java.util.Collection;

import core.Camera;
import core.Entity;
import core.managers.SceneManager;
import framebuffer.GBuffer;
import lights.Light;
import mesh.Material;
import mesh.Mesh;
import mesh.primitives.geometry.Plane;
import static renderers.ShaderProgramTypes.*;

public class DeferredRenderer extends Renderer{

	private GBuffer gBuffer;
    private Camera main;
	public static final Plane quad = new Plane(2);

	public DeferredRenderer(int width, int height, Camera cam){
		super(width, height);
		main = cam;
		gBuffer = new GBuffer(width, height);
		
		SceneManager.shaderPrograms.get("lightPass").setUniform("screenSpace", width, height);
		setupUniforms();
	}
	
	private void setupUniforms(){
		SceneManager.shaderPrograms.get("geoPass").setUniform("proj", main.getProjection());
		SceneManager.shaderPrograms.get("geoPass").setUniform("color", 1, 1, 1);
		SceneManager.shaderPrograms.get("geoPass").setUniform("specPower", 157.0f);
		SceneManager.shaderPrograms.get("geoPass").setUniform("specInt", 2);
		
		SceneManager.shaderPrograms.get("geoPass").setUniform("albedo", 0);
		SceneManager.shaderPrograms.get("geoPass").setUniform("normalMap", 1);
		SceneManager.shaderPrograms.get("geoPass").setUniform("specMap", 2);
		SceneManager.shaderPrograms.get("geoPass").setUniform("bumpMap", 3);
		
		SceneManager.shaderPrograms.get("geoPass").setUniform("gamma", 1);

		SceneManager.shaderPrograms.get("stencilPass").setUniform("proj", main.getProjection());
		
		SceneManager.shaderPrograms.get("lightPass").setUniform("proj", main.getProjection());
		SceneManager.shaderPrograms.get("lightPass").setUniform("positions", 0);
		SceneManager.shaderPrograms.get("lightPass").setUniform("normals", 1);

		SceneManager.shaderPrograms.get("finalPass").setUniform("diffuse", 0);
		SceneManager.shaderPrograms.get("finalPass").setUniform("lighting", 1);
		SceneManager.shaderPrograms.get("finalPass").setUniform("ambient", .3f);
		SceneManager.shaderPrograms.get("finalPass").setUniform("gamma", 1);
	}
	
	public void render(Collection<Entity> meshes, 
			ArrayList<Light> lights, 
			RenderLogic logic){
		
		gBuffer.geoPass();//ready the gbuffer for the geometry pass
		SceneManager.shaderPrograms.get("geoPass").setUniform("view", main.getLookAt());
		SceneManager.shaderPrograms.get("stencilPass").setUniform("view", main.getLookAt());
		SceneManager.shaderPrograms.get("lightPass").setUniform("view", main.getLookAt());
		SceneManager.shaderPrograms.get("lightPass").setUniform("eye", main.getPos());
		
		SceneManager.shaderPrograms.get("geoPass").bind();
		//render geometry
		for(Entity mesh : meshes){
			SceneManager.shaderPrograms.get("geoPass").setUniform("model", mesh.getTransform().getMatrix());
			Material mat = (Material) SceneManager.materials.get(mesh.getMesh().getMaterial());
			mat.bind(SceneManager.shaderPrograms.get("geoPass"));
			mesh.getMesh().render();
		}
		SceneManager.shaderPrograms.get("geoPass").unbind();
		
		gBuffer.setupLightingPass();
		
		for(Light light : lights){
			Mesh mesh = light.getVolume();
			SceneManager.shaderPrograms.get("stencilPass").bind();
			gBuffer.stencilPass();
			//render the light volume for stenciling
			SceneManager.shaderPrograms.get("stencilPass").setUniform("model", light.getTransform().getMatrix());
			mesh.render();
			SceneManager.shaderPrograms.get("stencilPass").unbind();
			
			SceneManager.shaderPrograms.get("lightPass").bind();
			//render the volume for calculation
			gBuffer.lightPass();
			light.bind(SceneManager.shaderPrograms.get("lightPass"));
			mesh.render();
			SceneManager.shaderPrograms.get("lightPass").unbind();
		}
		
		gBuffer.finalPass();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		SceneManager.shaderPrograms.get("finalPass").bind();
		quad.render();
		SceneManager.shaderPrograms.get("finalPass").unbind();
	}
	
	public void delete(){
		gBuffer.delete();
		SceneManager.shaderPrograms.get("geoPass").delete(); 
		SceneManager.shaderPrograms.get("stencilPass").delete(); 
		SceneManager.shaderPrograms.get("lightPass").delete();
		SceneManager.shaderPrograms.get("finalPass").delete();
		quad.delete();
	}
}
