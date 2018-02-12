package physics.collision.trees;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import core.Entity;
import glMath.Quaternion;
import glMath.transforms.TransformListener;
import glMath.vectors.Vec3;
import physics.collision.CollisionDetector;
import physics.collision.CollisionMesh;
import physics.collision.Ray;
import physics.collision.data.ContactPair;
import physics.collision.data.RayIntersection;

public class SpatialOctree {
	//first half of nodes will represent the upper portion of space
	//the nodes will read in counter clockwise from the -z, +x quadrant
	private OctreeNode root;
	private Vec3 halfDim, center;
	private Hashtable<CollisionMesh, Entity> entities;
	private int leafCap;
	
//	public SpatialOctree(int leafCapacity, int depthCap){
//		root = new OctreeNode();
//		center = new Vec3();
//		halfDim = new Vec3();
//		leafCap = leafCapacity;
//		gameObjects = new ArrayList<Entity>();
//	}
//	
//	public SpatialOctree(int leafCapacity, int depthCap, ArrayList<Entity> initializer){
//		root = new OctreeNode();
//		center = new Vec3();
//		halfDim = new Vec3();
//		leafCap = leafCapacity;
//		gameObjects = new ArrayList<Entity>();
//		for(Entity obj : initializer){
//			add(obj);
//		}
//	}
	
	public SpatialOctree(int leafCapacity, Vec3 center, Vec3 dimensions){
		this(leafCapacity, center.x, center.y, center.z, dimensions.x, dimensions.y, dimensions.z, null);
	}
	
	public SpatialOctree(int leafCapacity, float cX, float cY, float cZ, Vec3 dimensions){
		this(leafCapacity, cX, cY, cZ, dimensions.x, dimensions.y, dimensions.z, null);
	}
	
	public SpatialOctree(int leafCapacity, Vec3 center, float dX, float dY, float dZ){
		this(leafCapacity, center.x, center.y, center.z, dX, dY, dZ, null);
	}
	
	public SpatialOctree(int leafCapacity, float cX, float cY, float cZ, float dX, float dY, float dZ){
		this(leafCapacity, cX, cY, cZ, dX, dY, dZ, null);
	}
	
	public SpatialOctree(int leafCapacity, Vec3 center, Vec3 dimensions, ArrayList<Entity> initializer){
		this(leafCapacity, center.x, center.y, center.z, dimensions.x, dimensions.y, dimensions.z, initializer);
	}
	
	public SpatialOctree(int leafCapacity, Vec3 center, float dX, float dY, float dZ, ArrayList<Entity> initializer){
		this(leafCapacity, center.x, center.y, center.z, dX, dY, dZ, initializer);
	}
	
	public SpatialOctree(int leafCapacity, float cX, float cY, float cZ, float dX, float dY, float dZ, ArrayList<Entity> initializer){
		center = new Vec3(cX, cY, cZ);
		halfDim = new Vec3(dX/2.0f, dY/2.0f, dZ/2.0f);
//		root = new OctreeNode(null, this.center, this.halfDim, 0);
		leafCap = leafCapacity;
		entities = new Hashtable<CollisionMesh, Entity>();
	}
	
	public void add(Entity object){
//		//alternative might be to have the tree dynamically resize as items are added and removed
//		Vec3 relObjCenter = (Vec3)VecUtil.subtract(object.getTranslation(), root.volume.getCenter());
//		AABB objVolume = object.getBoundingVolume();
//		Vec3 objHalfDim = objVolume.getHalfDimensions();
//
//		boolean x = relObjCenter.x+objHalfDim.x < halfDim.x && relObjCenter.x-objHalfDim.x > -halfDim.x;
//		boolean y = relObjCenter.y+objHalfDim.y < halfDim.y && relObjCenter.y-objHalfDim.y > -halfDim.y;
//		boolean z = relObjCenter.z+objHalfDim.z < halfDim.z && relObjCenter.z-objHalfDim.z > -halfDim.z;
//		
//		if(x && y && z){
//			gameObjects.add(object);
//			root.add(gameObjects.size()-1, object, 0);
//		}else{
//			return false;
//		}
//		return true;
		if(object.getCollider() != null){
			entities.put(object.getCollider(), object);
		}
	}
	
	public void remove(Entity object){
		//traverse the tree then iterate through the indices of the leaf node to find the array list element
	}
	
	public void update(){
		
	}
	
	public ArrayList<ContactPair> getCollisions(){
		ArrayList<ContactPair> pairs = new ArrayList<ContactPair>();
		
		Entity[] objects = entities.values().toArray(new Entity[0]);
		//loop through each of the objects in the list
		for(int curObject = 0; curObject < objects.length; curObject++){
			//perform a second loop on all the remaining elements to determine if there is a collision
			for(int secondObj = curObject+1; secondObj < objects.length; secondObj++){
				Entity objA = objects[curObject];
				Entity objB = objects[secondObj];
				//check if they are colliding
				if(CollisionDetector.intersects(objA.getCollider(), objB.getCollider()).areColliding()){
					//if they are then add them to the results array
					pairs.add(new ContactPair(objA, objB));
				}
			}
		}
		
		return pairs;
	}
	
