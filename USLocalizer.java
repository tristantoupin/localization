package localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 80, DATACONSIDERED = 5;
	public static double DIST_WALL = 30, NOISE_MARGIN = 3;
	public static float[] usStored = new float[10];
	
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private Navigation nav;
	
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		
		this.nav = new Navigation(odo);

	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			while(getFilteredData() < DIST_WALL + NOISE_MARGIN){
				nav.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
			}
			nav.setSpeeds(0,0);
			
			angleA = odo.getAng();		//Temporary angle
			Sound.twoBeeps();
			
			try{
		    Thread.sleep(2000);                 //1000 milliseconds is one second.
			} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
			}

			//going the otherway to find the new (DIST_WALL - NOISE_MARGIN) angleA
			while(getFilteredData() < DIST_WALL - NOISE_MARGIN){
				nav.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
			}
			nav.setSpeeds(0,0);
			
			Sound.twoBeeps();

			try{
			    Thread.sleep(2000);                 //1000 milliseconds is one second.
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
			//find real angleA, average of the two found
			angleA = (angleA + odo.getAng())/2;
			
			
			nav.turnTo(-Math.PI/8);
			
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > DIST_WALL - NOISE_MARGIN){
				nav.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
			}
			// angleA is clockwise from angleB, so assume the average of the
			nav.setSpeeds(0,0);
			
			angleB = odo.getAng();
			
			Sound.twoBeeps();

			try{
			    Thread.sleep(2000);                 //1000 milliseconds is one second.
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
			
			while(getFilteredData() > DIST_WALL + NOISE_MARGIN){
				nav.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
			}
			nav.setSpeeds(0,0);
			
			angleB = (angleB + odo.getAng())/2;
			Sound.twoBeeps();

			try{
			    Thread.sleep(2000);                 //1000 milliseconds is one second.
			} catch (InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
			
			if(angleA > angleB){
				angleA = angleA - 360;
			}
			double north;
			if (angleA > angleB){
				north = 225 - (angleA + angleB)/2;
			} else {
				north = 45 - (angleA + angleB)/2;
			}
			
			Sound.beep();
			nav.turnTo(north*Math.PI/180);

			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			// rotate the robot until it sees a wall
			while(getFilteredData() > DIST_WALL){
				nav.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
			}

			angleA = odo.getAng();
			
			//switch directions and rotate until the robot sees the wall.


			// rotate until the robot no longer sees the wall and latch the angle.
			while(getFilteredData() < DIST_WALL){
				nav.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
			}
			nav.setSpeeds(0,0);

			angleB = odo.getAng();
			
			//if our angle A is bigger than B, subtract 360.
			if(angleA > angleB){
				angleA = angleA - 360;
			}
			//calculate the average angle andd the zero point (zeropoint is x axis)
			double averageAngle = (angleA + angleB)/2;
			double ZeroPoint =  angleB - averageAngle + 45;


			//rotate to the diagonal + 45 (to the x axis).
			nav.turnTo(ZeroPoint, true);
			
			// update the odometer position to 0 0 0. The x and y will be wrong
			// but that will be fixed by the LightLocalizer
			odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
		}
	}
	
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		
		float distance = usData[0]*100;
		storeUS(distance);
			
		return getAverage();
	}
	
	private void storeUS(float lastRead){
		if( lastRead > 255 ){
			createStore();
			usStored[0] = lastRead;
		}
	}
	
	private void createStore(){
		for (int i = (int)(DATACONSIDERED - 1); i > 0; i--){
			if (i != 0){
				usStored[i] = usStored[i-1];
			} else {
				usStored[i] = -1;
			}
		}
	
	}
	
	private float getAverage(){
		float average = 0;
		for (int i = 0; i < DATACONSIDERED; i++){
			average += usStored[i];
		}
		return average/DATACONSIDERED;
	}

}
