package mesh.loaders;

import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import mesh.Geometry;
import mesh.Material;
import mesh.OBJ;
import mesh.primitives.Triangle;
import mesh.primitives.Vertex;
import textures.Texture;
import textures.enums.InternalFormat;
import textures.enums.TexParam;
import textures.enums.TexParamEnum;
import textures.enums.TextureType;
import textures.loaders.ImageLoader;
import core.SceneManager;

public class OBJLoader implements MeshLoader {
	
	private ArrayList<Vec3> verts, normals;
	private ArrayList<Vec2> uvs;
	private Scanner obj;
	private String curGroup, curMat, filename;
	private Geometry curMesh;

	public OBJLoader(String file){
		this(new File(file));
	}
	
	public OBJLoader(File file){
		try{
			obj = new Scanner(file);
			//will retain the different values for each field of data, position, normal, and text coords
			verts = new ArrayList<Vec3>();
			normals = new ArrayList<Vec3>();
			uvs = new ArrayList<Vec2>();
			curMesh = new Geometry();
			curMat = null;
			filename = file.getParent()+"\\";
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void load(){
		while(obj.hasNextLine()){
			String line = obj.nextLine();
			parseLine(line);
			
		}
		//add the last mesh that was processed after we reached the end of the file
		if(normals.isEmpty()){
			//compute the normals for the loaded mesh
			curMesh.genNormals();
		}
		//compute the tangents and bitangents
		curMesh.genTangentBitangent();
		
		SceneManager.meshes.put(curGroup+"_"+curMat, new OBJ(curMesh, curMat));
		obj.close();
	}
	
	private void parseLine(String line){
		String[] data = line.split("\\s++");
//		System.out.println(line);
		//check what the line starts with and perform actions accordingly
		switch(data[0]){
			case "v"://vertex
				//add the values on this line to the list of verts 
				verts.add(new Vec3(
						Float.parseFloat(data[1]),	//x
						Float.parseFloat(data[2]),	//y
						Float.parseFloat(data[3])	//z
						));
				break;
			case "vn"://normal
				//add the values on this line to the list of normals 
				normals.add(new Vec3(
						Float.parseFloat(data[1]),	//x
						Float.parseFloat(data[2]),	//y
						Float.parseFloat(data[3])	//z
						));
				break;
			case "vt"://texture coord
				//add the values on this line to the list of texture coords
				//--------This could potentially be only 1 value or possibly 3, in all likely hood 
				//it will only be 2 though, for now that is what will be supported
				uvs.add(new Vec2(
						Float.parseFloat(data[1]),	//u
						Float.parseFloat(data[2])	//v
						));
				break;
			case "f":
				parseFace(data);
				break;
			case "usemtl":
				if(curMat != null){
					//add the finished model to the scene
					//if no normals were collected in the mesh generation compute them now
					if(normals.isEmpty()){
						curMesh.genNormals();
					}
					//compute the tangents and bitangents
					curMesh.genTangentBitangent();
					
					SceneManager.meshes.put(curGroup+"_"+curMat, new OBJ(curMesh, curMat));
				}
				curMat = data[1];
				curMesh.empty();
				break;
			case "mtllib":
				for(int curFile = 1; curFile < data.length; curFile++){
					parseMtl(data[curFile]);
				}
				break;
			case "g":
				//set variables for the next group
				String[] groups = line.split("\\s++");
				curGroup = groups[1];//only use the first group name ignore the rest
				break;
		}
	}
	
	private void parseFace(String[] points){
		ArrayList<Integer> indices = new ArrayList<Integer>();//stores indices found for current face
		//iterate through all the points defining the current face
		//since the data being passed was from the function above the first value is merely the letter f for marking the line
		for(int curVert = 1; curVert < points.length; curVert++){
			Vertex newVert = new Vertex(0,0,0, 0,0,0, 0,0);
			String[] indexValues = points[curVert].split("/");
			for(int curIndex = 0; curIndex < indexValues.length; curIndex++){
				//check if the value is empty, meaning that the value wasn't defined
				if(!indexValues[curIndex].isEmpty()){
					int index = Integer.parseInt(indexValues[curIndex]);
					switch(curIndex){
						case 0://vertex
							//determine if the index is relative or absolute
							if(index < 0){
								newVert.setPos(verts.get(verts.size()+index));
							}else{
								newVert.setPos(verts.get(index-1));//-1 because the first index is 1 not 0
							}
							break;
						case 1://uvs
							//determine if the index is relative or absolute
							if(index < 0){
								newVert.setUV(uvs.get(uvs.size()+index));
							}else{
								newVert.setUV(uvs.get(index-1));//-1 because the first index is 1 not 0
							}
							break;
						case 2://normals
							//determine if the index is relative or absolute
							if(index < 0){
								newVert.setNormal(normals.get(normals.size()+index));
							}else{
								newVert.setNormal(normals.get(index-1));//-1 because the first index is 1 not 0
							}
							break;
					}
				}
			}
			
			//see if the "new" vertex is actually new and add it if it is
			if(curMesh.getIndex(newVert) == -1){
				//add the value
				curMesh.add(newVert);
			}
			indices.add(curMesh.getIndex(newVert));
		}
		
		//now generate faces for the indices found
		//since more than 3 vertices can define a face in OBJ triangulation may be necessary
		for(int curFace = 1; curFace < indices.size()-1; curFace++){
			curMesh.add(new Triangle(
					indices.get(0),
					indices.get(curFace),
					indices.get(curFace+1)
					));
		}
	}
	
	private void parseMtl(String file){
		try(Scanner mtl = new Scanner(new File(filename+file))){
			String line;//current line being parsed
			String matName;
			Material curMat = null;
			while(mtl.hasNextLine()){
				line = mtl.nextLine().trim();
				//check if the line defines the start of a new material
				if(line.startsWith("newmtl")){
					curMat = new Material();
					String[] values = line.split("\\s++");
					matName = values[1];//get the material name
					//loop through the material definition and collect data
					do{
						line = mtl.nextLine().trim();//get the next line
						String[] lineValues = line.split("\\s++");
						switch(lineValues[0]){
							case "Ka":
								//check if the value was difined with a single greyscale value or multiple values
								if(lineValues.length == 2){
									float color = Float.parseFloat(lineValues[1]);
									curMat.setColor(new Vec4(color, color, color, 1));
								}else if(lineValues.length == 4){//in this case 3 colors define the color
									float r = Float.parseFloat(lineValues[1]);
									float g = Float.parseFloat(lineValues[2]);
									float b = Float.parseFloat(lineValues[3]);
									curMat.setColor(new Vec4(r, g, b, 1));
								}
								break;
							case "Kd":
								//check if the value was difined with a single greyscale value or multiple values
								if(lineValues.length == 2){
									float color = Float.parseFloat(lineValues[1]);
									curMat.setColor(new Vec4(color, color, color, 1));
								}else if(lineValues.length == 4){//in this case 3 colors define the color
									float r = Float.parseFloat(lineValues[1]);
									float g = Float.parseFloat(lineValues[2]);
									float b = Float.parseFloat(lineValues[3]);
									curMat.setColor(new Vec4(r, g, b, 1));
								}
								break;
							case "map_Ka":
								//check to make sure the map hasn't been added to the system already
								if(SceneManager.textures.get(lineValues[lineValues.length-1]) == null){
									Texture texture = ImageLoader.load(InternalFormat.RGBA8, TextureType._2D, filename+lineValues[lineValues.length-1]);
									texture.setParam(TexParam.MIN_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.MAG_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.WRAP_S, TexParamEnum.REPEAT);
									texture.setParam(TexParam.WRAP_T, TexParamEnum.REPEAT);
									SceneManager.textures.put(lineValues[lineValues.length-1], texture);
								}
								curMat.setTexture(Material.DIFFUSE, lineValues[lineValues.length-1]);//add texture to material
								break;
							case "map_Kd":
								//check to make sure the map hasn't been added to the system already
								if(SceneManager.textures.get(lineValues[lineValues.length-1]) == null){
									Texture texture = ImageLoader.load(InternalFormat.RGBA8, TextureType._2D, filename+lineValues[lineValues.length-1]);
									texture.setParam(TexParam.MIN_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.MAG_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.WRAP_S, TexParamEnum.REPEAT);
									texture.setParam(TexParam.WRAP_T, TexParamEnum.REPEAT);
									SceneManager.textures.put(lineValues[lineValues.length-1], texture);
								}
								curMat.setTexture(Material.DIFFUSE, lineValues[lineValues.length-1]);//add texture to material
								break;
							case "map_bump":
								//check to make sure the map hasn't been added to the system already
								if(SceneManager.textures.get(lineValues[lineValues.length-1]) == null){
									Texture texture = ImageLoader.load(InternalFormat.R8, TextureType._2D, filename+lineValues[lineValues.length-1]);
									texture.setParam(TexParam.MIN_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.MAG_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.WRAP_S, TexParamEnum.REPEAT);
									texture.setParam(TexParam.WRAP_T, TexParamEnum.REPEAT);
									SceneManager.textures.put(lineValues[lineValues.length-1], texture);
								}
								curMat.setTexture(Material.BUMP, lineValues[lineValues.length-1]);//add texture to material
								break;
							case "bump":
								//check to make sure the map hasn't been added to the system already
								if(SceneManager.textures.get(lineValues[lineValues.length-1]) == null){
									Texture texture = ImageLoader.load(InternalFormat.R8, TextureType._2D, filename+lineValues[lineValues.length-1]);
									texture.setParam(TexParam.MIN_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.MAG_FILTER, TexParamEnum.LINEAR);
									texture.setParam(TexParam.WRAP_S, TexParamEnum.REPEAT);
									texture.setParam(TexParam.WRAP_T, TexParamEnum.REPEAT);
									SceneManager.textures.put(lineValues[lineValues.length-1], texture);
								}
								curMat.setTexture(Material.BUMP, lineValues[lineValues.length-1]);//add texture to material
								break;
						}
					}while(mtl.hasNextLine() && !line.isEmpty());//only read the materials until a new one is found or the file ends
					SceneManager.materials.put(matName, curMat);//adds the material to the main application
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
