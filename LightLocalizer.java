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
	private double[] angles;
	private Navigation nav;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData){ //, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, SampleProvider usSensor, float[] usData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		angles = new double[4];

		this.nav = new Navigation(odo);
	}
	
	public void doLocalization() {

		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		
		nav.turnTo(225, false);
		leftMotor.stop(true);
		rightMotor.stop(true);
		this.colorSensor.fetchSample(colorData, 0);
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		/*GO BACKward until it sees a black line*/
		
		while(colorData[0] > 0.25){
			leftMotor.backward();
			rightMotor.backward();
			this.colorSensor.fetchSample(colorData, 0);

		}
		leftMotor.stop(true);
		rightMotor.stop(true);
		Sound.twoBeeps();
		odo.setPosition(new double [] {-Math.cos(45)*lightSensorDistance, -Math.sin(45)*lightSensorDistance, 0}, new boolean [] {true, true, false});
		/*GO ABOVE THE CROSS*/
		nav.travelTo(0.0,0.0);
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		/*(NOW WE ARE ABOVE THE CROSS)*/
		int angleIndex = 0;
		
		leftMotor.setSpeed(120);
		rightMotor.setSpeed(120);
		leftMotor.backward();
		rightMotor.forward();
		
		/*(GET 4 READINGS)*/
		while(angleIndex < 4){
			this.colorSensor.fetchSample(colorData, 0);

			if( colorData[0] < 0.25){
				angles[angleIndex] = odo.getAng();
				angleIndex++;
				Sound.beep();
				

			}
		}
		
		leftMotor.stop(true);
		rightMotor.stop(true);
	
		double deltaY = angles[3] - angles[1];
		double deltaX = angles[2] - angles[0];
		double newX = (-1)*lightSensorDistance*Math.cos(Math.PI*deltaX/(2*180));
		double newY = (-1)*lightSensorDistance*Math.cos(Math.PI*deltaY/(2*180));
		nav.turnTo(0, true);
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		odo.setPosition(new double [] {newX, newY, 0}, new boolean [] {true, true, true});
		nav.travelTo(0,0);
		leftMotor.stop(true);
		rightMotor.stop(true);
		nav.turnTo(0, true);
		leftMotor.stop(true);
		rightMotor.stop(true);
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});

	}
}
