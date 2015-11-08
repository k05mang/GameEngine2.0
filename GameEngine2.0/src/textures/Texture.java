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

	protected int id;
	protected TextureType type;
	
	public Texture(TextureType texType){
		type = texType;
		id = glCreateTextures(type.value);
	}
	
	public void bind(){
		glBindTexture(type.value, id);
	}
	
	public void bindToTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, id);
	}
	
	public void unbindFromTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, 0);
	}
	
	public void unbind(){
		glBindTexture(type.value, 0);
	}
	
	public void setParam(TexParam target, TexParamEnum value){
		glTextureParameteri(id, target.value, value.value);
	}
	
	public void setParam(TexParam target, int value){
		glTextureParameteri(id, target.value, value);
	}
	
	public void setParam(TexParam target, float value){
		glTextureParameterf(id, target.value, value);
	}
	
	public void setSwizzle(TexParamEnum r, TexParamEnum g, TexParamEnum b, TexParamEnum a){
		IntBuffer values = BufferUtils.createIntBuffer(4);
		values.put(r.value);
		values.put(g.value);
		values.put(b.value);
		values.put(a.value);
		values.flip();
		glTextureParameteriv(id, TexParam.SWIZZLE_RGBA.value, values);
	}
	
	public void setBorderColor(int v1, int v2, int v3, int v4){
		IntBuffer values = BufferUtils.createIntBuffer(4);
		values.put(v1);
		values.put(v2);
		values.put(v3);
		values.put(v4);
		values.flip();
		glTextureParameterIiv(id, TexParam.SWIZZLE_RGBA.value, values);
	}
	
	public void setBorderColor(float v1, float v2, float v3, float v4){
		FloatBuffer values = BufferUtils.createFloatBuffer(4);
		values.put(v1);
		values.put(v2);
		values.put(v3);
		values.put(v4);
		values.flip();
		glTextureParameterfv(id, TexParam.SWIZZLE_RGBA.value, values);
	}

	public abstract void bufferData(ByteBuffer pixels);
	public abstract void bufferData(ShortBuffer pixels);
	public abstract void bufferData(IntBuffer pixels);
	public abstract void bufferData(FloatBuffer pixels);
	public abstract void bufferData(DoubleBuffer pixels);
}
