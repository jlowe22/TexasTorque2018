package org.texastorque;

import org.texastorque.subsystems.Drivebase;


import java.util.ArrayList;

import org.texastorque.feedback.Feedback;
import org.texastorque.auto.AutoManager;
import org.texastorque.io.HumanInput;
import org.texastorque.io.Input;
import org.texastorque.io.RobotOutput;

import org.texastorque.subsystems.Subsystem;
import org.texastorque.subsystems.Drivebase.DriveType;
import org.texastorque.torquelib.base.TorqueIterative;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TorqueIterative {

	ArrayList<Subsystem> subsystems;
	
	@Override
	public void robotInit() {
		SmartDashboard.putNumber("AUTOMODE", 0);
		Input.getInstance();
		HumanInput.getInstance();
		RobotOutput.getInstance();
		Feedback.getInstance();
		
		subsystems = new ArrayList<Subsystem>();
		subsystems.add(Drivebase.getInstance());
		
		AutoManager.init();
	}
	
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
	
	}
	
	@Override
	public void autonomousInit() {
	
		Drivebase.getInstance().setType(DriveType.AUTODRIVE);
		
		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
	}
	
	@Override
	public void disabledContinuous() {
		
	}
	
	public void alwaysContinuous() {
		Drivebase.getInstance().smartDashboard();
	}
	
	@Override
	public void autonomousContinuous(){
		Feedback.getInstance().update();
		Drivebase.getInstance().autoContinuous();
	}
	
	@Override
	public void teleopInit() {
		Drivebase.getInstance().setType(DriveType.TELEOP);
		for(Subsystem system : subsystems) {
			system.teleopInit();
			system.setInput(HumanInput.getInstance());
			
		}
	}

	@Override
	public void teleopContinuous(){
		HumanInput.getInstance().update();
		for(Subsystem s: subsystems)
			s.teleopContinuous();
		Drivebase.getInstance().teleopContinuous();
	}
}
