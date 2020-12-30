package cecs277.passengers;

import cecs277.elevators.Elevator;

public class AttentiveDebarking implements DebarkingStrategy {

    /**
     * Returns true if the given passenger should depart the given elevator, which has opened its doors on some floor --
     * not necessarily the passenger's destination.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
        return elevator.getCurrentFloor().getNumber() == passenger.getDestination();
    }

    /**
     * Called when the passenger departed the elevator, giving a chance to schedule the next trip etc.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public void departedElevator(Passenger passenger, Elevator elevator) {
        passenger.scheduleDestination(elevator.getBuilding().getFloor(passenger.getDestination()));
    }
}
