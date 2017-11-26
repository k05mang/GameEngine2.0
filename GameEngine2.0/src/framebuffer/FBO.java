package framebuffer;

import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
//import static org.lwjgl.opengl.GL33.*;
//import static org.lwjgl.opengl.GL40.*;
//import static org.lwjgl.opengl.GL41.*;
//import static org.lwjgl.opengl.GL42.*;
//import static org.lwjgl.opengl.GL43.*;
//import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import textures.CubeMapFace;
import textures.Texture;
import textures.Texture1DArray;
import textures.Texture2DArray;
import textures.Texture2DMSArray;
import textures.Texture3D;
import textures.TextureCubeMap;
import textures.TextureCubeMapArray;
import core.Resource;

public class FBO implements Resource {
	private int id, bindTarget;
	
	/**
	 * Constructs a Framebuffer object on the GPU
	 */
	public FBO(){
		id = glCreateFramebuffers();
		bindTarget = GL_FRAMEBUFFER;
	}
	
	/**
	 * Binds the Framebuffer to the specified buffer targets for reading or writing or both.
	 * If both read and draw are false then the buffer will be unbound from its previous buffer target.
	 * 
	 * @param read True if the buffer should be bound to the read framebuffer, false if not
	 * @param draw True if the buffer should be bound to the draw framebuffer. false if not
	 */
	public void bind(boolean read, boolean draw){
		//unbind the framebuffer from it's current buffer
		glBindFramebuffer(bindTarget, 0);
		if(read && draw){
			glBindFramebuffer(GL_FRAMEBUFFER, id);
			bindTarget = GL_FRAMEBUFFER;
		}else if(draw){
			glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
			bindTarget = GL_DRAW_FRAMEBUFFER;
		}else if(read){
			glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
			bindTarget = GL_READ_FRAMEBUFFER;
		}else{
			glBindFramebuffer(bindTarget, 0);
		}
	}
	
	/**
	 * Gets the id that identifies this framebuffer object on the GPU
	 * 
	 * @return Id if the framebuffer exists, otherwise 0
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Unbinds this framebuffer from its previous framebuffer target effectively unbinding it from the context
	 */
	public void unbind(){
		glBindFramebuffer(bindTarget, 0);
	}
	
	/**
	 * Sets a parameter value for the framebuffer
	 * 
	 * @param param Parameter to set
	 * @param value Value to set the parameter to
	 */
	public void setParam(FrameBufferParam param, int value){
		glNamedFramebufferParameteri(id, param.value, value);
	}
	
	/**
	 * Sets the read buffer of this framebuffer object to the given attachment
	 * @param attachment
	 */
	public void setReadBuffer(int attachment){
		glNamedFramebufferReadBuffer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment));
	}
	
	/**
	 * Sets the framebuffers read buffer to the default read buffer 0
	 */
	public void unsetReadBuffer(){
		glNamedFramebufferReadBuffer(id, 0);
	}
	
//---------------------COLOR ATTACHMENT FUNCTIONS-------------------------------------
	
	/**
	 * Attaches the given Renderbuffer object to the color attachment point (@code attachment} of this Framebuffer
	 * 
	 * @param attachment Color attachment point to bind the Renderbuffer to
	 * @param buffer Renderbuffer to bind to this framebuffer's color attachment
	 */
	public void attachColor(int attachment, RenderBuffer buffer){
		glNamedFramebufferRenderbuffer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), GL_RENDERBUFFER, buffer.id);
	}
	
	/**
	 * Attaches the mipmap {@code level} of {@code texture} to this framebuffers color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param level Mipmap level of the texture to attach to the framebuffer
	 */
	public void attachColor(int attachment, Texture texture, int level){
		glNamedFramebufferTexture(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level);
	}
	
	/**
	 * Attaches the mipmap {@code level} of the {@code layer} of {@code texture} to this framebuffers color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the attachment point
	 * @param layer Layer of the texture to bind to the attachment point
	 * @param level Mipmap level to bind to the attachment point
	 */
	public void attachColor(int attachment, Texture3D texture, int layer, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, layer);
	}
	
	/**
	 * Attaches the mipmap {@code level} of the {@code index} of the texture array {@code texture} to this framebuffers color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param index Index of {@code texture} to bind to the attachment point
	 * @param level Mipmap level of {@code texture} at {@code index} to bind to the attachment point
	 */
	public void attachColor(int attachment, Texture1DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, index);
	}
	
	/**
	 * Attaches the mipmap {@code level} of the {@code index} of the texture array {@code texture} to this framebuffers color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param index Index of {@code texture} to bind to the attachment point
	 * @param level Mipmap level of {@code texture} at {@code index} to bind to the attachment point
	 */
	public void attachColor(int attachment, Texture2DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, index);
	}

	/**
	 * Attaches the mipmap {@code level} of the {@code index} of the texture array {@code texture} to this framebuffers color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param index Index of {@code texture} to bind to the attachment point
	 * @param level Mipmap level of {@code texture} at {@code index} to bind to the attachment point
	 */
	public void attachColor(int attachment, Texture2DMSArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, index);
	}
	
	/**
	 * Attaches the mipmap {@code level} of the {@code face} of the cube map {@code texture} to this framebuffers 
	 * color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param face Cube map face to attach to this framebuffer
	 * @param level Mipmap level of texture to attach to this framebuffer
	 */
	public void attachColor(int attachment, TextureCubeMap texture, CubeMapFace face, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, face.layer);
	}
	
	/**
	 * Attaches the mipmap {@code level} of the {@code face} of the cube map at {@code index} of the {@code texture} to this framebuffers 
	 * color attachment point
	 * 
	 * @param attachment Color attachment point to attach the texture to in the framebuffer
	 * @param texture Texture to attach to the framebuffer
	 * @param face Cube map face to attach to this framebuffer
	 * @param index Index of the texture to bind to this framebuffer
	 * @param level Mipmap level of texture to attach to this framebuffer
	 */
	public void attachColor(int attachment, TextureCubeMapArray texture, CubeMapFace face, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_COLOR_ATTACHMENT0+Math.max(0, attachment), texture.getId(), level, 6*index+face.layer);
	}
	
