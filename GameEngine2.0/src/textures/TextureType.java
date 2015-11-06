package textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

public enum TextureType {
	_1D(GL_TEXTURE_1D),
	_2D(GL_TEXTURE_2D),
	_3D(GL_TEXTURE_3D),
	_1D_ARRAY(GL_TEXTURE_1D_ARRAY),
	_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
	RECTANGLE(GL_TEXTURE_RECTANGLE),
	BUFFER(GL_TEXTURE_BUFFER),
	CUBE_MAP(GL_TEXTURE_CUBE_MAP),
	CUBE_MAP_ARRAY(GL_TEXTURE_CUBE_MAP_ARRAY),
	_2D_MULTISAMPLE(GL_TEXTURE_2D_MULTISAMPLE),
	_2D_MULTISAMPLE_ARRAY(GL_TEXTURE_2D_MULTISAMPLE_ARRAY);

	public final int value;
	
	private TextureType(int type){
		value = type;
	}
}
