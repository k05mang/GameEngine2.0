package renderers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

public enum RenderMode {
	POINTS (GL_POINTS),
	LINES (GL_LINES),
	LINE_STRIP(GL_LINE_STRIP),
	LINE_LOOP(GL_LINE_LOOP),
	LINES_ADJ(GL_LINES_ADJACENCY),
	LINE_STRIP_ADJ(GL_LINE_STRIP_ADJACENCY),
	TRIANGLES(GL_TRIANGLES),
	TRIANGLE_STRIP (GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL_TRIANGLE_FAN),
	TRIANGLE_STRIP_ADJ(GL_TRIANGLE_STRIP_ADJACENCY),
	TRIANGLES_ADJ(GL_TRIANGLES_ADJACENCY),
	PATCHES(GL_PATCHES);
	
	public final int mode;
	
	private RenderMode(int type){
		mode = type;
	}
}
