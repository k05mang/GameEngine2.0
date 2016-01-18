package mesh;

import glMath.vectors.Vec4;
import core.Resource;

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
	
	public Vec4 getColor(){
		return color;
	}
	
	public void setTexture(int type, String id){
		switch(type){
			case DIFFUSE:
				diffuse = new String(id);
			case NORMAL:
				normal = new String(id);
			case SPECULAR:
				specular = new String(id);
			case BUMP:
				bump = new String(id);
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

	@Override
	public void delete() {
		
	}
}
