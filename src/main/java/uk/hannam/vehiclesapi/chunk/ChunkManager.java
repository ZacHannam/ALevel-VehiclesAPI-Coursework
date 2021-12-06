package uk.hannam.vehiclesapi.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import lombok.Setter;
import uk.hannam.vehiclesapi.chunk.exceptions.ChunkBufferFullException;
import uk.hannam.vehiclesapi.chunk.exceptions.ChunkIsLoadedException;
import uk.hannam.vehiclesapi.chunk.exceptions.VehicleHasNoLocationException;
import uk.hannam.vehiclesapi.main.VehiclesAPI;
import uk.hannam.vehiclesapi.vehicles.Vehicle;

class ChunkID {
	
	@Getter
	@Setter
	// world value of the Chunk
	private World world;
	
	@Getter
	@Setter
	// X value of the Chunk
	private int x;
	
	@Getter
	@Setter
	// Z value of the Chunk
	private int z;
	
	/**
	 * Used to get the chunkID from a location#
	 * Uses X and Z values to find the chunk
	 * @param paramLocation
	 * @return
	 */
	public static ChunkID getChunkID(Location paramLocation) {
		return new ChunkID(paramLocation.getWorld(), paramLocation.getBlockX() / 16, paramLocation.getBlockZ() / 16);
	}
	
	/**
	 * Used to get the chunkID from a chunk
	 * Uses the chunk X and Z to get a chunkID
	 * @param paramChunk
	 * @return
	 */
	public static ChunkID getChunkID(Chunk paramChunk) {
		return new ChunkID(paramChunk.getWorld(), paramChunk.getX(), paramChunk.getZ());
	}
	
	/**
	 * Compares X, Z and World values to paramChunkID
	 * @param paramChunkID
	 * @return
	 */
	public boolean equals(ChunkID paramChunkID) {
		return (this.getX() == paramChunkID.getX()) && (this.getZ() == paramChunkID.getZ()) && (this.getWorld() == paramChunkID.getWorld());
	}
	
	/**
	 * Hashes the ChunkID for a value between 0 and paramMaxHashSize using the modulo function
	 * @param paramMaxHashSize
	 * @return
	 */
	public int hash(double paramMaxHashSize) {
		int concurrentHash = 1; // stores value of the current hash, cannot be 0 as multiplying first
		
		// runs through each letter in the world's name and multiplies the ordinal value of each character by the concurrent hash
		// this creates a large number which can be very random
		for(char c : this.getWorld().getName().toCharArray()) {
			concurrentHash *= c;
		}
		
		// Check if x is less than 0
		if(this.getX() < 0) {
			concurrentHash *= (Math.abs(this.getX()) + concurrentHash); // multiply the hash by the absolute value of x + concurrentHash
		} else if(this.getX() > 0) { // check if x is larger than 0
			concurrentHash *= Math.abs(this.getX()); // multiply the hash by the absolute value of x
		}
		
		// Check if z is less than 0
		if(this.getZ() < 0) {
			concurrentHash *= (Math.abs(this.getZ()) + concurrentHash); // multiply the hash by the absolute value of z + concurrentHash
		} else if(this.getX() > 0) { // Check if z is greater than 0
			concurrentHash *= Math.abs(this.getZ()); // multiply the hash by the absolute value of z
		}
		
		return (int) (Math.abs(concurrentHash) % paramMaxHashSize); // run modulo function on the hash to produce a number with a max size
	}
	
	public ChunkID (World paramWorld, int paramChunkX, int paramChunkZ) {
		this.setWorld(paramWorld); // set world to paramWorld
		this.setX(paramChunkX); // set x to paramChunkX
		this.setZ(paramChunkZ); // set z to paramChunkZ
	}
}

class ChunkBuffer {
	
	@Getter
	@Setter
	// ChunkID contains the chunkID for the chunk buffer
	private ChunkID chunkID;
	
