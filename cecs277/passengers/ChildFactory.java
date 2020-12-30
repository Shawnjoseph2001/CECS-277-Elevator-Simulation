package cecs277.passengers;

import cecs277.Simulation;
import cecs277.events.SingleDestinationTravel;

import java.util.Random;

public class ChildFactory implements PassengerFactory{
    private int mWeight;
    public ChildFactory(int weight) {
        mWeight = weight;
    }

    /**
     * Gets the name of this factory, that is, a brief description of a passenger that it creates.
     */
    @Override
    public String factoryName() {
        return " ChildFactory";
    }

    /**
     * Gets a short (1 or 2 letter) abbreviation for the name of this factory.
     */
    @Override
    public String shortName() {
        return "CF";
    }

    /**
     * Gets the weight with which this factory should be selected in a uniform random selection of known factories.
     */
    @Override
    public int factoryWeight() {
        return mWeight;
    }

    /**
     * Creates a BoardingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public BoardingStrategy createBoardingStrategy(Simulation simulation) {
        return new AwkwardBoarding(4);
    }

    /**
     * Creates a TravelStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public TravelStrategy createTravelStrategy(Simulation simulation) {
        Random r = simulation.getRandom();
        int destinationFloor = 2 + r.nextInt(simulation.getBuilding().getFloorCount() - 1);
        int visitDuration = 3600 + (int)(r.nextGaussian() * 1201);
        return new SingleDestinationTravel(destinationFloor, visitDuration);
    }

    /**
     * Creates a EmbarkingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
        return new ClumsyEmbarking();
    }

    /**
     * Creates a DebarkingStrategy used by passengers represented by this factory.
     *
     * @param simulation a reference to the simulation, in case the strategy needs a source of random numbers
     *                   or other information about the simulation.
     */
    @Override
    public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
        return new DistractedDebarking();
    }
}
