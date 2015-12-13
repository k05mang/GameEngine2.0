package textures.loaders;

import java.io.File;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import textures.CubeMapFace;
import textures.Texture;
import textures.Texture1D;
import textures.Texture1DArray;
import textures.Texture2D;
import textures.Texture2DArray;
import textures.Texture3D;
import textures.TextureCubeMap;
import textures.TextureCubeMapArray;
import textures.TextureRectangle;
import textures.enums.BaseFormat;
import textures.enums.InternalFormat;
import textures.enums.TexDataType;
import textures.enums.TextureType;

public abstract class ImageLoader {

	public static Texture load(InternalFormat iformat, TextureType type,  String file){
		return load(iformat, type, new File(file));
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, File file){
		Texture result = null;
		try {
			//check to make sure the file has the png extension
			if(file.getName().toLowerCase().contains(".png")){
				result = loadPNG(file, iformat, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//if we failed to load the image use a default texture
			result = loadDefault(iformat, type);
		}
		return result;
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, String... files){
		ArrayList<File> passThrough = new ArrayList<File>();
		for(String file : files){
			passThrough.add(new File(file));
		}
		try {
			return loadPNG(iformat, type, passThrough);
		} catch (Exception e) {
			e.printStackTrace();
			return loadDefault(iformat, type);
		}
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, File... files){
		ArrayList<File> passThrough = new ArrayList<File>();
		for(File file : files){
			passThrough.add(file);
		}
		try {
			return loadPNG(iformat, type, passThrough);
		} catch (Exception e) {
			e.printStackTrace();
			return loadDefault(iformat, type);
		}
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, ArrayList<File> files){
		try {
			return loadPNG(iformat, type, files);
		} catch (Exception e) {
			e.printStackTrace();
			return loadDefault(iformat, type);
		}
	}
	
	public static Texture loadPNG(InternalFormat iformat, TextureType type, ArrayList<File> files) throws Exception{
		Texture result = null;
		if(files.isEmpty()){
			return loadDefault(iformat, type);
		}else{
			ArrayList<PNG> decoders = new ArrayList<PNG>();
			PNG decoder = new PNG(files.get(0));
			decoders.add(decoder);
			int width = decoder.getWidth();
			int height = decoder.getHeight();
			
			//while reading in the files determine if they are all the same dimensions and if not handle it
			for(int curFile = 1; curFile < files.size(); curFile++){
				PNG parser = new PNG(files.get(curFile));
				//check if they have the same dimension
				if(width != parser.getWidth() || height != parser.getHeight()){
					System.err.println("Failed to load image array in ImageLoader, failed on load of file: "+files.get(0).getName());
					return loadDefault(iformat, type);
				}else{//else add it to the array of decoders
					decoders.add(parser);
				}
			}
			//initialize the right texture type
			switch(type){
				case CUBE_MAP:
					result = new TextureCubeMap(iformat, 1, Math.max(width, height));
					break;
				case CUBE_MAP_ARRAY:
					result = new TextureCubeMapArray(iformat, 1, Math.max(width, height), files.size()/6);
					break;
				case _2D_ARRAY:
					result = new Texture2DArray(iformat, 1, width, height, files.size());
					break;
				case _3D:
					result = new Texture3D(iformat, 1, width, height, files.size());
					break;
			}
			
			//loop through the decoders and upload the data to the texture
			for(int curDecoder = 0; curDecoder < decoders.size(); curDecoder++){
				PNG decode = decoders.get(curDecoder);
				switch(type){
					case CUBE_MAP:
						//determine which face of the cube map we are on
						switch(curDecoder){
							case 0:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.POS_X);
								break;
							case 1:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.NEG_X);
								break;
							case 2:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.POS_Y);
								break;
							case 3:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.NEG_Y);
								break;
							case 4:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.POS_Z);
								break;
							case 5:
								((TextureCubeMap)result).bufferData(decode.parse(), decode.format, decode.getType(), 0, CubeMapFace.NEG_Z);
								break;
						}
						break;
					case CUBE_MAP_ARRAY:
						//determine which face of the cube map we are on
						switch(curDecoder%6){
							case 0:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.POS_X);
								break;
							case 1:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.NEG_X);
								break;
							case 2:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.POS_Y);
								break;
							case 3:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.NEG_Y);
								break;
							case 4:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.POS_Z);
								break;
							case 5:
								((TextureCubeMapArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder%6, 0, CubeMapFace.NEG_Z);
								break;
						}
						break;
					case _2D_ARRAY:
						((Texture2DArray)result).bufferData(decode.parse(), decode.format, decode.getType(), curDecoder, 0);
						break;
					case _3D:
						((Texture3D)result).subImage(decode.parse(), decode.format, decode.getType(), 0, 0, 0, curDecoder, width, height, curDecoder+1);
						break;
				}
			}
		}
		return result;
	}
	
