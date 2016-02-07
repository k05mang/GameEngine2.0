package renderers;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glDisable;

import java.util.ArrayList;

import lights.Light;
import mesh.Material;
import mesh.Mesh;
import mesh.primitives.geometry.Plane;
import shaders.Shader;
import shaders.ShaderProgram;
import shaders.ShaderStage;
import core.Camera;
import core.Resource;
import core.SceneManager;
import core.TransformControl;
import framebuffer.Gbuffer;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;

public class DeferredRenderer {

	private Gbuffer gbuffer;
	private ShaderProgram geoPass, stencilPass, lightPass, finalPass;
	private Camera main;
	public static final Plane quad = new Plane(2);
	
	public DeferredRenderer(int width, int height, Camera cam){
		main = cam;
		gbuffer = new Gbuffer(width, height);
		Shader geoVert = new Shader("shaders/geoPass/geoVert.glsl", ShaderStage.VERTEX);
		Shader geoFrag = new Shader("shaders/geoPass/geoFrag.glsl", ShaderStage.FRAG);

		if(!geoVert.compile()){
			System.out.println(geoVert.getInfoLog());
		}
		if(!geoFrag.compile()){
			System.out.println(geoFrag.getInfoLog());
		}
		geoPass = new ShaderProgram();
		geoPass.attach(geoVert);
		geoPass.attach(geoFrag);
		if(!geoPass.link()){
			System.out.println(geoPass.getInfoLog());
		}
		
		Shader stencilVert = new Shader("shaders/stencilPass/stencilVert.glsl", ShaderStage.VERTEX);
		Shader stencilFrag = new Shader("shaders/stencilPass/stencilFrag.glsl", ShaderStage.FRAG);

		if(!stencilVert.compile()){
			System.out.println(stencilVert.getInfoLog());
		}
		if(!stencilFrag.compile()){
			System.out.println(stencilFrag.getInfoLog());
		}
		stencilPass = new ShaderProgram();
		stencilPass.attach(stencilVert);
		stencilPass.attach(stencilFrag);
		if(!stencilPass.link()){
			System.out.println(stencilPass.getInfoLog());
		}
		
		Shader lightVert = new Shader("shaders/lightPass/lightVert.glsl", ShaderStage.VERTEX);
		Shader lightFrag = new Shader("shaders/lightPass/lightFrag.glsl", ShaderStage.FRAG);

		if(!lightVert.compile()){
			System.out.println(lightVert.getInfoLog());
		}
		if(!lightFrag.compile()){
			System.out.println(lightFrag.getInfoLog());
		}
		lightPass = new ShaderProgram();
		lightPass.attach(lightVert);
		lightPass.attach(lightFrag);
		if(!lightPass.link()){
			System.out.println(lightPass.getInfoLog());
		}

		Shader finalVert = new Shader("shaders/finalPass/finalVert.glsl", ShaderStage.VERTEX);
		Shader finalFrag = new Shader("shaders/finalPass/finalFrag.glsl", ShaderStage.FRAG);

		if(!finalVert.compile()){
			System.out.println(finalVert.getInfoLog());
		}
		if(!finalFrag.compile()){
			System.out.println(finalFrag.getInfoLog());
		}
		finalPass = new ShaderProgram();
		finalPass.attach(finalVert);
		finalPass.attach(finalFrag);
		if(!finalPass.link()){
			System.out.println(finalPass.getInfoLog());
		}

		lightPass.setUniform("screenSpace", width, height);
		setupUniforms();
	}
	
	private void setupUniforms(){
		geoPass.setUniform("proj", main.getProjection());
		geoPass.setUniform("color", 1, 1, 1);
		geoPass.setUniform("specPower", 157.0f);
		geoPass.setUniform("specInt", 2);
		
		geoPass.setUniform("albedo", 0);
		geoPass.setUniform("normalMap", 1);
		geoPass.setUniform("specMap", 2);
		geoPass.setUniform("bumpMap", 3);
		
		geoPass.setUniform("gamma", 1);

		stencilPass.setUniform("proj", main.getProjection());
		
		lightPass.setUniform("proj", main.getProjection());
		lightPass.setUniform("positions", 0);
		lightPass.setUniform("normals", 1);

		finalPass.setUniform("diffuse", 0);
		finalPass.setUniform("lighting", 1);
		finalPass.setUniform("ambient", .3f);
		finalPass.setUniform("gamma", 1);
	}
	
	public void render(ArrayList<Resource> meshes, ArrayList<Light> lights){
		gbuffer.geoPass();//ready the gbuffer for the geometry pass
		geoPass.setUniform("view", main.getLookAt());
		stencilPass.setUniform("view", main.getLookAt());
		lightPass.setUniform("view", main.getLookAt());
		lightPass.setUniform("eye", main.getEye());
		
		geoPass.bind();
		//render geometry
		for(Resource mesh : meshes){
			Mesh castMesh = ((Mesh) mesh);
			geoPass.setUniform("model", castMesh.getModelView());
			Material mat = (Material) SceneManager.materials.get(castMesh.getMaterial());
			mat.bind(geoPass);
			castMesh.render();
		}
		geoPass.unbind();
		
		gbuffer.setupLightingPass();
		
		for(Light light : lights){
			Mesh mesh = light.getVolume();
			stencilPass.bind();
			gbuffer.stencilPass();
			//render the light volume for stenciling
			stencilPass.setUniform("model", mesh.getModelView());
			mesh.render();
			stencilPass.unbind();
			
			lightPass.bind();
			//render the volume for calculation
			gbuffer.lightPass();
			light.bind(lightPass);
			mesh.render();
			lightPass.unbind();
		}
		
		gbuffer.finalPass();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		finalPass.bind();
		quad.render();
		finalPass.unbind();
	}
	
	public void delete(){
		gbuffer.delete();
		geoPass.delete(); 
		stencilPass.delete(); 
		lightPass.delete();
		finalPass.delete();
		quad.delete();
	}
}
