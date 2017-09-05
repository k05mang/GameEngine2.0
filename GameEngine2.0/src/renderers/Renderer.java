package renderers;

import java.util.ArrayList;
import java.util.Collection;

import core.Entity;
import core.Resource;
import lights.Light;

public abstract class Renderer implements Resource{
	private float width, height;
	
	Renderer(float width, float height){
		this.width = width;
		this.height = height;
	}
	
	public abstract void render(Collection<Entity> meshes, 
			ArrayList<Light> lights,
			RenderLogic logic);
	
	public abstract void delete();
}
