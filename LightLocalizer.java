 package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	private int ROTATION_SPEED = 80;
	private static int ACCELERATION = 400;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private SampleProvider usSensor;
	private float[] usData;
	private double angleA, angleB, angleC, angleD;
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

		
		while(colorData[0] > 0.20){
			this.colorSensor.fetchSample(colorData, 0);
			leftMotor.backward();
			rightMotor.backward();
		}
		leftMotor.stop(true);
		rightMotor.stop(true);
		Sound.twoBeeps();
		odo.setPosition(new double [] {-Math.cos(45)*12.1, -Math.sin(45)*12.1, 0}, new boolean [] {true, true, false});
		
		nav.travelTo(0.0,0.0);
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		
		
	}
		private float getFilteredData() {
			usSensor.fetchSample(usData, 0);
			return usData[0]*100;
		}

}
