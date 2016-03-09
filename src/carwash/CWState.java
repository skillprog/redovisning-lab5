package carwash;

import java.util.ArrayList;
import java.util.Collections;

import random.ExponentialRandomStream;
import random.UniformRandomStream;
import sim.SimEvent;
import sim.SimState;

public class CWState extends SimState {
	
	private double lambda = 1.5;
	private double maxSimTime = 15; //Best�m n�r programmet ska avsluta
	
	private int fastWashers = 0;
	private int slowWashers = 0;
	private int maxQueue = 0;

	private int queueSize = 0;
	private int rejectedCars = 0;
	private double idleTime = 0;
	private double simulationTime = 0;
	private String event ="";
	private String carId = "-";
	private double previousEventTime = 0;
	public int meantimeCalc = 0;
	
	private double queueTime = 0;
	private double lastQueueTime = 0;
	
	public ArrayList<Double> carWashQueue = new ArrayList<>();
	
	private long seed = 1234;
	private double lowerFast = 2.8;
	private double upperFast = 5.6;
	private double lowerSlow = 4.5;
	private double upperSlow = 6.7;
	
	private UniformRandomStream fastRandom;
	private UniformRandomStream slowRandom;
	private ExponentialRandomStream expoRandom;


	public CWState(int fastWashers, int slowWashers , int maxQueueSize){
		this.fastWashers = fastWashers;
		this.slowWashers = slowWashers;
		this.maxQueue = maxQueueSize;
		this.fastRandom = new UniformRandomStream(lowerFast,upperFast,seed);
		this.slowRandom = new UniformRandomStream(lowerSlow,upperSlow,seed);
		this.expoRandom = new ExponentialRandomStream(lambda,seed);
	}

	//TODO can we refactor this?
	public void sort(){ //Kr�vs ifall en carId I k�n avslutas f�re ett carId tidigare i k�n. �ndrar positionen s� den blir korrekt
		if(carWashQueue.size() > 2){
			for(int i = 0; i < carWashQueue.size()-2; i += 2){ //byter plats s� den st�rsta tiden hamnar l�ngst bak
				if(carWashQueue.get(i) > carWashQueue.get(i+2)) {
					Collections.swap(carWashQueue, i, i+2);
					Collections.swap(carWashQueue, i+1, i+3); //oj�mna index h�ller koll p� snabb/l�ngsam tv�tt
				}
			}
		}	
	}

	public double getMaxTime(){
		return maxSimTime;
	}
	public double getIdleTime(){
		return idleTime;
	}
	
	public int getFastWashers(){
		return fastWashers;
	}
	
	public int getSlowWashers(){
		return slowWashers;
	}
	
	public double getLowerFast(){
		return lowerFast;
	}
	
	public double getUpperFast(){
		return upperFast;
	}
	
	public double getLowerSlow(){
		return lowerSlow;
	}
	
	public double getUpperSlow(){
		return upperSlow;
	}
	
	public double getLambda(){
		return lambda;		
	}
	
	public long getSeed(){
		return seed;
	}
	
	public int getMaxQueueSize(){
		return maxQueue;
	}
	
	public double getQueueTime(){
		return queueTime;
	}
	
	public int getRejectedCars(){
		return rejectedCars;
	}
	
	public double getFastRandom(){
		return fastRandom.next();
	}
	
	public double getSlowRandom(){
		return  slowRandom.next();
	}
	
	public double getExpoRandom(){
		return expoRandom.next();
	}
	
	public String getEvent(){
		return event;
	}
	
	public double getTime(){
		return simulationTime;
	}
	
	public String getCarId(){
		return carId;
	}


	public double getMeanQueue(){
		return getQueueTime()/meantimeCalc;
	}
	
	public int getQueueSize(){
		return queueSize;
	}
	
	public double getPreviousEventTime(){
		return previousEventTime;
	}
	
	public void setPreviousEventTime(double x){
		previousEventTime = x;
	}
	
	public void setRejected(int x){
		rejectedCars += x;
	}
	
	public void setIdle(double x){
		idleTime += x;
	}
	
	public void setQueueSize(int x){
		queueSize += x;
	}
	
	public void setQueueTime(double time){ //R�knar ut queueTime
		if(lastQueueTime == 0){
			lastQueueTime = time;
		}
		else{
			queueTime += queueSize * (time - lastQueueTime);
			lastQueueTime = time;
		}
	}
	
	public void setCarId(int x){

		carId = "" + x;
	}
	
	public void setEvent(int x){

		if(x == 1){
			event = "Arrive";
		}
		else if (x == 2){
			event = "Leave";
		}
		else if(x == 0){
			event = "Start";
		}
		else{
			event = "Stop";
			carId = "-";
		}
		setChanged();
		notifyObservers();
	}
	
	public void setSimulationTime(double x){
		simulationTime = x;
	}
	
	public void changeFastWashers(double x){

		fastWashers += x;
	}
	
	public void changeSlowWashers(double x){

		slowWashers += x;
	}
	private int counter = 0;

	@Override
	public ArrayList<SimEvent> addInSequence(ArrayList<SimEvent> carWashEventList){
		double time = this.getPreviousEventTime()+this.getExpoRandom();
		CWEvent e = new CWEvent(time, counter, this);
		carWashEventList.add(e);
		counter++;
		return carWashEventList;
	}

}