	@Getter
	@Setter
	// contains a list of UUIDs of vehicles in the chunk
	private UUID[] UUIDS;
	
	/**
	 * Used to add a vehicle to the ChunkBuffer
	 * @param paramUUID
	 */
	public void addVehicleUUID(UUID paramUUID) {
		UUID[] newListOfUUIDS = new UUID[this.getUUIDS().length + 1]; // creates a new list of unique IDs that will be 1 larger than the listOfUUIDS
		
		newListOfUUIDS[0] = paramUUID; // adds the new uuid of the vehicle being added to the newListOfUUIDS
		for(int index = 0; index < this.getUUIDS().length; index++) { // runs from 0 to the length of the old list of UUIDS
			newListOfUUIDS[index+1] = this.getUUIDS()[index]; // adds the vehicle's uuid to the newListOfUUIDS from position 1.
		}
		
		this.setUUIDS(newListOfUUIDS);
	}
	
	/**
	 * Used to remove a vehicle from the ChunkBuffer
	 * @param paramUUID
	 */
	public void removeVehicleUUID(UUID paramUUID) {
		if(this.getUUIDS().length == 1) this.setUUIDS(new UUID[0]);
		
		UUID[] newListOfUUIDS = new UUID[this.getUUIDS().length - 1];
		
		int index = 0; // index will be the current index of the newUUIDSInChunk
		for(int n = 0; n < this.getUUIDS().length; n++) { // n stores the current position in UUIDSInChunk
			if(this.getUUIDS()[n] != paramUUID) { // checks if not the current UUID in the iteration is the vehicle being removed
				newListOfUUIDS[index] = this.getUUIDS()[n]; // adds the UUID to the newUUIDSInChunk
				index+=1; // adds one to the index
			}
		}
		
		this.setUUIDS(newListOfUUIDS);
	}
	
	public ChunkBuffer(ChunkID paramChunkID, UUID[] paramUUIDS) {
		if(paramChunkID != null) {
			this.setChunkID(paramChunkID);
		}
		if(paramUUIDS != null) {
			this.setUUIDS(paramUUIDS);
		}
	}
}

class EmptyChunkBuffer extends ChunkBuffer {

	public EmptyChunkBuffer() {
		super(null, null);
	}
}

public class ChunkManager {
	
	private static final int MAX_HASH_SIZE = 10000; // the maximum amount of vehicles possible
	
	/*
	 * @Getter
	private HashMap<ChunkID, UUID[]> vehicleChunkBuffer = new HashMap<ChunkID, UUID[]>(); // CHUNK ID; LIST OF UUIDS OF VEHICLES IN THAT CHUNK
	 */
	
	@Getter
	private final HashMap<UUID, ChunkID> vehiclesInChunkInBuffer = new HashMap<UUID, ChunkID>(); // UUID of vehicle, CHUNK ID of the vehicles location
	
	@Getter
	private final ChunkBuffer[] chunkBuffer; // definite list that contains all of the hashed results
	
