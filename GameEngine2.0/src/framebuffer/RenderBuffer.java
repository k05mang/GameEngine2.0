package framebuffer;
import core.Resource;
import textures.enums.InternalFormat;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL31.*;
//import static org.lwjgl.opengl.GL32.*;
//import static org.lwjgl.opengl.GL33.*;
//import static org.lwjgl.opengl.GL40.*;
//import static org.lwjgl.opengl.GL41.*;
//import static org.lwjgl.opengl.GL42.*;
//import static org.lwjgl.opengl.GL43.*;
//import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;

public class RenderBuffer implements Resource{
	protected int id, width, height, samples;
	protected InternalFormat format;
	
	/**
	 * Constructs a Renderbuffer for use as th logical buffer of a Framebuffer.
	 * The RenderBuffer will have the given {@code width} and {@code height}, and
	 * the given {@code format}.
	 * 
	 * @param width Width of the Renderbuffer
	 * @param height Height of the Renderbuffer
	 * @param format Format of the Renderbuffer
	 */
	public RenderBuffer(int width, int height, InternalFormat format){
		this(width, height, format, 0);
	}
	
	/**
	 * Constructs a Renderbuffer for use as th logical buffer of a Framebuffer.
	 * The RenderBuffer will have the given {@code width} and {@code height}, and
	 * the given {@code format}, and will use the given number of {@code samples}
	 * for multisample rendering.
	 * 
	 * @param width Width of the Renderbuffer
	 * @param height Height of the Renderbuffer
	 * @param format Format of the Renderbuffer
	 * @param samples Number of samples to use with reading the buffer
	 */
	public RenderBuffer(int width, int height, InternalFormat format, int samples){
		id = glCreateRenderbuffers();
		this.width = width;
		this.height = height;
		this.samples = samples;
		this.format = format;
		glNamedRenderbufferStorageMultisample(id, samples, format.value, width, height);
	}
	
	/**
	 * Binds the Renderbuffer to the context
	 */
	public void bind(){
		glBindRenderbuffer(GL_RENDERBUFFER, id);
	}
	
	/**
	 * Unbinds the Renderbuffer from the context
	 */
	public void unbind(){
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
	}
	
	/**
	 * Resizes the Renderbuffer to the dimensions given
	 * 
	 * @param width New width of the Renderbuffer
	 * @param height New height of the Renderbuffer
	 */
	public void resize(int width, int height){
		this.width = width;
		this.height = height;
		glNamedRenderbufferStorageMultisample(id, samples, format.value, width, height);
	}

	@Override
	public void delete() {
		glDeleteRenderbuffers(id);
	}
}
