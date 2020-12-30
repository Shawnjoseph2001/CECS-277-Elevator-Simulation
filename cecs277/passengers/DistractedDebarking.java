package cecs277.passengers;

import cecs277.elevators.Elevator;
import cecs277.events.PassengerNextDestinationEvent;

public class DistractedDebarking implements DebarkingStrategy{
    private boolean mArrivedOnce;
    private boolean mLeftOnce;
    /**
     * Returns true if the given passenger should depart the given elevator, which has opened its doors on some floor --
     * not necessarily the passenger's destination.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
        if(mArrivedOnce && !mLeftOnce) {
            mLeftOnce = true;
            return true;
        }
        else if(mArrivedOnce && mLeftOnce && passenger.getDestination() == elevator.getCurrentFloor().getNumber()) {
            return true;
        }
        else if(passenger.getDestination() == elevator.getCurrentFloor().getNumber()) {
            mArrivedOnce = true;
            return false;
        }
        else {
            return false;
        }

    }

    /**
     * Called when the passenger departed the elevator, giving a chance to schedule the next trip etc.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public void departedElevator(Passenger passenger, Elevator elevator) {
        if(elevator.getCurrentFloor().getNumber() != passenger.getDestination()) {
            elevator.getBuilding().getSimulation().scheduleEvent(new PassengerNextDestinationEvent(5, passenger, elevator.getCurrentFloor()));
            System.out.println(passenger + " Got off on the wrong floor.");
        }
        else {
            System.out.println(passenger + " Got off on the correct floor.");
        }
    }
}
