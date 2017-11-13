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
import textures.enums.TexParam;
import textures.enums.TexParamEnum;
import textures.enums.TextureType;

public abstract class ImageLoader {

	public static Texture load(InternalFormat iformat, TextureType type,  String file){
		return load(iformat, type, new File(file));
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, File file){
		Texture result = null;
		try {
			ImageParser decoder = null;
			//check to make sure the file has the png extension
			if(file.getName().toLowerCase().contains(".png")){
				decoder = new PNGDecoder(file);
			}else if(file.getName().toLowerCase().contains(".tga")){
				decoder = new TGADecoder(file);
			}
			int width = decoder.getWidth();
			int height = decoder.getHeight();
			
			switch(type){
				case RECTANGLE:
					result = new TextureRectangle(iformat, width, height);
					((TextureRectangle)result).bufferData(decoder.parse(), decoder.getFormat(), decoder.getType(), 0);
					break;
				case _1D:
					result = new Texture1D(iformat, 1, width*height);
					((Texture1D)result).bufferData(decoder.parse(), decoder.getFormat(), decoder.getType(), 0);
					break;
				case _1D_ARRAY:
					result = new Texture1DArray(iformat, 1, width, height);
					((Texture1DArray)result).bufferData(decoder.parse(), decoder.getFormat(), decoder.getType(), 0, height, 0);
					break;
				case _2D:
					result = new Texture2D(iformat, 1, width, height);
					((Texture2D)result).bufferData(decoder.parse(), decoder.getFormat(), decoder.getType(), 0);
					break;
				default:
					result = new Texture2D(iformat, 1, width, height);
					((Texture2D)result).bufferData(decoder.parse(), decoder.getFormat(), decoder.getType(), 0);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			//if we failed to load the image use a default texture
			result = loadDefault(iformat, type);
		}
		return result;
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, String... files){
		ArrayList<File> passThrough = new ArrayList<File>(files.length);
		for(String file : files){
			passThrough.add(new File(file));
		}
		return load(iformat, type, passThrough);
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, File... files){
		ArrayList<File> passThrough = new ArrayList<File>(files.length);
		for(File file : files){
			passThrough.add(file);
		}
		return load(iformat, type, passThrough);
	}
	
	public static Texture load(InternalFormat iformat, TextureType type, ArrayList<File> files){
		Texture result = null;
		try {
			if(files.isEmpty()){
				result =  loadDefault(iformat, type);
			}else{
				ArrayList<ImageParser> decoders = new ArrayList<ImageParser>();
				int width = 0;
				int height = 0;
				
				//while reading in the files determine if they are all the same dimensions and if not handle it
				for(int curFile = 0; curFile < files.size(); curFile++){
					ImageParser decoder = null;
					String filename = files.get(curFile).getName().toLowerCase();
					if(filename.contains(".png")){
						 decoder = new PNGDecoder(files.get(curFile));
					}else if(filename.contains(".tga")){
						decoder = new TGADecoder(files.get(curFile));
					}
					//check if we are on the first file and initialize the width and height checks
					if(curFile == 0){
						width = decoder.getWidth();
						height = decoder.getHeight();
					}
					//check if they have the same dimension
					if(width != decoder.getWidth() || height != decoder.getHeight()){
						System.err.println("Failed to load image array in ImageLoader, failed on load of file: "+files.get(curFile).getName()+" due to image size inconsistency");
						result = loadDefault(iformat, type);
						break;
					}else{//else add it to the array of decoders
						decoders.add(decoder);
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
					ImageParser decode = decoders.get(curDecoder);
					switch(type){
						case CUBE_MAP:
							//determine which face of the cube map we are on
							switch(curDecoder){
								case 0:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.POS_X);
									break;
								case 1:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.NEG_X);
									break;
								case 2:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.POS_Y);
									break;
								case 3:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.NEG_Y);
									break;
								case 4:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.POS_Z);
									break;
								case 5:
									((TextureCubeMap)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), 0, CubeMapFace.NEG_Z);
									break;
							}
							break;
						case CUBE_MAP_ARRAY:
							//determine which face of the cube map we are on
							switch(curDecoder%6){
								case 0:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.POS_X);
									break;
								case 1:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.NEG_X);
									break;
								case 2:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.POS_Y);
									break;
								case 3:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.NEG_Y);
									break;
								case 4:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.POS_Z);
									break;
								case 5:
									((TextureCubeMapArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder/6, 0, CubeMapFace.NEG_Z);
									break;
							}
							break;
						case _2D_ARRAY:
							((Texture2DArray)result).bufferData(decode.parse(), decode.getFormat(), decode.getType(), curDecoder, 0);
							break;
						case _3D:
							((Texture3D)result).subImage(decode.parse(), decode.getFormat(), decode.getType(), 0, 0, 0, curDecoder, width, height, curDecoder+1);
							break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = loadDefault(iformat, type);
		}
		return result;
	}
	
	public static Texture loadDefault(InternalFormat iformat, TextureType type){
		return loadDefault(iformat, type, 8, 8);
	}
	
	/**
	 * Loads the system defined default image
	 * 
	 * @param iformat Internal format of the texture object
	 * @param type Texture type to create 
	 * @param width Width of the texture
	 * @param height Height of the texture
	 * 
	 * @return Texture object of the given type that is the default image from the system
	 */
	public static Texture loadDefault(InternalFormat iformat, TextureType type, int width, int height){
		Texture result = null;
		ByteBuffer pixels = ImageParser.getDefault(width, height, type == TextureType.CUBE_MAP || type == TextureType.CUBE_MAP_ARRAY, false);

		switch(type){
			case RECTANGLE:
				result = new TextureRectangle(iformat, width, height);
				((TextureRectangle)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			case _1D:
				result = new Texture1D(iformat, 1, width*height);
				((Texture1D)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			case _1D_ARRAY:
				result = new Texture1DArray(iformat, 1, width, height);
				((Texture1DArray)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0, height, 0);
				break;
			case _2D:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
//			case BUFFER:
//				break;
			case CUBE_MAP:
				result = new TextureCubeMap(iformat, 1, width);
				((TextureCubeMap)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			case CUBE_MAP_ARRAY:
				result = new TextureCubeMapArray(iformat, 1, width, 1);
				((TextureCubeMapArray)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			case _2D_ARRAY:
				result = new Texture2DArray(iformat, 1, width, height, 1);
				((Texture2DArray)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			case _3D:
				result = new Texture3D(iformat, 1, width, height, 1);
				((Texture3D)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
			default:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(pixels, BaseFormat.RGB, TexDataType.UBYTE, 0);
				break;
		}
		result.setParam(TexParam.MIN_FILTER, TexParamEnum.NEAREST);
		result.setParam(TexParam.MAG_FILTER, TexParamEnum.NEAREST);
		result.setParam(TexParam.WRAP_S, TexParamEnum.CLAMP_TO_EDGE);
		result.setParam(TexParam.WRAP_T, TexParamEnum.CLAMP_TO_EDGE);
		return result;
	}
}
