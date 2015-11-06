package textures;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL45.*;

//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL12.*;
//import static org.lwjgl.opengl.GL13.*;
//import static org.lwjgl.opengl.GL14.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL21.*;
//import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.opengl.GL31.*;
//import static org.lwjgl.opengl.GL32.*;
//import static org.lwjgl.opengl.GL33.*;
//import static org.lwjgl.opengl.GL40.*;
//import static org.lwjgl.opengl.GL41.*;
//import static org.lwjgl.opengl.GL42.*;
//import static org.lwjgl.opengl.GL43.*;
//import static org.lwjgl.opengl.GL44.*;
//import static org.lwjgl.opengl.GL45.*;

public abstract class Texture {

	protected int id;
	protected TextureType type;
	
	public Texture(TextureType texType){
		type = texType;
		id = glGenTextures();
	}
	
	public void bind(){
		glBindTexture(type.value, id);
	}
	
	public void bindToTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, id);
	}
	
	public void unbindFromTextureUnit(int texUnit){
		glBindTextureUnit(GL_TEXTURE0+texUnit, 0);
	}
	
	public void unbind(){
		glBindTexture(type.value, 0);
	}
}
