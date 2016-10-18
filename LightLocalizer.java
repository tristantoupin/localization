 package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	private int ROTATION_SPEED = 80;
	private static final int ACCELERATION = 400;
	private static double lightSensorDistance = 12.1;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private SampleProvider usSensor;
	private float[] usData;
	private double[] angles;
	private Navigation nav;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData){ //, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, SampleProvider usSensor, float[] usData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.usSensor = usSensor;
		this.usData = usData;
		
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		angles = new double[4];

		this.nav = new Navigation(odo);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		nav.turnTo(225, false);
		leftMotor.stop(true);
		rightMotor.stop(true);
		this.colorSensor.fetchSample(colorData, 0);
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);

		
		while(colorData[0] > 0.22){
			leftMotor.backward();
			rightMotor.backward();
			this.colorSensor.fetchSample(colorData, 0);

		}
		leftMotor.stop(true);
		rightMotor.stop(true);
		Sound.twoBeeps();
		odo.setPosition(new double [] {-Math.cos(45)*lightSensorDistance, -Math.sin(45)*lightSensorDistance, 0}, new boolean [] {true, true, false});
		
		nav.travelTo(0.0,0.0);
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		/*(NOW WE ARE ABOVE THE CROSS)*/
		int angleIndex = 0;
		
		leftMotor.setSpeed(120);
		rightMotor.setSpeed(120);
		leftMotor.backward();
		rightMotor.forward();
		while(angleIndex < 4){
			this.colorSensor.fetchSample(colorData, 0);

			if( colorData[0] < 0.22){ //getColorData() - firstBrightness > 10){
				angles[angleIndex] = odo.getAng();
				angleIndex++;
				Sound.beep();

			}
		}
		
		leftMotor.stop(true);
		rightMotor.stop(true);
	
		//0th element = first y line, 1st = first x point, 3rd = second y, 4th = second x
		//calculate the deltas.
		double deltaY = angles[3] - angles[1];
		double deltaX = angles[2] - angles[0];
		// do trig to compute (0,0) and 0 degrees
		double xValue = (-1)*lightSensorDistance*Math.cos(Math.PI*deltaX/(2*180));
		double yValue = (-1)*lightSensorDistance*Math.cos(Math.PI*deltaY/(2*180));
		nav.turnTo(0, true); //navi.turnTo(deltaTheta, true);
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		odo.setPosition(new double [] {xValue, yValue, 0}, new boolean [] {true, true, true});

		//now travel to 0,0 and turn to 0 (we are done!)
		nav.travelTo(0, 0);
		leftMotor.stop(true);
		rightMotor.stop(true);
		nav.turnTo(0, true);
		leftMotor.stop(true);
		rightMotor.stop(true);
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});

		
	}
		private float getColorData() {
			this.colorSensor.fetchSample(colorData, 0);
			return usData[0]*100;
		}

}
