package framebuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.glStencilOpSeparate;
import textures.Texture2D;
import textures.enums.InternalFormat;

public class Gbuffer {

	private FBO framebuffer;
	private Texture2D diffuse, position, normal, specular, lightAccum, depth;
	private int width, height;
	
	public Gbuffer(int width, int height) {
		this.width = width;
		this.height = height;
		framebuffer = new FBO();//create framebuffer
		//create data buffers
		diffuse = new Texture2D(InternalFormat.RGB32F, 0, width, height);
		position = new Texture2D(InternalFormat.RGBA32F, 0, width, height);//specular power can be kept in the alpha channel
		normal = new Texture2D(InternalFormat.RGBA32F, 0, width, height);//specular intensity can be kept in the alpha channel
		lightAccum = new Texture2D(InternalFormat.RGBA32F, 0, width, height);
		specular = new Texture2D(InternalFormat.R32F, 0, width, height);
		depth = new Texture2D(InternalFormat.D24_S8, 0, width, height);
		
		//attach data buffers
		framebuffer.attachColor(0, diffuse, 0);
		framebuffer.attachColor(1, position, 0);
		framebuffer.attachColor(2, normal, 0);
		framebuffer.attachColor(3, specular, 0);
		framebuffer.attachColor(4, lightAccum, 0);
		framebuffer.attachDepthStencil(depth, 0);
		
		framebuffer.setDrawBuffers(0, 1, 2);
	}

	public void geoPass(){
//		specular.unbindFromTextureUnit(2);//1 will be the engine target for deferred lightAccum texture
		framebuffer.bind(true, true);
		framebuffer.setDrawBuffers(0,1,2);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);//clear the buffer for a new pass
		glDepthMask(true);//enable writing into the depth buffer
		glDisable(GL_STENCIL_TEST);//disable stencil testing
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}
	
	public void setupLightingPass(){
		glDepthMask(false);//disable writing into the depth buffer
		glEnable(GL_STENCIL_TEST);//enable stencil testing
		glEnable(GL_BLEND);
	    glBlendEquation(GL_FUNC_ADD);
	    glBlendFunc(GL_ONE, GL_ONE);
		position.bindToTextureUnit(0);
		normal.bindToTextureUnit(1);
		depth.bindToTextureUnit(2);
		framebuffer.setDrawBuffers(3,4);
		glClearColor(0,0,0, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		glClearColor(.4f, .6f, .8f, 1);
	}
	
	public void stencilPass(){
		framebuffer.setDrawBuffers();
		glStencilFunc(GL_ALWAYS, 0, 0);//have the stencil test always pass
		glClear(GL_STENCIL_BUFFER_BIT);//clear the previous stencil value from the buffer
		glEnable(GL_DEPTH_TEST);
		glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR, GL_KEEP);
		glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR, GL_KEEP);
		glDisable(GL_CULL_FACE);
	}
	
	public void lightPass(){
		framebuffer.setDrawBuffers(3,4);
		glStencilFunc(GL_NOTEQUAL, 0, 0xff);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
	}
	
	public void finalPass(){
		glDepthMask(true);//enable writing into the depth buffer
		glDisable(GL_STENCIL_TEST);//enable stencil testing
		glDisable(GL_BLEND);
		framebuffer.unbind();//set the default framebuffer as the current framebuffer
		//bind the textures for read back
		diffuse.bindToTextureUnit(0);//0 will be the engine target for deferred diffuse texture
		lightAccum.bindToTextureUnit(1);//1 will be the engine target for deferred lightAccum texture
		specular.bindToTextureUnit(2);//1 will be the engine target for deferred lightAccum texture
		glEnable(GL_DEPTH_TEST);
//		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}
	
	public void bindDiffuse(){
		diffuse.bindToTextureUnit(0);
	}
	
	public void bindPosition(){
		position.bindToTextureUnit(0);
	}
	
	public void bindNormal(){
		normal.bindToTextureUnit(0);
	}
	
	public void bindLight(){
		lightAccum.bindToTextureUnit(0);
	}
	
	public void unbind(){
		framebuffer.unbind();
	}
	
	public void delete(){
		framebuffer.delete();
		diffuse.delete(); 
		position.delete(); 
		normal.delete(); 
		lightAccum.delete(); 
		specular.delete(); 
		depth.delete();
	}
}