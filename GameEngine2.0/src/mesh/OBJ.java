package mesh;

import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.AttribType;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.BufferUsage;
import gldata.IndexBuffer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import mesh.primitives.Face;
import mesh.primitives.Vertex;
import renderers.RenderMode;

public class OBJ extends Renderable {

	private String fileName;
	private ArrayList<Vec3> verts, normals;
	private ArrayList<Vec2> uvs;
	
	public OBJ(String file){
		this(new File(file));
	}
	
	public OBJ(File file){
		super();
		fileName = file.getName();
		try(Scanner obj = new Scanner(file)){
			//create vertex buffer
			BufferObject vbo = new BufferObject(BufferType.ARRAY);
			vbos.add(vbo);
			vao.addVertexBuffer("default", vbo);
			
			
			//will retain the different values for each field of data, position, normal, and text coords
			verts = new ArrayList<Vec3>();
			normals = new ArrayList<Vec3>();
			uvs = new ArrayList<Vec2>();
			
			while(obj.hasNextLine()){
				String line = obj.nextLine();
				
				parseLine(line);
				
			}
			
			//if the mesh hasn't defined normals compute normals
			if(normals.isEmpty()){
				geometry.computeNormals();
			}

			//construct index buffer
			IndexBuffer ibo = new IndexBuffer(getIndexType(geometry.getNumVertices()-1));
			ibos.add(ibo);
			vao.addIndexBuffer("default", RenderMode.TRIANGLES, ibo);
			
			//add indices and vertices to the buffers
			geometry.insertVertices(vbo);
			geometry.insertIndices(ibo, RenderMode.TRIANGLES);

			vbo.flush(BufferUsage.STATIC_DRAW);
			ibo.flush(BufferUsage.STATIC_DRAW);
			
			vao.setIndexBuffer("default");

			vao.addAttrib(0, AttribType.VEC3, false, 0);
			vao.addAttrib(1, AttribType.VEC3, false, 0);
			vao.addAttrib(2, AttribType.VEC2, false, 0);
			
			vao.registerVBO("default");

			vao.setAttribVBO(0, "default");
			vao.setAttribVBO(1, "default");
			vao.setAttribVBO(2, "default");

			vao.enableAttribute(0);
			vao.enableAttribute(1);
			vao.enableAttribute(2);
		}catch(IOException e){
			e.printStackTrace();
		}
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
			default://otherwise ignore it for now
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
			if(geometry.getIndex(newVert) == -1){
				//add the value
				geometry.add(newVert);
			}
			indices.add(geometry.getIndex(newVert));
		}
		
		//now generate faces for the indices found
		//since more than 3 vertices can define a face in OBJ triangulation may be necessary
		
		for(int curFace = 1; curFace < indices.size()-1; curFace++){
			geometry.add(new Face(
					indices.get(0),
					indices.get(curFace),
					indices.get(curFace+1)
					));
		}
	}
}
