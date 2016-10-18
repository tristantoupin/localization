package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class WidthTest {
	public static final int SPEED = 150, ACCELERATION = 200, LAPS = 3;
	public static EV3LargeRegulatedMotor lMotor, rMotor;
	private static Odometer odometer;
	
	//contructor
	public WidthTest(Odometer odo){
		WidthTest.odometer = odo;
		EV3LargeRegulatedMotor[] motors = WidthTest.odometer.getMotors();
		WidthTest.lMotor = motors[0];
		WidthTest.rMotor = motors[1];

		// set acceleration
		WidthTest.lMotor.setAcceleration(ACCELERATION);
		WidthTest.rMotor.setAcceleration(ACCELERATION);
	}
	
	public static double convertTilesToDistance(int TILESFORWARD){
		return (double)TILESFORWARD*30.48;
	}

	public void drive() {
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { lMotor, rMotor }){
			motor.stop();
			motor.setAcceleration(200);
		}
		WidthTest.lMotor.setSpeed(SPEED);
		WidthTest.rMotor.setSpeed(SPEED);
		
		WidthTest.lMotor.rotate(convertAngle(odometer.getWheelRadius(), odometer.getWidth(), convertLaps(LAPS)),true);
		WidthTest.rMotor.rotate(-convertAngle(odometer.getWheelRadius(), odometer.getWidth(), convertLaps(LAPS)),false);
		
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * convertLaps(LAPS) / 360.0);
	}
	
	private static double convertLaps(int laps){
		return (laps * 360.0);
	}
	
}
