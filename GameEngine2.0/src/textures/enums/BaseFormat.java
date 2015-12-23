package textures.enums;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public enum BaseFormat {
	DEPTH(GL_DEPTH_COMPONENT),
	STENCIL_INDEX(GL_STENCIL_INDEX),
	DEPTH_STENCIL(GL_DEPTH_STENCIL),
	
	RED(GL_RED),
	GREEN(GL_GREEN),
	BLUE(GL_BLUE),
	
	RG(GL_RG),
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	BGR(GL_BGR),
	BGRA(GL_BGRA),
	
	RED_INTEGER(GL_RED_INTEGER),
	GREEN_INTEGER(GL_GREEN_INTEGER),
	BLUE_INTEGER(GL_BLUE_INTEGER),
	
	RG_INTEGER(GL_RG_INTEGER),
	RGB_INTEGER(GL_RGB_INTEGER),
	RGBA_INTEGER(GL_RGBA_INTEGER),
	BGR_INTEGER(GL_BGR_INTEGER),
	BGRA_INTEGER(GL_BGRA_INTEGER);

	public final int value;
	
	private BaseFormat(int type){
		value = type;
	}
}
