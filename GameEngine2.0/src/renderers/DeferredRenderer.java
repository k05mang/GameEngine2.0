package renderers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import java.util.ArrayList;

import lights.Light;
import mesh.Renderable;
import shaders.Shader;
import shaders.ShaderProgram;
import shaders.ShaderStage;
import core.Camera;
import core.Resource;
import framebuffer.Gbuffer;

public class DeferredRenderer {

	private Gbuffer gbuffer;
	private ShaderProgram geoPass, stencilPass, lightPass;
	private Camera main;
	private int width, height;
	
	public DeferredRenderer(int width, int height, Camera cam){
		main = cam;
		this.width = width;
		this.height = height;
		gbuffer = new Gbuffer(width, height);
		Shader geoVert = new Shader("shaders/geoPass/geoVert.glsl", ShaderStage.VERTEX);
		Shader geoFrag = new Shader("shaders/geoPass/geoVert.glsl", ShaderStage.FRAG);

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
	}
	
	private void setupUniforms(){
		geoPass.setUniform("proj", main.getProjection());
		geoPass.setUniform("color", .5f, .7f, .3f);
		geoPass.setUniform("specPow", .5f, .7f, .3f);
		geoPass.setUniform("specInt", .5f, .7f, .3f);
		

		stencilPass.setUniform("proj", main.getProjection());
		

		lightPass.setUniform("proj", main.getProjection());
		lightPass.setUniform("screenSpace", width, height);
		lightPass.setUniform("ambient", .3f);
		lightPass.setUniform("positions", 0);
		lightPass.setUniform("normals", 1);
		intensity;
		uniform vec3 lightColor, lightPos, eye;
	}
	
	public void render(ArrayList<Resource> meshes, ArrayList<Light> lights){
		gbuffer.geoPass();//ready the gbuffer for the geometry pass
		geoPass.setUniform("view", main.getLookAt());
		stencilPass.setUniform("view", main.getLookAt());
		lightPass.setUniform("view", main.getLookAt());
		
		geoPass.bind();
		//render geometry
		for(Resource mesh : meshes){
			Renderable castMesh = ((Renderable) mesh);
			geoPass.setUniform("model", castMesh.getModelView());
			Renderer.render(castMesh);
		}
		geoPass.unbind();
		
		gbuffer.setupLightingPass();
		
		for(Light light : lights){
			Renderable mesh = light.getVolume();
			gbuffer.stencilPass();
			//render the light volume for stenciling
			Renderer.render(mesh);
			
			//render the volume for calculation
			gbuffer.lightPass();
			Renderer.render(mesh);
		}
		glDepthMask(true);//enable writing into the depth buffer
		glDisable(GL_STENCIL_TEST);//enable stencil testing
		glDisable(GL_BLEND);
	}
}
