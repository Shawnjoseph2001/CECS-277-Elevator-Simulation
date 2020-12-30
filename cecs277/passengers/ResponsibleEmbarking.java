package cecs277.passengers;

import cecs277.elevators.Elevator;

public class ResponsibleEmbarking implements EmbarkingStrategy {

    /**
     * Called when the passenger entered the given elevator, giving a chance to request floors, etc.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        elevator.addDestination(passenger.getDestination());
    }
}
