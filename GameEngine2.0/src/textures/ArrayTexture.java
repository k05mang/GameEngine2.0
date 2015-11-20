package textures;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.DoubleBuffer;

public interface ArrayTexture {

	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int index, int level);
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int index, int level);
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int index, int level);
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int index, int level);
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int index, int level);
}
