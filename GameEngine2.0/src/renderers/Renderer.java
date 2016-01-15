package renderers;

import mesh.Renderable;
import gldata.VertexArray;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
	public static void render(Renderable target){
		VertexArray vao = target.getVAO();
		vao.bind();
		glDrawElements(vao.getRenderMode().mode, vao.getNumIndices(), vao.getIndexType().enumType, 0);
		vao.unbind();
	}
}