	/**
	 * Gets a list of the objects in this tree that are intersecting with the given ray object. The returned list contains
	 * various information about he intersection of the ray with the object. Additionally the list provided is depth sorted,
	 * the first element of the list will always be the closest element to the ray origin.
	 * 
	 * @param ray Ray to be tested for intersection with
	 * @return Depth sorted list of elements that intersect with the given ray from this tree.
	 */
	public ArrayList<RayIntersection> getCollisions(Ray ray){
		ArrayList<RayIntersection> pairs = new ArrayList<RayIntersection>();
		
		//loop through each of the objects in the list
		for(CollisionMesh curCollider : entities.keySet()){
			RayIntersection current = CollisionDetector.intersects(ray, curCollider);
			if(current.areColliding()){
				//if they are then add them to the results array
				//perform a binary search looking for the position to insert this values depth
				int start = 0;
				int end = pairs.size();
				
				//continue searching until we find the index we are looking to insert on
				while(start != end){
					//continuously adjust the mid point depending on the half of the array we are searching on
					//addition is necessary to get the mid point index of the upper half of the array
					int mid = (start+end)/2;
					if(current.getDepthEntered() < pairs.get(mid).getDepthEntered()){
						end = mid;
					}else{
						start = mid+1;
					}
				}
				
				pairs.add(start, current);
			}
		}
		
		return pairs;
	}
	
	public Entity getEntity(CollisionMesh mesh){
		return entities.get(mesh);
	}
	
	private class OctreeNode implements TransformListener{
//		protected ArrayList<Integer> gameIndices;
//		protected OctreeNode[] nodes;
//		protected OctreeNode parent;
//		protected AABB volume;
//		
//		/**
//		 * Constructs an octree node
//		 * 
//		 * @param parent Parent node of this tree node, if null then this node is the root node
//		 * @param parentCenter Center point of the parent node
//		 * @param parentHalfDim Half dimensions of the parent node
//		 * @param index Index that represents what quadrant of the parent this node is meant to represent
//		 */
//		public OctreeNode(OctreeNode parent, Vec3 parentCenter, Vec3 parentHalfDim, int index){
//			
//		}
//		
//		/**
//		 * Adds a Game Object to the tree, passing the object down the tree finding the proper node to place it in
//		 * 
//		 * @param index Index into the primary game objects array where the game object is stored
//		 * @param object Actual game object to be added, this is used to check bounds and position against the node
//		 * @param curDepth The current traversal depth of the tree
//		 */
//		public void add(int index, Entity object, int curDepth){
//			//check if we have reached the depth cap for the tree, at this point the object can only be added to the 
//			//current node regardless of the leaf capacity set by the tree
//			if(curDepth == maxDepth){
//				gameIndices.add(index);
//			}else{
//				//check if this node has been split yet, if it has the continuing moving down the tree to find 
//				//the leaf for the game object that is to be added
//				if(nodes != null){
//					//check whether the add object intersects the bounding volumes of the nodes and add it to the
//					//node that contains it
//					for(int curNode = 0; curNode < 8; curNode++){
//						if(nodes[curNode].volume.colliding(object.getBoundingVolume())){
//							nodes[curNode].add(index, object, curDepth+1);
//						}
//					}
//				}else{
//					//if there is room for this object at this level then add it
//					if(gameIndices.size() < leafCap){
//						gameIndices.add(index);
//					}else{
//						//break this node up
//						split();
//						//parse the objects that are contained in this node and distribute them to the sub nodes
//						//that were made
//						for(Integer objIndex : gameIndices){
//							Entity curObject = gameObjects.get(objIndex.intValue());
//							//check which sub space to put the object in
//							for(int curNode = 0; curNode < 8; curNode++){
//								if(nodes[curNode].volume.colliding(curObject.getBoundingVolume())){
//									nodes[curNode].add(objIndex.intValue(), curObject, curDepth+1);
//								}
//							}
//						}
//						//after splitting this node into the sub nodes and distributing the current nodes objects among them
//						//put the current object into the nodes it belongs
//						for(int curNode = 0; curNode < 8; curNode++){
//							if(nodes[curNode].volume.colliding(object.getBoundingVolume())){
//								nodes[curNode].add(index, object, curDepth+1);
//							}
//						}
//						//clear the indices stored at this level since this node is no longer a leaf
//						gameIndices.clear();
//					}
//				}
//			}
//		}
//		
//		public void split(){
//			Vec3 center = volume.getCenter();
//			Vec3 hlafDimensions = volume.getHalfDimensions();
//			nodes = new OctreeNode[]{
//					new OctreeNode(this, center, hlafDimensions, 0),
//					new OctreeNode(this, center, hlafDimensions, 1),
//					new OctreeNode(this, center, hlafDimensions, 2),
//					new OctreeNode(this, center, hlafDimensions, 3),
//					new OctreeNode(this, center, hlafDimensions, 4),
//					new OctreeNode(this, center, hlafDimensions, 5),
//					new OctreeNode(this, center, hlafDimensions, 6),
//					new OctreeNode(this, center, hlafDimensions, 7)
//			};
//		}
//
		@Override
		public void scaled(float x, float y, float z) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void translated(float x, float y, float z) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void rotated(Quaternion rotation) {
			// TODO Auto-generated method stub
			
		}
	}
}
