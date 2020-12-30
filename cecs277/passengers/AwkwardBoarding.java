package cecs277.passengers;

import cecs277.elevators.Elevator;

public class AwkwardBoarding implements BoardingStrategy {
int mThreshold;
public AwkwardBoarding(int threshold) {
    mThreshold = threshold;
}

    /**
     * Returns true if the given passenger will board the given elevator.
     *
     * @param passenger
     * @param elevator
     */
    @Override
    public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
        if(mThreshold <= elevator.getPassengerCount() && elevator.getPassengerCount() <= elevator.getCapacity()) {
            mThreshold = mThreshold + 2;
            return false;
        }
        return true;
    }
}
