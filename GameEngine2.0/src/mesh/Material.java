package mesh;

import glMath.vectors.Vec4;
import shaders.ShaderProgram;
import textures.Texture;
import core.Resource;
import core.SceneManager;

public class Material implements Resource{

	public static final int 
	DIFFUSE = 0,
	NORMAL = 1,
	SPECULAR = 2,
	BUMP = 3,
	COLOR = 4;
	private String diffuse, normal, specular, bump;
	private float specPower, specInt;
	private Vec4 color;
	
	public Material(){
		diffuse = null;
		normal = null;
		specular = null;
		bump = null;
		color = null;
		specPower = 0;
		specInt = 0;
	}
	
	public void setSpecularExp(float exp){
		specPower = exp;
	}
	
	public float getSpecularExponent(){
		return specPower;
	}
	
	public void setSpecularIntensity(float intensity){
		specInt = intensity;
	}
	
	public float getSpecularIntensity(){
		return specInt;
	}
	
	public void setColor(Vec4 color){
		if(this.color == null){
			this.color = new Vec4(color);
		}else{
			this.color.set(color);
		}
	}
	
	public void setColor(float r, float g, float b, float a){
		if(this.color == null){
			this.color = new Vec4(r,g,b,a);
		}else{
			this.color.set(r,g,b,a);
		}
	}
	
	public Vec4 getColor(){
		return color;
	}
	
	public void setTexture(int type, String id){
		switch(type){
			case DIFFUSE:
				diffuse = new String(id);
				break;
			case NORMAL:
				normal = new String(id);
				break;
			case SPECULAR:
				specular = new String(id);
				break;
			case BUMP:
				bump = new String(id);
				break;
		}
	}
	
	public String getTextureId(int type){
		switch(type){
			case DIFFUSE:
				return diffuse;
			case NORMAL:
				return normal;
			case SPECULAR:
				return specular;
			case BUMP:
				return bump;
			default:
				return null;
		}
	}
	
	public boolean hasMaterial(int type){
		switch(type){
			case DIFFUSE:
				return diffuse != null;
			case NORMAL:
				return normal != null;
			case SPECULAR:
				return specular != null;
			case BUMP:
				return bump != null;
			case COLOR:
				return color != null;
			default:
				return false;
		}
	}
	
	public void bind(ShaderProgram program){
		//diffuse textures will be bound to the first texture sampler
		if(diffuse != null){
			((Texture) SceneManager.textures.get(diffuse)).bindToTextureUnit(DIFFUSE);
			program.setUniform("useDiffuse", true);
		}else{
			if(color != null){
				program.setUniform("useDiffuse", false);
				program.setUniform("color", color);
			}else{
				((Texture) SceneManager.textures.get("default")).bindToTextureUnit(DIFFUSE);
				program.setUniform("useDiffuse", true);
			}
		}
		
		if(normal != null){
			((Texture) SceneManager.textures.get(normal)).bindToTextureUnit(NORMAL);
			program.setUniform("useNormalMap", true);
		}else{
			program.setUniform("useNormalMap", false);
		}
		
		if(specular != null){
			((Texture) SceneManager.textures.get(specular)).bindToTextureUnit(SPECULAR);
			program.setUniform("useSpecMap", true);
		}else{
			program.setUniform("useSpecMap", false);
			program.setUniform("specPower", specPower);
			program.setUniform("specInt", specInt);
		}
		
		if(bump != null){
			((Texture) SceneManager.textures.get(bump)).bindToTextureUnit(BUMP);
			program.setUniform("useBump", true);
		}else{
			program.setUniform("useBump", false);
		}
	}

	@Override
	public void delete() {
		
	}
}
