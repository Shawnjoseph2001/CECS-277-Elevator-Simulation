package cecs277.passengers;

import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;

/**
 * A passenger that is either waiting on a floor or riding an elevator.
 */
public class Passenger implements FloorObserver, ElevatorObserver {
	// An enum for determining whether a Passenger is on a floor, an elevator, or busy (visiting a room in the building).
	public enum PassengerState {
		WAITING_ON_FLOOR,
		ON_ELEVATOR,
		BUSY
	}
	
	// A cute trick for assigning unique IDs to each object that is created. (See the constructor.)
	private static int mNextId;
	private BoardingStrategy mBoardingStrategy;
	private DebarkingStrategy mDebarkingStrategy;
	private EmbarkingStrategy mEmbarkingStrategy;
	private TravelStrategy mTravelStrategy;
	private String mFactoryName;
	protected static int nextPassengerId() {
		return ++mNextId;
	}
	
	private int mIdentifier;
	private PassengerState mCurrentState;

	public Passenger(BoardingStrategy boardingStrategy, DebarkingStrategy debarkingStrategy, EmbarkingStrategy embarkingStrategy, TravelStrategy travelStrategy, String factoryName) {
		mBoardingStrategy = boardingStrategy;
		mDebarkingStrategy = debarkingStrategy;
		mEmbarkingStrategy = embarkingStrategy;
		mTravelStrategy = travelStrategy;
		mIdentifier = nextPassengerId();
		mCurrentState = PassengerState.WAITING_ON_FLOOR;
		mFactoryName = factoryName;
	}
	
	public void setState(PassengerState state) {
		mCurrentState = state;
	}
	
	/**
	 * Gets the passenger's unique identifier.
	 */
	public int getId() {
		return mIdentifier;
	}
	public void scheduleDestination(Floor floor) {
		mTravelStrategy.scheduleNextDestination(this, floor);
	}
	
	/**
	 * Handles an elevator arriving at the passenger's current floor.
	 */
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// This is a sanity check. A Passenger should never be observing a Floor they are not waiting on.
		if (mBoardingStrategy.willBoardElevator(this, elevator)) {

			// TODO: check if the elevator is either NOT_MOVING, or is going in the direction that this passenger wants.
			// If so, this passenger becomes an observer of the elevator.
			Elevator.Direction passState;
			if(getDestination() > floor.getNumber()) {
				passState = Elevator.Direction.MOVING_UP;
			}
			else if(getDestination() < floor.getNumber()){
				passState = Elevator.Direction.MOVING_DOWN;
			}
			else {
				passState = Elevator.Direction.NOT_MOVING;
				System.out.println("Uhh...");
			}
			if(elevator.getCurrentDirection() == Elevator.Direction.NOT_MOVING || elevator.getCurrentDirection() == passState) {
				elevator.addObserver(this);
			}

		}
	}

	/**
	 * Triggered when an elevator enters the DECELERATING state.
	 *
	 * @param sender
	 */
	@Override
	public void elevatorDecelerating(Elevator sender) {
	}

	/**
	 * Handles an observed elevator opening its doors. Depart the elevator if we are on it; otherwise, enter the elevator.
	 */
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// The elevator is arriving at our destination. Remove ourselves from the elevator, and stop observing it.
		// Does NOT handle any "next" destination...
		if (mDebarkingStrategy.willLeaveElevator(this, elevator)) {
			// TODO: remove this passenger from the elevator, and as an observer of the elevator. Call the
			// leavingElevator method to allow a derived class to do something when the passenger departs.
			// Set the current state to BUSY.
			elevator.removeObserver(this);
			elevator.removePassenger(this);
			leavingElevator(elevator);
			setState(PassengerState.BUSY);
		}
		// The elevator has arrived on the floor we are waiting on. If the elevator has room for us, remove ourselves
		// from the floor, and enter the elevator.
		else if (mCurrentState == PassengerState.WAITING_ON_FLOOR && elevator.getCurrentFloor().getNumber() != getDestination()) {
			// TODO: determine if the passenger will board the elevator using willBoardElevator.
			// If so, remove the passenger from the current floor, and as an observer of the current floor;
			// then add the passenger as an observer of and passenger on the elevator. Then set the mCurrentState
			// to ON_ELEVATOR.
			if(mBoardingStrategy.willBoardElevator(this, elevator)) {
				elevator.getCurrentFloor().removeObserver(this);
				elevator.getCurrentFloor().removeWaitingPassenger(this);
				elevator.addPassenger(this);
				elevator.addObserver(this);
				mCurrentState = PassengerState.ON_ELEVATOR;
				mEmbarkingStrategy.enteredElevator(this, elevator);
			}
			
			
			
			
			
			
			
		}
		else if(mCurrentState == PassengerState.WAITING_ON_FLOOR && elevator.getCurrentFloor().getNumber() == getDestination()){
			elevator.removeObserver(this);
			elevator.removePassenger(this);
		setState(PassengerState.BUSY);
			leavingElevator(elevator);
		}
	}
	
	/**
	 * Returns the passenger's current destination (what floor they are travelling to).
	 */
	public int getDestination() {
		return mTravelStrategy.getDestination();
	}
	
	/**
	 * Called to determine whether the passenger will board the given elevator that is moving in the direction the
	 * passenger wants to travel.
	 */
	protected boolean willBoardElevator(Elevator elevator) {
		return mBoardingStrategy.willBoardElevator(this, elevator);
	}
	
	/**
	 * Called when the passenger is departing the given elevator.
	 */
	protected void leavingElevator(Elevator elevator) {
		mDebarkingStrategy.departedElevator(this, elevator);
	}
	
	// This will be overridden by derived types.
	@Override
	public String toString() {
		return mFactoryName + mIdentifier;
	}
	
	@Override
	public void directionRequested(Floor sender, Elevator.Direction direction) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// Don't care about this.
	}
	
	// The next two methods allow Passengers to be used in data structures, using their id for equality. Don't change 'em.
	@Override
	public int hashCode() {
		return Integer.hashCode(mIdentifier);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Passenger passenger = (Passenger)o;
		return mIdentifier == passenger.mIdentifier;
	}
	
}