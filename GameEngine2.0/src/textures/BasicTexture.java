package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface BasicTexture {

	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level);
}
