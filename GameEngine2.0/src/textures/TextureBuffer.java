package textures;

import textures.enums.InternalFormat;
import textures.enums.TextureType;
import gldata.BufferObject;
import static org.lwjgl.opengl.GL45.*;

public class TextureBuffer extends Texture {

	/**
	 * Constructs a buffer texture with the given {@code InternalFormat}.
	 * 
	 * @param format Internal format of the texture buffer
	 */
	public TextureBuffer(InternalFormat format) {
		super(TextureType.BUFFER, format, 1);
	}
	
	/**
	 * Constructs a buffer texture with the given {@code InternalFormat} and {@code BufferObject}.
	 * 
	 * @param format Internal format of the texture buffer
	 * @param buffer BufferObject to attach to this buffer texture
	 */
	public TextureBuffer(InternalFormat format, BufferObject buffer) {
		super(TextureType.BUFFER, format, 1);
		glTextureBuffer(id, iformat.value, buffer.getId());
	}

	/**
	 * Attaches a {@code BufferObject} to this buffer texture
	 * 
	 * @param buffer Buffer object to attach to this buffer texture
	 */
	public void attachBuffer(BufferObject buffer){
		glTextureBuffer(id, iformat.value, buffer.getId());
	}
}
