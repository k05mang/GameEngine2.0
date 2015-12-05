package textures.loaders;

import java.nio.ByteBuffer;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public class TextureData {

	int width, height;
	BaseFormat format;
	TexDataType dataType;
	ByteBuffer data;
	
	public TextureData(int width, int height, BaseFormat format, TexDataType type, ByteBuffer data) {
		this.width = width;
		this.height = height;
		this.format = format;
		dataType = type;
		this.data = data;
	}

}
