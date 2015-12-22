package textures.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class TGADecoder extends ImageParser {

	private final int COLOR_MAPPED = 1,
			TRUE_COLOR = 2,
			GREYSCALE = 3,
			RLE_COLOR_MAP = 9,
			RLE_TRUE_COLOR = 10,
			TLE_GREYSCALE = 11;//there may or may not be a 32 and 33 type this is based on the TGA specification
	private int imageType;
	
	public TGADecoder(File file) throws IOException {
		super(file);
		//byte ordering is from least to most signif
		int idLength = 0xff & imageStream.readByte();
		boolean hasColorMap = imageStream.readByte() == 1;
		imageType = imageStream.readByte();
	}

	@Override
	public ByteBuffer parse() throws IOException, DataFormatException {
		// TODO Auto-generated method stub
		return null;
	}

}
