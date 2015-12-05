package textures.enums;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public enum BaseFormat {
	DEPTH(GL_DEPTH_COMPONENT),
	DEPTH_STENCIL(GL_DEPTH_STENCIL),
	RED(GL_RED),
	RG(GL_RG),
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	STENCIL_INDEX(GL_STENCIL_INDEX);

	public final int value;
	
	private BaseFormat(int type){
		value = type;
	}
}