	/**
	 * Gets the position of a given ChunkID within the Chunk Buffer.
	 * @param paramChunkID
	 * @return the index location of the paramChunkID if the ChunkID is within the buffer otherwise -1 if the chunk cannot be found.
	 */
	private int getChunkBufferIndex(ChunkID paramChunkID) {
		
		// Retrieves the hashed version of the ChunkID
		int hash = paramChunkID.hash(MAX_HASH_SIZE);
		
		// Checks if the the hashed version of the ChunkID is in the chunkBuffer. This is done by checking that the value in the chunkBuffer is not null.
		while(this.getChunkBuffer()[hash] != null) {
			
			// Checks if the element at the hash index in the chunkBuffer is equal to the paramChunkID.
			// Also checks if the element at the position of hash in chunkBuffer is a EmptyChunkBuffer.
			// If it is and we attempt to get it's chunkID a NullPointerException is thrown.
			// That also means that the EmptyChunkBuffer check must be completed before checking if the ChunkIDs match.
			if(!(this.getChunkBuffer()[hash] instanceof EmptyChunkBuffer) && this.getChunkBuffer()[hash].getChunkID().equals(paramChunkID)) {
				
				// Returns the hash, as that is the confirmed position of the paramChunkID in the BufferChunk
				return hash;
				
			// The else statement is called if the hash does not match the ChunkID
			} else {
				
				// Adds 1 to the hash and finds the remainder where the dividend is MAX_HASH_SIZE using a modulo operation.
				// This will make sure that the new hash does not increase past the (MAX_HASH_SIZE - 1)
				// which is the maximum addressable index in the chunkBuffer
				hash = (hash + 1) % MAX_HASH_SIZE;
			}
		}
		
		// -1 is returned as the ChunkID is not within the chunkBuffer. This is a completely ambiguous result as
		// negative numbers are not ordinal in terms of index locations
		return -1;
	}
	
	/**
	 * Retrieves the ChunkBuffer from the Buffer using it's index location.
	 * @param paramIndex
	 * @return null if there is no BufferChunk at paramIndex in the buffer otherwise the BufferChunk at paramIndex in the buffer.
	 */
	private ChunkBuffer getChunkBuffer(int paramIndex) {
		
		// Checks if the item in the ChunkBuffer at the location of paramIndex is null
		// This means that the ChunkBuffer does not exist
		if(this.getChunkBuffer()[paramIndex] == null) {
			
			// Returns null as the ChunkBuffer does not exist
			return null;
		}
		
		// As a ChunkBuffer exists in the paramIndex location, the ChunkBuffer in that slot is returned.
		return this.getChunkBuffer()[paramIndex];
	}
	
	/**
	 * Retrieves the ChunkBuffer from the Buffer using it's ChunkID.
	 * @param paramChunkID
	 * @return null if there is no BufferChunk available for the paramChunkID in the buffer otherwise the BufferChunk corresponding to the paramChunkID.
	 */
	private ChunkBuffer getChunkBuffer(ChunkID paramChunkID) {
		// Gets the index using the getChunkBufferIndex method of the paramChunkID.
		// This may return -1 as a negative result, therefore it must check for it.
		int index = this.getChunkBufferIndex(paramChunkID); 
		
		// Checks if index is -1 as if it is, then no ChunkBuffer is available.
		if(index == -1) {
			
			// Returns -1 as the ChunkBuffer does not exist for paramChunkID
			return null;
		}
		
		// If the index is not -1 then it will return the value at position index in the chunkBuffer
		return this.getChunkBuffer()[index];
	}
	
	/**
	 * Removes a ChunkBuffer from the chunkBuffer using it's ChunkID
	 * @param paramChunkID
	 */
	private void removeChunkBuffer(ChunkID paramChunkID) { 
		// Gets the index using the getChunkBufferIndex method of the paramChunkID.
		// This may return -1 as a negative result, therefore it must check for it.
		int index = this.getChunkBufferIndex(paramChunkID);
		
		// Checks if index is -1 as if it is, then no ChunkBuffer is available.
		if(index != -1) {
			// Sets the chunkBuffer[index] to an EmptyChunkBuffer. Setting it to an EmptyChunkBuffer means that it is not null. This is vital for other hashes to still work.
			this.getChunkBuffer()[index] = new EmptyChunkBuffer();
		}
	}
	
