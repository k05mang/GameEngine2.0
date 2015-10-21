package gldata;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;

public enum BufferType {
	ARRAY(GL_ARRAY_BUFFER),
	ATOMIC_COUNTER(GL_ATOMIC_COUNTER_BUFFER),
	COPY_READ(GL_COPY_READ_BUFFER),
	COPY_WRITE(GL_COPY_WRITE_BUFFER),
	DISPATCH_INDIRECT(GL_DISPATCH_INDIRECT_BUFFER),
	DRAW_INDIRECT(GL_DRAW_INDIRECT_BUFFER),
	ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER),
	PIXEL_PACK(GL_PIXEL_PACK_BUFFER),
	PIXEL_UNPACK(GL_PIXEL_UNPACK_BUFFER),
	QUERY(GL_QUERY_BUFFER),
	SHADER_STORAGE(GL_SHADER_STORAGE_BUFFER),
	TEXTURE(GL_TEXTURE_BUFFER),
	TRANSFORM_FEEDBACK(GL_TRANSFORM_FEEDBACK_BUFFER),
	UNIFORM(GL_UNIFORM_BUFFER);
	
	public final int type;
	
	private BufferType(int type){
		this.type = type;
	}
}
