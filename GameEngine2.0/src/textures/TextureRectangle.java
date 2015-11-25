package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class TextureRectangle extends Texture implements BasicTexture {

	public TextureRectangle(InternalFormat format, int width, int height) {
		super(TextureType.RECTANGLE, format, 0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level) {
		// TODO Auto-generated method stub

	}

}
