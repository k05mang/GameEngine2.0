package mesh;

import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import gldata.BufferObject;
import gldata.BufferType;
import gldata.IndexBuffer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import mesh.primitives.Vertex;
import renderers.RenderMode;

public class OBJ extends Renderable {

	private String fileName;
	private ArrayList<Vec3> verts, normals;
	private ArrayList<Vec2> uvs;
	private HashMap<String, Vertex> hashVerts;
	
	public OBJ(File file){
		super();
		fileName = file.getName();
		try(Scanner obj = new Scanner(file)){
			//create vertex buffer
			BufferObject vbo = new BufferObject(BufferType.ARRAY);
			vbos.add(vbo);
			vao.addVertexBuffer("default", vbo);
			
			//construct index buffer
			IndexBuffer ibo = new IndexBuffer(IndexBuffer.IndexType.INT);
			ibos.add(ibo);
			vao.addIndexBuffer("default", RenderMode.TRIANGLES, ibo);
			//start parsing the file
			
			//will retain the different values for each field of data, position, normal, and text coords
			verts = new ArrayList<Vec3>();
			normals = new ArrayList<Vec3>();
			uvs = new ArrayList<Vec2>();
			//tracks previously defined vertices for creation of index buffer
			hashVerts = new HashMap<String, Vertex>();
			
			while(obj.hasNextLine()){
				String line = obj.nextLine();
				
				parseLine(line);
			}
			
		}catch(IOException e){
			
		}
	}
	
	private void parseLine(String line){
		String[] data = line.split("\\s");
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
		BufferObject vbo = vbos.get(0);
		IndexBuffer ibo = ibos.get(0);
		ArrayList<Integer> indices = new ArrayList<Integer>();//stores indices found for current face
		//iterate through all the points defining the current face
		//since the data being passed was from the function above the first value is merely the letter f for marking the line
		for(int curVert = 1; curVert < points.length; curVert++){
			//see if the indices defining the current vertex were already defined
			if(hashVerts.get(points[curVert]) != null){
				indices.add(geometry.getIndex(hashVerts.get(points[curVert])));
			}else{
				//if not, check if any negative values defined the face, this is to convert them to the absolute index variant which is how the 
				//vertices are stored in the hash map
				if(points[curVert].contains("-")){
					//convert all negative values to their corresponding absolute values
					String[] indexValues = points[curVert].split("/");
					StringBuilder newIndices = new StringBuilder();
					//vertex indices are defined as triplets 
					
					//check if the value is relative or absolute
					if(indexValues[0].contains("-")){
						//if it's relative then convert it
						int index = Integer.parseInt(indexValues[0]);
						newIndices.append((verts.size()-index)+"/");
					}else{
						newIndices.append(indexValues[0]+"/");
					}
					
					//check if the value is empty, meaning that texture coords weren't defined here
					if(indexValues[1].isEmpty()){
						newIndices.append("/");
					}else if(indexValues[1].contains("-")){//check if the value is relative or absolute
						//if it's relative then convert it
						int index = Integer.parseInt(indexValues[1]);
						newIndices.append((uvs.size()-index)+"/");
					}else{
						newIndices.append(indexValues[1]+"/");
					}
					
					//check if the value is empty, meaning that texture coords weren't defined here
					if(indexValues[2].isEmpty()){
						newIndices.append("/");
					}else if(indexValues[2].contains("-")){//check if the value is relative or absolute
						//if it's relative then convert it
						int index = Integer.parseInt(indexValues[2]);
						newIndices.append((normals.size()-index));
					}else{
						newIndices.append(indexValues[2]);
					}
					
					//check again with the converted values
					if(hashVerts.get(newIndices) != null){
						indices.add(geometry.getIndex(hashVerts.get(newIndices)));
					}else{//it's a new point that needs to be added
						Vertex newVert = new Vertex(0,0,0, 0,0,0, 0,0);
						String[] newIValues = newIndices.toString().split("/");
						
						//position value
						int index = Integer.parseInt(newIValues[0]);
						newVert.setPos(verts.get(index-1));//-1 because the first index is 1 not 0

						//uv value
						if(!newIValues[1].isEmpty()){
							index = Integer.parseInt(newIValues[1]);
							newVert.setUV(uvs.get(index-1));//-1 because the first index is 1 not 0
						}
						
						//normal value
						if(!newIValues[2].isEmpty()){
							index = Integer.parseInt(newIValues[2]);
							newVert.setNormal(normals.get(index-1));//-1 because the first index is 1 not 0
						}
						
						//add the value
						hashVerts.put(newIndices.toString(), newVert);
						geometry.add(newVert);
						newVert.addTo(vbo);
						indices.add(geometry.getIndex(newVert));
					}
				}else{//then the vertex is new and we can add it to the list
					Vertex newVert = new Vertex(0,0,0, 0,0,0, 0,0);
					String[] indexValues = points[curVert].split("/");
					
					//position value
					int index = Integer.parseInt(indexValues[0]);
					newVert.setPos(verts.get(index-1));//-1 because the first index is 1 not 0

					//uv value
					if(!indexValues[1].isEmpty()){
						index = Integer.parseInt(indexValues[1]);
						newVert.setUV(uvs.get(index-1));//-1 because the first index is 1 not 0
					}
					
					//normal value
					if(!indexValues[2].isEmpty()){
						index = Integer.parseInt(indexValues[2]);
						newVert.setNormal(normals.get(index-1));//-1 because the first index is 1 not 0
					}
					
					//add the value
					hashVerts.put(points[curVert], newVert);
					geometry.add(newVert);
					newVert.addTo(vbo);
					indices.add(geometry.getIndex(newVert));
				}
			}
			
		}
		
		//now generate faces for the indices found
		//since more than 3 vertices can define a face in OBJ triangulation may be necessary
	}
}
