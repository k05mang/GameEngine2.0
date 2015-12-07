package textures.loaders;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

//import magick.ImageInfo;
//import magick.MagickException;
//import magick.MagickImage;
//import magick.MagickLoader;






import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import core.Scene;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
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
		return load(new File(file), iformat, type);
	}
	
	public static Texture load(File file, InternalFormat iformat, TextureType type){
		Texture result = null;
		try {
			//check to make sure the file has the png extension
			if(file.getName().toLowerCase().contains(".png")){
	//			loadImage(file, iformat);
				result = loadPNG(file, iformat, type);
			}
		} catch (IOException e) {
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
		} catch (IOException e) {
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
		} catch (IOException e) {
			e.printStackTrace();
			return loadDefault(iformat, type);
		}
	}
	
	public static Texture loadPNG(InternalFormat iformat, TextureType type, ArrayList<File> files) throws IOException{
		Texture result = null;
		if(files.isEmpty()){
			return loadDefault(iformat, type);
		}else{
			ArrayList<PNGDecoder> decoders = new ArrayList<PNGDecoder>();
			FileInputStream imageFile = new FileInputStream(files.get(0));
			PNGDecoder decoder = new PNGDecoder(imageFile);
			decoders.add(decoder);
			int width = decoder.getWidth();
			int height = decoder.getHeight();
			
			//while reading in the files determine if they are all the same dimensions and if not handle it
			for(int curFile = 1; curFile < files.size(); curFile++){
				FileInputStream image = new FileInputStream(files.get(curFile));
				PNGDecoder parser = new PNGDecoder(image);
				//check if they have the same dimension
				if(width != parser.getWidth() || height != parser.getHeight()){
					image.close();
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
				PNGDecoder decode = decoders.get(curDecoder);
				BaseFormat texFormat;
				Format format;
				int pixelElements = 0;
				
				if(decode.isRGB()){
					if(decode.hasAlpha()){//RGBA
						format = Format.RGBA;
						texFormat = BaseFormat.RGBA;
						pixelElements = 4;
					}else{//RGB
						format = Format.RGB;
						texFormat = BaseFormat.RGB;
						pixelElements = 3;
					}
				}else{
					if(decode.hasAlpha()){//RG
						format = Format.LUMINANCE_ALPHA;
						texFormat = BaseFormat.RG;
						pixelElements = 2;
					}else{//Red
						format = Format.LUMINANCE;
						texFormat = BaseFormat.RED;
						pixelElements = 1;
					}
				}
				
				ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*pixelElements);
				
				decode.decodeFlipped(pixels, pixelElements*width, format);
				
				pixels.flip();
				
				switch(type){
					case CUBE_MAP:
						//determine which face of the cube map we are on
						switch(curDecoder){
							case 0:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.POS_X);
								break;
							case 1:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.NEG_X);
								break;
							case 2:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.POS_Y);
								break;
							case 3:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.NEG_Y);
								break;
							case 4:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.POS_Z);
								break;
							case 5:
								((TextureCubeMap)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, CubeMapFace.NEG_Z);
								break;
						}
						break;
					case CUBE_MAP_ARRAY:
						//determine which face of the cube map we are on
						switch(curDecoder%6){
							case 0:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.POS_X);
								break;
							case 1:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.NEG_X);
								break;
							case 2:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.POS_Y);
								break;
							case 3:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.NEG_Y);
								break;
							case 4:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.POS_Z);
								break;
							case 5:
								((TextureCubeMapArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder%6, 0, CubeMapFace.NEG_Z);
								break;
						}
						break;
					case _2D_ARRAY:
						((Texture2DArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, curDecoder, 0);
						break;
					case _3D:
						((Texture3D)result).subImage(pixels, texFormat, TexDataType.UBYTE, 0, 0, 0, curDecoder, width, height, curDecoder+1);
						break;
				}
			}
		}
		return result;
	}
	
//	private static void loadImage(String file, InternalFormat iformat){
//		try {
//			MagickLoader loader = new MagickLoader();
//			ImageInfo imageData = new ImageInfo(file);
//			MagickImage image = new MagickImage(imageData);
//			Dimension imageDim = image.getDimension();
//			System.out.println("width: "+imageDim.getWidth()+" Height: "+imageDim.getHeight());
//		} catch (MagickException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private static Texture loadPNG(File file, InternalFormat iformat, TextureType type) throws IOException{
		FileInputStream imageFile = new FileInputStream(file);
		PNGDecoder decoder = new PNGDecoder(imageFile);
		
		int width = decoder.getWidth();
		int height = decoder.getHeight();
		
		BaseFormat texFormat;
		Format format;
		int pixelElements = 0;
		
		if(decoder.isRGB()){
			if(decoder.hasAlpha()){//RGBA
				format = Format.RGBA;
				texFormat = BaseFormat.RGBA;
				pixelElements = 4;
			}else{//RGB
				format = Format.RGB;
				texFormat = BaseFormat.RGB;
				pixelElements = 3;
			}
		}else{
			if(decoder.hasAlpha()){//RG
				format = Format.LUMINANCE_ALPHA;
				texFormat = BaseFormat.RG;
				pixelElements = 2;
			}else{//Red
				format = Format.LUMINANCE;
				texFormat = BaseFormat.RED;
				pixelElements = 1;
			}
		}
		
		ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*pixelElements);
		
		decoder.decodeFlipped(pixels, pixelElements*width, format);
		
		pixels.flip();
		Texture result = null;
		
		switch(type){
			case RECTANGLE:
				result = new TextureRectangle(iformat, width, height);
				((TextureRectangle)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0);
				break;
			case _1D:
				result = new Texture1D(iformat, 1, width*height);
				((Texture1D)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0);
				break;
			case _1D_ARRAY:
				result = new Texture1DArray(iformat, 1, width, height);
				((Texture1DArray)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0, height, 0);
				break;
			case _2D:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0);
				break;
			default:
				result = new Texture2D(iformat, 1, width, height);
				((Texture2D)result).bufferData(pixels, texFormat, TexDataType.UBYTE, 0);
				break;
		}
		imageFile.close();
		return result;
	}
	
	public static Texture loadDefault(InternalFormat iformat, TextureType type){
		Texture result = null;
		int defaultWidth = 8, defaultHeight = 8;
		int cubeMapFactor = type == TextureType.CUBE_MAP || type == TextureType.CUBE_MAP_ARRAY ? 6 : 1;
		ByteBuffer pixels = BufferUtils.createByteBuffer(defaultWidth*defaultHeight*4*cubeMapFactor);
		
		
		for(int curRow = 0; curRow < defaultHeight*cubeMapFactor; curRow++){
			for(int curCol = 0; curCol < defaultWidth*cubeMapFactor; curCol++){
				int colorMod = (curRow+curCol)%2;
				//use an alternating checkerboard pattern
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)1);
			}
		}
		pixels.flip();

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