	/**
	 * Adds a ChunkBuffer to the bufferChunk list.
	 * @param paramChunkBuffer
	 */
	private void addChunkBuffer(ChunkBuffer paramChunkBuffer) {
		
		// Sets hash to the hash of paramChunkBuffer's ChunkID
		int hash = paramChunkBuffer.getChunkID().hash(MAX_HASH_SIZE);
		
		// The iteration value records the total number of times the while loop is repeated
		// Hopefully it never comes close to the MAX_HASH_SIZE.
		int iteration = 0;
		
		// The while loop checks firstly that the element at the location of hash in chunkBuffer is not null and secondly that it is not an EmptyChunkBuffer
		// If these two expressions are met then the indefinite while loop starts.
		while(this.getChunkBuffer()[hash] != null && !(this.getChunkBuffer()[hash] instanceof EmptyChunkBuffer)) {
			
			 // Checks if the iteration is larger than the MAX_HASH_SIZE this means that the list chunkBuffer list is completely full.
			if(iteration >= MAX_HASH_SIZE) {
				// Throws a new exception and breaks out of the loop if the chunkBuffer is completely full.
				throw new RuntimeException(new ChunkBufferFullException()); 
			}
			
			// Adds 1 to the hash and finds the remainder where the dividend is MAX_HASH_SIZE using a modulo operation.
			// This will make sure that the new hash does not increase past the (MAX_HASH_SIZE - 1)
			// which is the maximum addressable index in the chunkBuffer
			hash = (hash + 1) % MAX_HASH_SIZE;
			
			 // Adds 1 to the iteration
			iteration++;
		}
		
		// sets the BufferChunk into the chunkBuffer[hash] as this is an empty spot.
		this.getChunkBuffer()[hash] = paramChunkBuffer;
	}
	
	/**
	 * Adds a vehicle to the chunk buffer
	 * @param paramVehicle
	 */
	public void addVehicleToBuffer(Vehicle paramVehicle) {
		// Must check if the vehicle's location is null. If the vehicle's location
		// is null then it will not be in a chunk, hence can not be added to the buffer.
		if(paramVehicle.getLocation() == null) {
			
			// Throws a VehicleHasNoLocationException as the vehicle cannot be added to the buffer since it has no location
			throw new RuntimeException(new VehicleHasNoLocationException());
		}
		
		// Checks if the chunk that the vehicle is in, is loaded. If it is loaded then it will not be able to be added to the buffer
		if(this.isChunkLoaded(paramVehicle.getLocation().toLocation())) {
			
			//Throws a ChunkIsLoadedException as the Chunk is loaded
			throw new RuntimeException(new ChunkIsLoadedException());
		}
		
		// Gets the ChunkID of the Chunk that the vehicle is currently in
		ChunkID chunkID = ChunkID.getChunkID(paramVehicle.getLocation().toLocation());
		
		// Gets the index of the ChunkID is the chunkBuffer
		// This will return -1 if the chunk is not in the chunkBuffer.
		// However, if the ChunkID is in the chunkBuffer the 
		int index = this.getChunkBufferIndex(chunkID);
		
		// Checks if the index equals -1
		// If the index is -1 then the chunk is not in the chunkBuffer
		if(index == -1) {
			
			// Adds a new ChunkBuffer to chunkBuffer using a new UUID list containing just the vehicle, and the ChunkID of the chunk that the vehicle is currently in.
			this.addChunkBuffer(new ChunkBuffer(chunkID, new UUID[] {paramVehicle.getUuid()}));
			
		// There is already a ChunkBuffer for the chunk, in the chunkBuffer list.
		} else {
			
			// Gets the ChunkBuffer from the chunkBuffer list at the index found.
			ChunkBuffer chunkBuffer = this.getChunkBuffer(index);
			
			// Adds the vehicle's UUID to the ChunkBuffer
			chunkBuffer.addVehicleUUID(paramVehicle.getUuid());
			
			// Sets the chunkID variable to that of the ChunkBuffers. This is to reduce memory usage
			// if there are thousands of vehicles in the VehiclesInChunkInBuffer map, as the ChunkIDs are identical.
			chunkID = chunkBuffer.getChunkID();
		}
		
		// Puts the vehicle's UUID and the ChunkID into the VehiclesInChunkInBuffer map.
		this.getVehiclesInChunkInBuffer().put(paramVehicle.getUuid(), chunkID);
	}
	
