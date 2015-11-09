package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.opengl.GL45.*;

public class Texture1D extends Texture {
	protected int width;
	
	public Texture1D(TextureType texType, InternalFormat format, int levels_samples, int width) {
		super(texType, format, levels_samples);
		this.width = width;
		glTextureStorage1D(id, levels_samples, format.value, width);
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
