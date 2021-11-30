/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import movement.map.MapRoute;
import movement.map.MapNode;
import core.Coord;
import core.Settings;
import core.SettingsError;

/**
 * This class controls the movement of busses. It informs the bus control system
 * the bus is registered with every time the bus stops.
 *
 * @author Frans Ekman
 */
public class BusMovement extends MapRouteMovement {

	
	/** Per node group setting used for selecting an alternate route file ({@value}) */
	public static final String ALTERNATE_ROUTE_FILE_S = "alternateRouteFile";
	private BusControlSystem controlSystem;
	private int id;
	private static int nextID = 0;
	private boolean startMode;
	private boolean mainRoute = true;
	private List<Coord> stops;
	private HashMap<Integer, ArrayList<Integer>> intercepts = null;
	String alternateFileName;

	int type;
	/** Prototype's reference to alternate routes read for the group */
	private List<MapRoute> altRoutes = null;
	
	/** Prototype's reference to the original routes that wont be tampered with */
	private List<MapRoute> origRoutes = null;
	/**
	 * Creates a new instance of BusMovement
	 * @param settings
	 */
	public BusMovement(Settings settings) {
		super(settings);
		this.type = settings.getInt(ROUTE_TYPE_S);
		int bcs = settings.getInt(BusControlSystem.BUS_CONTROL_SYSTEM_NR);
		controlSystem = BusControlSystem.getBusControlSystem(bcs);
		controlSystem.setMap(super.getMap());
		this.id = nextID++;
		controlSystem.registerBus(this);
		startMode = true;
		stops = new LinkedList<Coord>();
		List<MapNode> stopNodes = super.getStops();
		for (MapNode node : stopNodes) {
			stops.add(node.getLocation().clone());
		}
		controlSystem.setBusStops(stops);
		try {
			this.alternateFileName = settings.getSetting(ALTERNATE_ROUTE_FILE_S);
			this.registerAltRoute(this.type, this.alternateFileName);
			
		} catch (SettingsError e) {
			//TODO: handle exception
			System.out.println("could not find any alternate routes defined");
		}
	}

	/**
	 * Create a new instance from a prototype
	 * @param proto
	 */
	public BusMovement(BusMovement proto) {
		super(proto);
		this.origRoutes = proto.origRoutes;
		this.altRoutes = proto.altRoutes;
		// this.route = proto.getRoute().replicate();
		// this.intercepts = proto.intercepts;
		this.controlSystem = proto.controlSystem;
		this.allRoutes = proto.allRoutes;
		this.id = nextID++;
		controlSystem.registerBus(this);
		startMode = true;
	}

	public void registerAltRoute(int type, String altFilename){
		this.origRoutes = allRoutes;
		this.altRoutes = MapRoute.readRoutes(altFilename, type, getMap());
		/** for now we will cater to simply passing in a single route from out wkt file */
		MapRoute altRoute = altRoutes.get(0);
		MapRoute fullRoute = allRoutes.get(0);
		int size = altRoute.getNrofStops();
		Coord firstNode = altRoute.getStops().get(0).getLocation();
		Coord lastNode = altRoute.getStops().get(size - 1).getLocation();

		if(this.intercepts == null) {
			this.intercepts = new HashMap<Integer, ArrayList<Integer>>();
		}
		
		int matchingStartIndex = -1;
		int matchingEndIndex = -1;
		// for(MapRoute route: allRoutes){
			for (int i = 0; i < fullRoute.getStops().size(); i++) {
			// for(MapNode node: fullRoute.getStops()){
				MapNode node = fullRoute.getStops().get(i);
				/** check to see if the variation attaches to the route at its starting and ending points */
				if(node.getLocation().getX() == firstNode.getX() && firstNode.getY() == node.getLocation().getY()){
					matchingStartIndex = i;
					System.out.print("we have a start match");
				}
				if(node.getLocation().getX() == lastNode.getX() && lastNode.getY() == node.getLocation().getY()){
					matchingEndIndex = i;
					System.out.print("we have an end match");
				}
			}
			if(matchingEndIndex != -1 && matchingStartIndex != -1) {
				ArrayList<Integer> indecies = new ArrayList<Integer>();
				indecies.add(matchingStartIndex);
				indecies.add(matchingEndIndex);
				this.intercepts.put(0, indecies);
				System.out.print("adding proper intercepts");
				System.out.println(this.intercepts);
			}
		// }
	}

	public void changeRoute(){
		System.out.println(this.intercepts);
		if(this.intercepts != null){
			if(this.mainRoute){
				this.route.insertStops(this.intercepts.get(0).get(0), this.intercepts.get(0).get(1), this.altRoutes.get(0).getStops());
				this.mainRoute = false;
			} else {
				this.route.insertStops(0, this.route.getNrofStops() - 1, origRoutes.get(0).getStops());
				this.mainRoute = true;
			}
		}
	}

	public MapRoute getRoute(){
		return this.route;
	}

	@Override
	public Coord getInitialLocation() {
		return (super.getInitialLocation()).clone();
	}

	@Override
	public Path getPath() {
		Coord lastLocation = (super.getLastLocation()).clone();
		Path path = super.getPath();
		if (!startMode) {
			controlSystem.busHasStopped(id, lastLocation, path);
		}
		startMode = false;
		return path;
	}

	@Override
	public BusMovement replicate() {
		return new BusMovement(this);
	}

	/**
	 * Returns unique ID of the bus
	 * @return unique ID of the bus
	 */
	public int getID() {
		return id;
	}

}