	/**
	 * Used to remove a vehicle from the ChunkBuffer
	 * @param paramVehicle
	 */
	public void removeVehicleFromBuffer(Vehicle paramVehicle) {
		
		 // Checks if the paramVehicle's UUID is in the vehiclesInChunkInBuffer. If it is not then it will return.
		if(!this.getVehiclesInChunkInBuffer().containsKey(paramVehicle.getUuid())) return;
		
		// Gets the ChunkID of the Chunk that the vehicle is currently in, from the vehiclesInChunkInBuffer map.
		ChunkID chunkID = this.getVehiclesInChunkInBuffer().get(paramVehicle.getUuid());
		
		// Retrieves the chunkBuffer from the chunkBuffer list from the chunkID
		ChunkBuffer chunkBuffer = this.getChunkBuffer(chunkID); 
		
		// Checks if the UUID list in chunkBuffer is not null
		// This is defensive programming as it should never be null.
		if(chunkBuffer.getUUIDS() != null) {
			
			// Checks if there is a single UUID in the UUID list within the ChunkBuffer. 
			// If there is only one item, the ChunkBuffer can be replaced with an EmptyChunkBuffer.
			if(chunkBuffer.getUUIDS().length == 1){
				
				// removes the ChunkBuffer from the chunkBuffer list using its chunkID.
				this.removeChunkBuffer(chunkID); 
				
			// If there are multiple paramVehicle UUIDs in the ChunkBuffer then only a single UUID should be removed.
			} else {
				
				// Removes the paramVehicle's UUID from the ChunkBuffer UUID list.
				chunkBuffer.removeVehicleUUID(paramVehicle.getUuid());
			}
		}
		
		// Removes the paramVehicle and its corresponding ChunkID from the vehiclesInChunkInBuffer
		this.getVehiclesInChunkInBuffer().remove(paramVehicle.getUuid());
	}
	
	/**
	 * Shows all of the vehicles within paramChunk
	 * @param paramChunk
	 */
	public void loadChunk(Chunk paramChunk) {
	
		// Gets the chunkID for the paramChunk.
		ChunkID chunkID = ChunkID.getChunkID(paramChunk);
		
		// Gets the index of the chunkID within the chunkBuffer list.
		// Value may be -1
		int index = this.getChunkBufferIndex(chunkID);
		
		// Checks if index is -1, if the value is -1 then the chunk is not in the buffer.
		// This means there are no vehicles to show within the chunk, therefore it can return.
		if(index == -1) return;
		
		// Iterates through the UUIDs of vehicles in the ChunkBuffer from the chunkBuffer[index]
		for(UUID uuid : this.getChunkBuffer()[index].getUUIDS()) {
			
			// Retrieves the vehicle object from the Vehicle Manager from the vehicle's UUID
			Vehicle vehicle = VehiclesAPI.getVehicleManager().getVehicleFromUUID(uuid);
			
			// Checks if the vehicle is spawned. This is defensive programming as the vehicle
			// should be removed from the buffer when it is despawned.
			if(vehicle.isSpawned()) {
				
				// If the vehicle is spawned, which it should always be if it is still in the buffer
				// it is shown.
				vehicle.show();
			}
			
			// The vehicle is removed from the vehiclesInChunkInBuffer as the vehicle is no longer
			// in the buffer as it is now shown.
			this.getVehiclesInChunkInBuffer().remove(uuid);
		}
		
		// removes the chunk from the chunkBuffer, as all of the vehicles in the chunk have now been shown.
		this.removeChunkBuffer(chunkID);
	}
	
