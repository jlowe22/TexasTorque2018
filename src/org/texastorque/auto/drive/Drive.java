package org.texastorque.auto.drive;

import org.texastorque.auto.AutoManager;
import org.texastorque.auto.AutoCommand;

import org.texastorque.feedback.Feedback;
import org.texastorque.subsystems.Drivebase.DriveType;

public class Drive extends AutoCommand {
	
	private final double D_CONSTANT = 0.0744;
	private final double D_PRECISION = 0.125;
	
	private double distance;
	private double precision;
	private double time = -999;
	
	public Drive(double distance, double precision, double time) {
		this.distance = distance;
		this.precision = precision;
		this.time = time;
	}
	
	public Drive(double distance, double precision) {
		this.distance = distance;
		this.precision = precision;
	}
	
	public Drive(double distance) {
		this.distance = distance;
		precision = D_PRECISION;
	}
	
	@Override
	public void run() {
		Feedback.getInstance().resetDB_gyro();
		Feedback.getInstance().resetDB_encoders();
		System.out.println("run");
		input.setDB_driveSetpoint(distance, precision);
		drivebase.setType(DriveType.AUTODRIVE);
		if(time != -999)
			AutoManager.pause(time);
		else
			AutoManager.pause(distance * D_CONSTANT);
	}

	@Override
	public void reset() {
		distance = 0;
	}

}