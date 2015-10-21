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
	
	@Override
	public String toString(){
		switch(this){
			case POINTS:
				return "POINTS";
			case LINES:
				return "LINES";
			case LINES_ADJ:
				return "LINES_ADJ";
			case LINE_LOOP:
				return "LINE_LOOP";
			case LINE_STRIP:
				return "LINE_STRIP";
			case LINE_STRIP_ADJ:
				return "LINE_STRIP_ADJ";
			case PATCHES:
				return "PATCHES";
			case TRIANGLES:
				return "TRIANGLES";
			case TRIANGLES_ADJ:
				return "TRIANGLES_ADJ";
			case TRIANGLE_FAN:
				return "TRIANGLE_FAN";
			case TRIANGLE_STRIP:
				return "TRIANGLE_STRIP";
			case TRIANGLE_STRIP_ADJ:
				return "TRIANGLE_STRIP_ADJ";
		}
		return "";
	}
}
