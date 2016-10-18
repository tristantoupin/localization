package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class WheelRadiusTest {
	public static final int SPEED = 150, ACCELERATION = 200, TILESFORWARD = 7;
	public static EV3LargeRegulatedMotor lMotor, rMotor;
	private static Odometer odometer;
	
	//contructor
	public WheelRadiusTest(Odometer odo){
		WheelRadiusTest.odometer = odo;
		EV3LargeRegulatedMotor[] motors = WheelRadiusTest.odometer.getMotors();
		WheelRadiusTest.lMotor = motors[0];
		WheelRadiusTest.rMotor = motors[1];

		// set acceleration
		WheelRadiusTest.lMotor.setAcceleration(ACCELERATION);
		WheelRadiusTest.rMotor.setAcceleration(ACCELERATION);
	}
	
	public static double convertTilesToDistance(int TILESFORWARD){
		return (double)TILESFORWARD*30.48;
	}

	public void drive() {
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { lMotor, rMotor }){
			motor.stop();
			motor.setAcceleration(200);
		}
		WheelRadiusTest.lMotor.setSpeed(SPEED);
		WheelRadiusTest.rMotor.setSpeed(SPEED);
		
		WheelRadiusTest.lMotor.rotate(convertDistance(odometer.getWheelRadius(), convertTilesToDistance(TILESFORWARD)),true);
		WheelRadiusTest.rMotor.rotate(convertDistance(odometer.getWheelRadius(), convertTilesToDistance(TILESFORWARD)),false);
		
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
}
