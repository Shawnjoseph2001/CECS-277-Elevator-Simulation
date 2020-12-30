package cecs277.passengers;

import cecs277.Simulation;

public class StonerFactory extends VisitorFactory{
    public StonerFactory(int weight) {
        super(weight);
    }

    /**
     * Gets the name of this factory, that is, a brief description of a passenger that it creates.
     */
    @Override
    public String factoryName() {
        return "StonerFactory";
    }

    /**
     * Gets a short (1 or 2 letter) abbreviation for the name of this factory.
     */
    @Override
    public String shortName() {
        return "SF";
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
        return new ConfusedDebarking();
    }
}
