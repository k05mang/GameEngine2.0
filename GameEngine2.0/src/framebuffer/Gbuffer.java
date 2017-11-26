package framebuffer;
import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL14.GL_DECR_WRAP;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.GL_INCR_WRAP;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.glStencilOpSeparate;
import static org.lwjgl.opengl.GL45.glBlitNamedFramebuffer;

import java.util.HashMap;

import textures.Texture2D;
import textures.enums.InternalFormat;

public class GBuffer {

	private FBO framebuffer;
//	private Texture2D diffuse, position, normal, lighting, depth;
	private HashMap<String, Texture2D> buffers;
	private int width, height;
	
	public GBuffer(int width, int height) {
		buffers = new HashMap<String, Texture2D>();
		this.width = width;
		this.height = height;
		framebuffer = new FBO();//create framebuffer
		//create data buffers
		buffers.put("diffuse", new Texture2D(InternalFormat.RGBA32F, 0, width, height));
		buffers.put("position", new Texture2D(InternalFormat.RGBA32F, 0, width, height));//specular power can be kept in the alpha channel
		buffers.put("normal", new Texture2D(InternalFormat.RGBA32F, 0, width, height));//specular intensity can be kept in the alpha channel
		buffers.put("lighting", new Texture2D(InternalFormat.RGB32F, 0, width, height));
		buffers.put("depth", new Texture2D(InternalFormat.D24_S8, 0, width, height));
		
		//attach data buffers
		framebuffer.attachColor(0, buffers.get("diffuse"), 0);
		framebuffer.attachColor(1, buffers.get("position"), 0);
		framebuffer.attachColor(2, buffers.get("normal"), 0);
		framebuffer.attachColor(3, buffers.get("lighting"), 0);
		framebuffer.attachDepthStencil(buffers.get("depth"), 0);
		
		framebuffer.setDrawBuffers(0, 1, 2);
	}

	/**
	 * Binds the appropriate textures and sets the context state for rendering the geo pass
	 */
	public void geoPass(){
		framebuffer.bind(true, true);
		framebuffer.setDrawBuffers(0,1,2);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);//clear the buffer for a new pass
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
	    //set position and normal textures to be readable for the lighting pass
	    buffers.get("position").bindToTextureUnit(0);
	    buffers.get("normal").bindToTextureUnit(1);
		//set the lighting buffer to render in to
		framebuffer.setDrawBuffers(3);
		glClear(GL_COLOR_BUFFER_BIT);
		glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP);
		glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP);
	}
	
	public void stencilPass(){
		//disable render buffers
		framebuffer.setDrawBuffers();
		glStencilFunc(GL_ALWAYS, 0, 0);//have the stencil test always pass
		glClear(GL_STENCIL_BUFFER_BIT);//clear the previous stencil value from the buffer
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
	}
	
	public void lightPass(){
		framebuffer.setDrawBuffers(3);
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
		buffers.get("diffuse").bindToTextureUnit(0);//0 will be the engine target for deferred diffuse texture
		buffers.get("lighting").bindToTextureUnit(1);
		glEnable(GL_DEPTH_TEST);
//		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}
	
	public void bindBuffer(String buffer){
		buffers.get(buffer).bindToTextureUnit(0);
	}
	
//	public void bindDiffuse(){
//		buffers.get("diffuse").bindToTextureUnit(0);
//	}
//	
//	public void bindPosition(){
//		buffers.get("position").bindToTextureUnit(0);
//	}
//	
//	public void bindNormal(){
//		buffers.get("normal").bindToTextureUnit(0);
//	}
	
	/**
	 * Sets iterates over this gbuffers list of textures and blits the main framebuffer to render all the buffers
	 */
	public void debug(){
		//get the width and height required by the resulting screens
		//use the square root to allow for dynamically sized squares to show each screen evenly
		int divisor = (int)Math.floor(Math.sqrt(buffers.size()));
		int startX = 0, startY = 0, 
				endX = width/divisor, endY = height/divisor;
		//iterate over the list of buffers minus 1 to account for not getting the depth buffer
		for(int curBuffer = 0; curBuffer < buffers.size()-1; curBuffer++){
			//set the framebuffers current read buffer
			framebuffer.setReadBuffer(curBuffer);
			//blit the framebuffer
			glBlitNamedFramebuffer(framebuffer.getId(), 0, 
					0, 0, width, height, 
					startX+endX*(curBuffer%divisor), startY+endY*(curBuffer/divisor), 
					endX*((curBuffer%divisor)+1), endY*((curBuffer/divisor)+1), 
					GL_COLOR_BUFFER_BIT, textures.enums.TexParamEnum.LINEAR.value);
		}
		//unset the read buffer
		framebuffer.unsetReadBuffer();
	}
	
	public void unbind(){
		framebuffer.unbind();
	}
	
	public void delete(){
		framebuffer.delete();
		for(Texture2D buffer : buffers.values()){
			buffer.delete();
		}
	}
}
