package org.texastorque.auto.drive;

import org.texastorque.auto.AutoManager;
import org.texastorque.auto.AutoCommand;

import org.texastorque.feedback.Feedback;
import org.texastorque.subsystems.Drivebase.DriveType;

public class Turn extends AutoCommand {
	
	private final double T_CONSTANT = .05;
	private final double D_PRECISION = .125;
	
	private double angle;
	private double precision;
	private double time = -999;
	
	public Turn(double angle, double precision, double time) {
		this.angle = angle;
		this.precision = precision;
		this.time = time;
	}
	
	public Turn(double angle, double precision) {
		this.angle = angle;
		this.precision = precision;
	}
	
	public Turn(double angle) {
		this.angle = angle;
		precision = D_PRECISION;
	}
	
	@Override
	public void run() {
		Feedback.getInstance().resetDB_gyro();
		Feedback.getInstance().resetDB_encoders();
		
		input.setDB_turnSetpoint(angle, precision);
		drivebase.setType(DriveType.AUTOTURN);
		if(time != -999)
			AutoManager.pause(time);
		else
			AutoManager.pause(angle * T_CONSTANT);
	}

	@Override
	public void reset() {
		angle = 0;
	}
	
}