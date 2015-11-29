package textures;

import static org.lwjgl.opengl.GL13.*;

public enum CubeMapFace {
	POS_X(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0),
	NEG_X(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 1),
	POS_Y(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 2),
	NEG_Y(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 3),
	POS_Z(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 4),
	NEG_Z(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 5);
	
	public final int value, layer;
	
	private CubeMapFace(int type, int layerIndex){
		value = type;
		layer = layerIndex;
	}
}