	/**
	 * Hides all of the vehicles within paramChunk
	 * @param paramChunk
	 */
	public void unloadChunk(Chunk paramChunk) {
		
		// Checks if there are any vehicles on the server, if there are none then there 
		// is no need to complete anything else in this method so it returns.
		if(VehiclesAPI.getVehicleManager().getNumberOfVehicles() == 0) return;
		
		// Gets the chunkID for the paramChunk.
		ChunkID chunkID = ChunkID.getChunkID(paramChunk);
		
		// Dynamic list that is used to store all of the vehicles that are being despawned.
		List<UUID> vehiclesInChunk = new ArrayList<UUID>();
		
		// Iterates through all of the vehicles on the server.
		for(Vehicle vehicle : VehiclesAPI.getVehicleManager().getVehicles().values()) {
			
			// Checks if the vehicle is spawned, if the vehicle is not spawned then it
			// will already be hidden and will not need to be in the ChunkBuffer
			if(vehicle.isSpawned()) {
				
				// Checks that the vehicle has a location and that the ChunkIDs match
				// it checks that the location is not null, as if it is null, then getting the location will cause an error
				// The location returned by the vehicle is in the form of a VehicleLocation therefore it is necessary to turn it to a Spigot location
				// Uses a custom equals to check for equivalence instead of exact same object
				if(vehicle.getLocation() != null && ChunkID.getChunkID(vehicle.getLocation().toLocation()).equals(chunkID)) {
					
					// Hides the vehicle as the chunk is being unloaded
					vehicle.hide();
					
					// Adds vehicle to the list of vehicles in the chunk which is necessary for creating a list of UUIDS for the buffer chunk
					vehiclesInChunk.add(vehicle.getUuid());
					
					// Puts the vehicle's UUID and the chunkID into the vehiclesInChunkInBuffer
					this.getVehiclesInChunkInBuffer().put(vehicle.getUuid(), chunkID);
				}
			}
		}
		
		// If there are no vehicles in the vehiclesInChunkArray then there is no need to have a BufferChunk
		// returns if there are no vehicles in the chunk instead of creating a BufferChunk
		if(vehiclesInChunk.size() == 0) return;
		
		// As no more UUIDS can be added to the vehiclesInChunk, a new UUID list is created with a definite size of vehiclesInChunk
		// All of the elements from vehiclesInChunk will be moved to the new uuids variable.
		UUID[] uuids = new UUID[vehiclesInChunk.size()];
		
		// For loop from 0 to the length of the vehiclesInChunk.
		for(int index = 0; index < vehiclesInChunk.size(); index++) {
			
			// moves items from vehiclesInChunk into uuids.
			uuids[index] = vehiclesInChunk.get(index);
			
		}
		
		// Adds a new ChunkBuffer where ChunkID = chunkId and UUIDS = uuids.
		this.addChunkBuffer(new ChunkBuffer(chunkID, uuids));	
	}
	
	/**
	 * Checks if a chunk is loaded, without loading the chunk.
	 * @param paramLocation
	 * @return returns a boolean value of if the chunk is currently loaded or not
	 */
	public boolean isChunkLoaded(Location paramLocation) {
		
		// Gets the chunkID of the location
		ChunkID chunkID = ChunkID.getChunkID(paramLocation);
		
		// Retrieves all of the loaded chunks on the server. Unfortunately, a binary search will not work
		// as the list is not ordered, and all loaded chunks are added onto the end.
		Chunk[] chunks = paramLocation.getWorld().getLoadedChunks();
		
		// Iterates through the loaded chunks on the server and performs a linear search
		for(Chunk chunk : chunks) {
			
			// checks if the chunkID from the chunk in the iteration is equal to the chunkID
			if(chunkID.equals(ChunkID.getChunkID(chunk))) {
				
				// if they are equal then the chunkID is a loaded chunk therefore returns true
				return true;
			}
		}
		
		// After the iteration, if the boolean has not returned true then the chunk has not been loaded
		return false;
	}
	
	public ChunkManager() {
		chunkBuffer = new ChunkBuffer[MAX_HASH_SIZE];
	}
}
