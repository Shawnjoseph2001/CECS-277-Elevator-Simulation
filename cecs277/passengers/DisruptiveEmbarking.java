package cecs277.passengers;

import cecs277.buildings.Floor;
import cecs277.elevators.Elevator;

public class DisruptiveEmbarking implements EmbarkingStrategy {

    /**
     * Called when the passenger entered the given elevator, giving a chance to request floors, etc.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public void enteredElevator(Passenger passenger, Elevator elevator) {
        if(passenger.getDestination() < elevator.getCurrentFloor().getNumber()) {
            for(int i = 0; i < passenger.getDestination(); i++) {
                elevator.addDestination(i);
            }
        }
        else {
            for(int i = passenger.getDestination(); i < elevator.getBuilding().getFloorCount(); i++) {
                elevator.addDestination(i);
            }
        }
    }
}
