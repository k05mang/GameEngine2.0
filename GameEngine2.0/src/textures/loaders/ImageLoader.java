package textures.loaders;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

//import magick.ImageInfo;
//import magick.MagickException;
//import magick.MagickImage;
//import magick.MagickLoader;

import org.lwjgl.BufferUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import textures.Texture;
import textures.Texture2D;
import textures.enums.BaseFormat;
import textures.enums.InternalFormat;
import textures.enums.TexDataType;
import textures.enums.TextureType;

public abstract class ImageLoader {

	public static Texture load(String file, InternalFormat iformat/*, TextureType type*/){
		//check to make sure the file has the png extension
		if(file.toLowerCase().contains(".png")){
//			loadImage(file, iformat);
			return loadPNG(file, iformat);
		}
		return null;
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
	
	private static Texture loadPNG(String file, InternalFormat iformat){
		try{
			FileInputStream imageFile = new FileInputStream(file);
			PNGDecoder decoder = new PNGDecoder(imageFile);
			
			int width = decoder.getWidth();
			int height = decoder.getHeight();
			
			BaseFormat texFormat;
			Format format;
			InternalFormat iFormat;
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
			Texture2D texture = new Texture2D(iformat, 1, width, height);
			
			texture.bufferData(pixels, texFormat, TexDataType.UBYTE, 0);
			return texture;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
}