//	private static Texture loadImage(File file, InternalFormat iformat){
//		try {
//			PNG image = new PNG(file);
//			Texture result = new Texture2D(iformat, 1, image.getWidth(), image.getHeight());
//			((Texture2D)result).bufferData(image.parse(), image.getFormat(), TexDataType.UBYTE, 0);
//			return result;
////			System.out.println(image.parse().remaining());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	private static Texture loadPNG(File file, InternalFormat iformat, TextureType type) throws Exception{
		PNG decoder = new PNG(file);
		
		int width = decoder.getWidth();
		int height = decoder.getHeight();
		Texture result = null;
		
		switch(type){
			case RECTANGLE:
				result = new TextureRectangle(iformat, width, height);
				((TextureRectangle)result).bufferData(decoder.parse(), decoder.format, decoder.getType(), 0);
				break;
			case _1D:
				result = new Texture1D(iformat, 1, width*height);
				((Texture1D)result).bufferData(decoder.parse(), decoder.format, decoder.getType(), 0);
				break;
			case _1D_ARRAY:
				result = new Texture1DArray(iformat, 1, width, height);
				((Texture1DArray)result).bufferData(decoder.parse(), decoder.format, decoder.getType(), 0, height, 0);
				break;
			case _2D:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(decoder.parse(), decoder.format, decoder.getType(), 0);
				break;
			default:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(decoder.parse(), decoder.format, decoder.getType(), 0);
				break;
		}
		return result;
	}
	
	public static Texture loadDefault(InternalFormat iformat, TextureType type){
		Texture result = null;
		int defaultWidth = 8, defaultHeight = 8;
		ByteBuffer pixels = ImageParser.getDefault(defaultWidth, defaultHeight, type == TextureType.CUBE_MAP || type == TextureType.CUBE_MAP_ARRAY, true);

		switch(type){
			case RECTANGLE:
				result = new TextureRectangle(iformat, defaultWidth, defaultHeight);
				((TextureRectangle)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			case _1D:
				result = new Texture1D(iformat, 1, defaultWidth*defaultHeight);
				((Texture1D)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			case _1D_ARRAY:
				result = new Texture1DArray(iformat, 1, defaultWidth, defaultHeight);
				((Texture1DArray)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0, defaultHeight, 0);
				break;
			case _2D:
				result = new Texture2D(iformat, 1, defaultWidth, defaultHeight);
				((Texture2D)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
//			case BUFFER:
//				break;
			case CUBE_MAP:
				result = new TextureCubeMap(iformat, 1, defaultWidth);
				((TextureCubeMap)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			case CUBE_MAP_ARRAY:
				result = new TextureCubeMapArray(iformat, 1, defaultWidth, 1);
				((TextureCubeMapArray)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			case _2D_ARRAY:
				result = new Texture2DArray(iformat, 1, defaultWidth, defaultHeight, 1);
				((Texture2DArray)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			case _3D:
				result = new Texture3D(iformat, 1, defaultWidth, defaultHeight, 1);
				((Texture3D)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
			default:
				result = new Texture2D(iformat, 1, defaultWidth, defaultHeight);
				((Texture2D)result).bufferData(pixels, BaseFormat.RGBA, TexDataType.BYTE, 0);
				break;
		}
		return result;
	}
}
