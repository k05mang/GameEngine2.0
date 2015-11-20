package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.opengl.GL45.*;

public class Texture1D extends Texture {
	protected int width;
	
	public Texture1D(InternalFormat format, int levels, int width) {
		super(TextureType._1D, format, levels);
		this.width = width;
		glTextureStorage1D(id, levels, format.value, width);
	}

	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

}
