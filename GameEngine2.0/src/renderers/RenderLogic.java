package renderers;

import java.util.HashMap;

import core.Entity;
import lights.Light;
import shaders.ShaderProgram;
import textures.Texture;

public interface RenderLogic {

	public void geoemtryLogic(Entity mesh);
	public void lightLogic(Light light);
	public void stencilLogic(Entity mesh);
	public void postProcess(Texture finalImg);
}
