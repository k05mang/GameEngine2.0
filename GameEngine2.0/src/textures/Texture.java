package textures;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;
//import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL31.*;
//import static org.lwjgl.opengl.GL32.*;
//import static org.lwjgl.opengl.GL33.*;
//import static org.lwjgl.opengl.GL40.*;
//import static org.lwjgl.opengl.GL41.*;
//import static org.lwjgl.opengl.GL42.*;
//import static org.lwjgl.opengl.GL43.*;
//import static org.lwjgl.opengl.GL44.*;
//import static org.lwjgl.opengl.GL45.*;

public abstract class Texture {

	protected int id, levels_samples;
	protected TextureType type;
	protected InternalFormat iformat;
	
	public Texture(TextureType texType, InternalFormat format, int levels_samples){
		type = texType;
		id = glCreateTextures(type.value);
		iformat = format;
		this.levels_samples = Math.max(0,  levels_samples);//cap it
	}
	
	/**
	 * Binds this texture object to the texture target it is associated with
	 */
	public void bind(){
		glBindTexture(type.value, id);
	}
	
	/**
	 * Binds this texture to the given texture unit but does not set the texture unit to be active.
	 * 
	 * @param texUnit Texture unit to bind this texture object to
	 */
	public void bindToTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, id);
	}
	
	/**
	 * Unbinds the texture bound at the given texture unit
	 * 
	 * @param texUnit Texture unit to unbind this texture object to
	 */
	public void unbindFromTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, 0);
	}
	
	/**
	 * Unbinds this texture from the target this texture is associated with
	 */
	public void unbind(){
		glBindTexture(type.value, 0);
	}
	
	/**
	 * Generates mipmaps for this texture, if the texture is a rectangle or buffer type this function does nothing
	 */
	public void genMipMaps(){
		if(type != TextureType.RECTANGLE && type != TextureType.BUFFER){
			glGenerateTextureMipmap(id);
		}
	}
	
	/**
	 * Sets a given parameter of this texture to the given constant value
	 * 
	 * @param target Parameter to set for this texture
	 * @param value Value to set the parameter to
	 */
	public void setParam(TexParam target, TexParamEnum value){
		glTextureParameteri(id, target.value, value.value);
	}

	/**
	 * Sets a given parameter of this texture to the given integer value
	 * 
	 * @param target Parameter to set for this texture
	 * @param value Value to set the parameter to
	 */
	public void setParam(TexParam target, int value){
		glTextureParameteri(id, target.value, value);
	}

	/**
	 * Sets a given parameter of this texture to the given integer value
	 * 
	 * @param target Parameter to set for this texture
	 * @param value Value to set the parameter to
	 */
	public void setParam(TexParam target, float value){
		glTextureParameterf(id, target.value, value);
	}
	
	/**
	 * Sets the swizzle of the rgba components of this texture
	 * 
	 * @param r Red component swizzle value, this specifies what channel a call to r, in the shader, will read from
	 * @param g Green component swizzle value, this specifies what channel a call to g, in the shader, will read from
	 * @param b Blue component swizzle value, this specifies what channel a call to b, in the shader, will read from
	 * @param a Alpha component swizzle value, this specifies what channel a call to a, in the shader, will read from
	 */
	public void setSwizzle(TexParamEnum r, TexParamEnum g, TexParamEnum b, TexParamEnum a){
		IntBuffer values = BufferUtils.createIntBuffer(4);
		values.put(r.value);
		values.put(g.value);
		values.put(b.value);
		values.put(a.value);
		values.flip();
		glTextureParameteriv(id, TexParam.SWIZZLE_RGBA.value, values);
	}
	
	/**
	 * Sets the border color for this texture to the given values without conversion to floating point
	 * 
	 * @param r Red value of the border color
	 * @param g Green value of the border color
	 * @param b Blue value of the border color
	 * @param a Alpha value of the border color
	 */
	public void setBorderColor(int r, int g, int b, int a){
		IntBuffer values = BufferUtils.createIntBuffer(4);
		values.put(r);
		values.put(g);
		values.put(b);
		values.put(a);
		values.flip();
		glTextureParameterIiv(id, TexParam.SWIZZLE_RGBA.value, values);
	}

	/**
	 * Sets the border color for this texture to the given values
	 * 
	 * @param r Red value of the border color
	 * @param g Green value of the border color
	 * @param b Blue value of the border color
	 * @param a Alpha value of the border color
	 */
	public void setBorderColor(float r, float g, float b, float a){
		FloatBuffer values = BufferUtils.createFloatBuffer(4);
		values.put(r);
		values.put(g);
		values.put(b);
		values.put(a);
		values.flip();
		glTextureParameterfv(id, TexParam.SWIZZLE_RGBA.value, values);
	}

	/**
	 * Uses the given buffer to set the pixel data of the texture on the GPU
	 * 
	 * @param pixels Data buffer of pixels to send to the GPu for this texture
	 * @param format Format of the pixel data
	 * @param type Data type of the pixel data being passed
	 * @param level Level of the texture mipmap or array to set
	 */
//	public abstract void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public abstract void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public abstract void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public abstract void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public abstract void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level);
}
