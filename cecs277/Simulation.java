package cecs277;

import cecs277.buildings.Building;
import cecs277.events.SimulationEvent;
import cecs277.events.SpawnPassengerEvent;
import cecs277.passengers.*;

import java.util.*;

public class Simulation {
	private Random mRandom;
	private PriorityQueue<SimulationEvent> mEvents = new PriorityQueue<>();
	private long mCurrentTime;
	private List<PassengerFactory> mFactories;
	private Building mBuilding;
	
	/**
	 * Seeds the Simulation with a given random number generator.
	 */
	public Simulation(Random random) {
		mRandom = random;
	}
	
	/**
	 * Gets the current time of the simulation.
	 */
	public long currentTime() {
		return mCurrentTime;
	}
	
	/**
	 * Access the Random object for the simulation.
	 */
	public Random getRandom() {
		return mRandom;
	}
	
	/**
	 * Adds the given event to a priority queue sorted on the scheduled time of execution.
	 */
	public void scheduleEvent(SimulationEvent ev) {
		mEvents.add(ev);
	}
	public Building getBuilding() {
		return mBuilding;
	}
	public List<PassengerFactory> getFactories() {
		return mFactories;
	}
	
	public void startSimulation(Scanner input) {
		mFactories = new ArrayList<>(Arrays.asList(new VisitorFactory(10), new WorkerFactory(2), new ChildFactory(3), new DeliveryPersonFactory(2), new StonerFactory(1),new JerkFactory(2)));
		System.out.println("How many floors?");
		int floorNum = /*input.nextInt()*/10;
		System.out.println("How many elevators?");
		int elNum = /*input.nextInt()*/1;
		mBuilding = new Building(floorNum, elNum, this);
		Building b = mBuilding;
		SpawnPassengerEvent ev = new SpawnPassengerEvent(0, b);
		scheduleEvent(ev);

		// Set this boolean to true to make the simulation run at "real time".
		// Change the scale below to less than 1 to speed up the "real time".

		// TODO: the simulation currently stops at 200s. Instead, ask the user how long they want to simulate.
		System.out.println("How long would you like to run the simulation?");
		int nextSimLength = /*input.nextInt()*/2027;
		System.out.println(b);
		while(nextSimLength != -1)
		{
		long nextStopTime = mCurrentTime + nextSimLength;
		// If the next event in the queue occurs after the requested sim time, then just fast forward to the requested sim time.
			assert mEvents.peek() != null;
			if (mEvents.peek().getScheduledTime() >= nextStopTime) {
			mCurrentTime = nextStopTime;
		}

		// As long as there are events that happen between "now" and the requested sim time, process those events and
		// advance the current time along the way.
		while (!mEvents.isEmpty() && mEvents.peek().getScheduledTime() <= nextStopTime) {
			SimulationEvent nextEvent = mEvents.poll();

			while(nextEvent == null) {
				mEvents.remove(nextEvent);
				nextEvent = mEvents.poll();
			}
			long diffTime = nextEvent.getScheduledTime() - mCurrentTime;

			mCurrentTime += diffTime;
			nextEvent.execute(this);
			System.out.println(nextEvent);

		}

		// TODO: print the Building after simulating the requested time.
		System.out.println(b.toString());
			System.out.println("How long would you like to run the simulation?");
			nextSimLength = input.nextInt();
		
		/*
		 TODO: the simulation stops after one round of simulation. Write a loop that continues to ask the user
		 how many seconds to simulate, simulates that many seconds, and stops only if they choose -1 seconds.
		*/
	}
	}
	
	public static void main(String[] args) {
		// TODO: ask the user for a seed value and change the line below.
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter a seed value:");
		int seed = /*input.nextInt();*/3;
		Simulation sim = new Simulation(new Random(seed));
		sim.startSimulation(input);
	}
}
