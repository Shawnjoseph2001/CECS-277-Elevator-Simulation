package cecs277.elevators;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.events.ElevatorStateEvent;
import cecs277.passengers.Passenger;

import java.util.*;
import java.util.stream.Collectors;

public class Elevator implements FloorObserver {
	
	public enum ElevatorState {
		IDLE_STATE,
		DOORS_OPENING,
		DOORS_CLOSING,
		DOORS_OPEN,
		ACCELERATING,
		DECELERATING,
		MOVING
	}
	public enum Direction {
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	
	private int mNumber;
	private Building mBuilding;
	private OperationMode mode;

	private ElevatorState mCurrentState = ElevatorState.IDLE_STATE;
	private Direction mCurrentDirection = Direction.NOT_MOVING;
	private Floor mCurrentFloor;
	private List<Passenger> mPassengers = new ArrayList<>();
	
	private List<ElevatorObserver> mObservers = new ArrayList<>();
	public Set<Floor> mFloorRequests = new HashSet<>();
	
	
	public Elevator(int number, Building bld) {
		mNumber = number;
		mBuilding = bld;
		mCurrentFloor = bld.getFloor(1);
		scheduleModeChange(new IdleMode(), ElevatorState.IDLE_STATE, 0);
	}
	public ElevatorState getCurrentState() {
		return mCurrentState;
	}
	public List<ElevatorObserver> getObservers() {
		return mObservers;
	}
	
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	void scheduleStateChange(ElevatorState state, long timeFromNow) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, this));
	}
	
	/**
	 * Adds the given passenger to the elevator's list of passengers, and requests the passenger's destination floor.
	 */
	public void addPassenger(Passenger passenger) {
		// TODO: add the passenger's destination to the set of requested floors.
		mPassengers.add(passenger);
	}
	public void addDestination(int floor) {
		if(mCurrentFloor != mBuilding.getFloor(floor)) {
			mFloorRequests.add(mBuilding.getFloor(floor));
		}
	}
	public void removePassenger(Passenger passenger) {
		mPassengers.remove(passenger);
	}

	
	/**
	 * Schedules the elevator's next state change based on its current state.
	 */
	public void tick() { /*
		// TODO: port the logic of your state changes from Project 1, accounting for the adjustments in the spec.
		// TODO: State changes are no longer immediate; they are scheduled using scheduleStateChange().

		// Example of how to trigger a state change:
		// scheduleStateChange(ElevatorState.MOVING, 3); // switch to MOVING and call tick(), 3 seconds from now.
		switch(mCurrentState) {
			case IDLE_STATE:
				mCurrentFloor.addObserver(this);
				for(ElevatorObserver e:mObservers) {
					e.elevatorWentIdle(this);
				}
				break;
			case DOORS_OPENING:
				scheduleStateChange(ElevatorState.DOORS_OPEN, 2);
				break;
			case DOORS_OPEN:
				int beforeFloor = mCurrentFloor.getWaitingPassengers().size();
				int beforeElevator = getPassengerCount();
				ArrayList<ElevatorObserver> elements = new ArrayList<>(mObservers);
				for(ElevatorObserver e:elements) {
					e.elevatorDoorsOpened(this);
				}
				mFloorRequests.remove(mCurrentFloor);
				int afterFloor = mCurrentFloor.getWaitingPassengers().size();
				int afterElevator = getPassengerCount();
				int boarded = beforeFloor - afterFloor;
				int left = boarded + beforeElevator - afterElevator;
				scheduleStateChange(ElevatorState.DOORS_CLOSING, 1 + ((boarded + left) / 2));
				break;
			case DOORS_CLOSING:
				if(greaterFloorRequest()) {
					scheduleStateChange(ElevatorState.ACCELERATING, 2);
				}
				else if(mFloorRequests.size() > 0) {
					Floor mNextRequest = mFloorRequests.iterator().next();
					if(mNextRequest.getNumber() > mCurrentFloor.getNumber()) {
						mCurrentDirection = Direction.MOVING_UP;
					}
					else {
						mCurrentDirection = Direction.MOVING_DOWN;
					}
					scheduleStateChange(ElevatorState.DOORS_OPENING, 2);
				}
				else {
					mCurrentDirection = Direction.NOT_MOVING;
					scheduleStateChange(ElevatorState.IDLE_STATE, 2);
				}
				break;
			case ACCELERATING:
				mCurrentFloor.removeObserver(this);
				scheduleStateChange(ElevatorState.MOVING, 3);
				break;
			case MOVING:
				if(mCurrentDirection == Direction.MOVING_UP && mCurrentFloor.getNumber() < mBuilding.getFloorCount()) {
					mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber() + 1);
				}
				else if(mCurrentDirection == Direction.MOVING_DOWN && mCurrentFloor.getNumber() > 1){
					mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber() - 1);
				}
				else {
					scheduleStateChange(ElevatorState.IDLE_STATE, 0);
					mCurrentDirection = Direction.NOT_MOVING;
				}
				if(mFloorRequests.contains(mCurrentFloor) || mCurrentFloor.directionIsPressed(mCurrentDirection)) {
					scheduleStateChange(ElevatorState.DECELERATING, 2);
				}
				else {
					scheduleStateChange(ElevatorState.MOVING, 2);
				}
				break;
			case DECELERATING:
				boolean b = false;
		for(Floor f:mFloorRequests) {
			if (f.getNumber() < mCurrentFloor.getNumber() && mCurrentDirection == Direction.MOVING_DOWN
					|| f.getNumber() > mCurrentFloor.getNumber() && mCurrentDirection == Direction.MOVING_UP) {
				b = true;
				break;
			}
		}
		if(b || mCurrentFloor.directionIsPressed(mCurrentDirection)) {

		}
		else if(mCurrentFloor.getWaitingPassengers().size() > 0) {
			if(mCurrentFloor.directionIsPressed(Direction.MOVING_UP)) {
				mCurrentDirection = Direction.MOVING_UP;
			}
			else {
				mCurrentDirection = Direction.MOVING_DOWN;
			}

		}
		else {
			mCurrentDirection = Direction.NOT_MOVING;
		}
		List<ElevatorObserver> el = new ArrayList<>(mObservers);
		for(ElevatorObserver e:el) {
			e.elevatorDecelerating(this);
		}
		scheduleStateChange(ElevatorState.DOORS_OPENING, 3);
		}*/
		mode.tick(this);
	}
	boolean greaterFloorRequest() {
		if(mCurrentDirection == Direction.MOVING_UP) {
			for(Floor i:mFloorRequests) {
				if(i.getNumber() > mCurrentFloor.getNumber()) {
					return true;
				}
			}
		}
		else if(mCurrentDirection == Direction.MOVING_DOWN) {
			for(Floor i:mFloorRequests) {
				if(i.getNumber() < mCurrentFloor.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Sends an idle elevator to the given floor.
	 */
	public void dispatchTo(Floor floor, Elevator.Direction direction) { /*
		// TODO: if we are currently idle and not on the given floor, change our direction to move towards the floor.
		// TODO: set a floor request for the given floor, and schedule a state change to ACCELERATING immediately.
		if(mCurrentState == ElevatorState.IDLE_STATE && mCurrentFloor != floor) {
			if(mCurrentFloor.getNumber() > floor.getNumber()) {
				mCurrentDirection = Direction.MOVING_DOWN;
			}
			else {
				mCurrentDirection = Direction.MOVING_UP;
			}
			mFloorRequests.add(floor);
			scheduleStateChange(ElevatorState.ACCELERATING, 0);
		} */
		mode.dispatchToFloor(this, floor, direction);
	}
	
	// Simple accessors
	public Floor getCurrentFloor() {
		return mCurrentFloor;
	}
	
	public Direction getCurrentDirection() {
		return mCurrentDirection;
	}
	
	public Building getBuilding() {
		return mBuilding;
	}
	
	/**
	 * Returns true if this elevator is in the idle state.
	 * @return
	 */
	public boolean canBeDispatchedToFloor(Floor floor) {
		// TODO: complete this method.
		return mode.canBeDispatchedToFloor(this, floor);
	}

	
	// All elevators have a capacity of 10, for now.
	public int getCapacity() {
		return 10;
	}
	
	public int getPassengerCount() {
		return mPassengers.size();
	}
	
	// Simple mutators
	public void setState(ElevatorState newState) {
		mCurrentState = newState;
	}
	
	public void setCurrentDirection(Direction direction) {
		mCurrentDirection = direction;
	}
	public int getNumber() {
		return mNumber;
	}
	
	public void setCurrentFloor(Floor floor) {
		mCurrentFloor = floor;
	}
	
	// Observers
	public void addObserver(ElevatorObserver observer) {
		if(!mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}
	
	public void removeObserver(ElevatorObserver observer) {
		if(mObservers.contains(observer)) {
			mObservers.remove(observer);
		}
	}
	
	
	// FloorObserver methods
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// Not used.
	}
	
	/**
	 * Triggered when our current floor receives a direction request.
	 */
	@Override
	public void directionRequested(Floor sender, Direction direction) { /*
		// TODO: if we are currently idle, change direction to match the request. Then alert all our observers that we are decelerating,
		// TODO: then schedule an immediate state change to DOORS_OPENING.
		if(isIdle()) {
			mCurrentDirection = direction;
			List<ElevatorObserver> l = new ArrayList<>(mObservers);
			for(ElevatorObserver o:l) {
				o.elevatorDecelerating(this);
			}
			scheduleStateChange(ElevatorState.DOORS_OPENING, 0);
		}
		*/
		mode.directionRequested(this, sender, direction);
	}
	public void scheduleModeChange(OperationMode o, ElevatorState e, int i) {
		scheduleStateChange(e, i);
		mode = o;
	}
	public void announceElevatorIdle() {
		for(ElevatorObserver e:mObservers) {
			e.elevatorWentIdle(this);
		}
	}
	public void announceElevatorDecelerating() {
		Iterable<ElevatorObserver> it= new ArrayList<>(mObservers);
		for(ElevatorObserver e:it) {
			e.elevatorDecelerating(this);
		}
	}


	public Set<Floor> getFloorRequests() {
		return mFloorRequests;
	}

	public List<Passenger> getPassengers() {
		return mPassengers;
	}

	// Voodoo magic.
	@Override
	public String toString() {
		return "Elevator " + mNumber + " - " + " [" + mode + "] " + mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
				+ "[" + mPassengers.stream().map(m -> m.toString()).collect(Collectors.joining(", ")) + "]"
		 + " {" + mFloorRequests.stream().map(p -> Integer.toString(p.getNumber())).collect(Collectors.joining(", "))
		 + "}";
	}
	
}
