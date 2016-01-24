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
	
	/**
	 * Creates a material with no initial data
	 */
	public Material(){
		diffuse = null;
		normal = null;
		specular = null;
		bump = null;
		color = null;
		specPower = 0;
		specInt = 0;
	}
	
	/**
	 * Sets the specular exponent for this material
	 * 
	 * @param exp Value representing the exponent used in computing specular highlights
	 */
	public void setSpecularExp(float exp){
		specPower = exp;
	}
	
	/**
	 * Gets the specular exponent for this material
	 * 
	 * @return Value representing the exponent used in computing specular highlights
	 */
	public float getSpecularExponent(){
		return specPower;
	}
	
	/**
	 * Sets the specular intensity for this material
	 * 
	 * @param intensity Intensity of the specular highlights of this material
	 */
	public void setSpecularIntensity(float intensity){
		specInt = intensity;
	}
	
	/**
	 * Gets the specular intensity of this material
	 * 
	 * @return Specular intensity of this material
	 */
	public float getSpecularIntensity(){
		return specInt;
	}
	
	/**
	 * Sets the color of this material to the given color vector, this color value
	 * is used only if there is no diffuse texture for this material
	 * 
	 * @param color Color to set this material to
	 */
	public void setColor(Vec4 color){
		if(this.color == null){
			this.color = new Vec4(color);
		}else{
			this.color.set(color);
		}
	}
	
	/**
	 * Sets the color of this material to the given color values, this color value
	 * is used only if there is no diffuse texture for this material
	 * 
	 * @param r Red component of the color
	 * @param g Green component of the color
	 * @param b Blue component of the color
	 * @param a Alpha component of the color
	 */
	public void setColor(float r, float g, float b, float a){
		if(this.color == null){
			this.color = new Vec4(r,g,b,a);
		}else{
			this.color.set(r,g,b,a);
		}
	}
	
	/**
	 * Gets the color for this material
	 * 
	 * @return Vec4 representing the RGBA color of this material
	 */
	public Vec4 getColor(){
		return color;
	}
	
	/**
	 * Sets the specified texture type of this material to the given
	 * texture id stored in the system
	 * 
	 * @param type Integer constant defined in Material that specifies which texture to set
	 * @param id Texture id stored in the system to set this materials texture to
	 */
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
	
	/**
	 * Removes the texture, specified by type, from this material
	 * 
	 * @param type Texture to remove from this material
	 */
	public void removeTexture(int type){
		switch(type){
			case DIFFUSE:
				diffuse = null;
				break;
			case NORMAL:
				normal = null;
				break;
			case SPECULAR:
				specular = null;
				break;
			case BUMP:
				bump = null;
				break;
		}
	}
	
	/**
	 * Gets the texture id used by the texture specified by type
	 * 
	 * @param type Texture type to retrieve the value of from this material
	 * @return Id of the texture in the system that this material uses for the specified texture
	 */
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
	
	/**
	 * Determines whether this material has renderable information for the given type
	 * 
	 * @param type Type of data to check in this material
	 * @return True if this material has renderable values assigned to the given field, false otherwise
	 */
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
	
	/**
	 * Binds this material to the given shader program. If this material has data assigned to a field it will use that material.
	 * When binding the different textures to the context the material will bind the textures to the texture units as follows:
	 * <br>
	 * <ul>
	 * <li>Diffuse -> 0</li>
	 * <li>Normal -> 1</li>
	 * <li>Specular -> 2</li>
	 * <li>Bump -> 3</li>
	 * </ul>
	 * <br>
	 * If there is no diffuse texture, the materials color value will be used instead, in the event that the material has neither
	 * a diffuse or color assigned to it, then the system default diffuse will be used. Additionally if there is no specular map
	 * attached to the material then the system will use the specular intensity and exponent values assigned to this material.
	 * 
	 * @param program Shader program to bind material properties to
	 */
	public void bind(ShaderProgram program){
		//diffuse textures will be bound to the first texture sampler
		if(diffuse != null){
			Texture albedo = (Texture) SceneManager.textures.get(diffuse);
			//check to make sure the texture assigned to the diffuse exists
			if(albedo != null){
				albedo.bindToTextureUnit(DIFFUSE);
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
		}else{
			if(color != null){
				program.setUniform("useDiffuse", false);
				program.setUniform("color", color);
			}else{
				((Texture) SceneManager.textures.get("default")).bindToTextureUnit(DIFFUSE);
				program.setUniform("useDiffuse", true);
			}
		}
		
		//bind the normal texture
		if(normal != null){
			((Texture) SceneManager.textures.get(normal)).bindToTextureUnit(NORMAL);
			program.setUniform("useNormalMap", true);
		}else{
			program.setUniform("useNormalMap", false);
		}
		
		//bind the specular map
		if(specular != null){
			((Texture) SceneManager.textures.get(specular)).bindToTextureUnit(SPECULAR);
			program.setUniform("useSpecMap", true);
			program.setUniform("specPower", specPower);
			program.setUniform("specInt", specInt);
		}else{
			program.setUniform("useSpecMap", false);
			program.setUniform("specPower", specPower);
			program.setUniform("specInt", specInt);
		}
		
		//bind the bump map
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