//---------------------DEPTH ATTACHMENT FUNCTIONS-------------------------------------
	
	public void attachDepth(RenderBuffer buffer){
		glNamedFramebufferRenderbuffer(id, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, buffer.id);
	}
	
	public void attachDepth(Texture texture, int level){
		glNamedFramebufferTexture(id, GL_DEPTH_ATTACHMENT, texture.getId(), level);
	}
	
	public void attachDepth(Texture3D texture, int layer, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, layer);
	}
	
	public void attachDepth(Texture1DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepth(Texture2DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepth(Texture2DMSArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepth(TextureCubeMap texture, CubeMapFace face, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, face.layer);
	}
	
	public void attachDepth(TextureCubeMapArray texture, CubeMapFace face, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_ATTACHMENT, texture.getId(), level, 6*index+face.layer);
	}
	
//---------------------STENCIL ATTACHMENT FUNCTIONS-------------------------------------
	
	public void attachStencil(RenderBuffer buffer){
		glNamedFramebufferRenderbuffer(id, GL_STENCIL_ATTACHMENT, GL_RENDERBUFFER, buffer.id);
	}
	
	public void attachStencil(Texture texture, int level){
		glNamedFramebufferTexture(id, GL_STENCIL_ATTACHMENT, texture.getId(), level);
	}
	
	public void attachStencil(Texture3D texture, int layer, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, layer);
	}
	
	public void attachStencil(Texture1DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachStencil(Texture2DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachStencil(Texture2DMSArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachStencil(TextureCubeMap texture, CubeMapFace face, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, face.layer);
	}
	
	public void attachStencil(TextureCubeMapArray texture, CubeMapFace face, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_STENCIL_ATTACHMENT, texture.getId(), level, 6*index+face.layer);
	}

//---------------------DEPTH_STENCIL ATTACHMENT FUNCTIONS-------------------------------------
		
	public void attachDepthStencil(RenderBuffer buffer){
		glNamedFramebufferRenderbuffer(id, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, buffer.id);
	}
	
	public void attachDepthStencil(Texture texture, int level){
		glNamedFramebufferTexture(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level);
	}
	
	public void attachDepthStencil(Texture3D texture, int layer, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, layer);
	}
	
	public void attachDepthStencil(Texture1DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepthStencil(Texture2DArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepthStencil(Texture2DMSArray texture, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, index);
	}
	
	public void attachDepthStencil(TextureCubeMap texture, CubeMapFace face, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, face.layer);
	}
	
	public void attachDepthStencil(TextureCubeMapArray texture, CubeMapFace face, int index, int level){
		glNamedFramebufferTextureLayer(id, GL_DEPTH_STENCIL_ATTACHMENT, texture.getId(), level, 6*index+face.layer);
	}
	
	public void setDrawBuffers(int... attachments){
		if(attachments.length == 0){
			glNamedFramebufferDrawBuffers(id, GL_NONE);
		}else{
			IntBuffer buffers = BufferUtils.createIntBuffer(attachments.length);
			for(int attachment : attachments){
				buffers.put(GL_COLOR_ATTACHMENT0+attachment);
			}
			buffers.flip();
			glNamedFramebufferDrawBuffers(id, buffers);
		}
	}
	
	@Override
	public void delete() {
		glDeleteFramebuffers(id);
		id = 0;
	}

	/**
	 * Determines whether or not this Framebuffer object is Framebuffer complete as defined by the GL
	 * 
	 * @return True if the Framebuffer is complete, false otherwise
	 */
	public boolean isComplete(){
		if(glCheckNamedFramebufferStatus(id, GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Determines the status of the Framebuffer object and retuns a String representing the status
	 * of the Framebuffer.
	 * 
	 * @return String representing the current status of the Framebuffer object on the GPU
	 */
	public String getStatus(){
		switch(glCheckNamedFramebufferStatus(id, GL_FRAMEBUFFER)){
			case GL_FRAMEBUFFER_UNDEFINED:
				return "GL_FRAMEBUFFER_UNDEFINED";
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				return "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				return "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				return "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				return "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
			case GL_FRAMEBUFFER_UNSUPPORTED:
				return "GL_FRAMEBUFFER_UNSUPPORTED";
			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				return "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
			case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
				return "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
			default:
				return "GL_FRAMEBUFFER_COMPLETE";
		}
	}
}
