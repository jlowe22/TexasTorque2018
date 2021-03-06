package org.texastorque.auto;

import org.texastorque.feedback.Feedback;
import org.texastorque.io.Input;
import org.texastorque.io.RobotOutput;
import org.texastorque.subsystems.Drivebase;

public abstract class AutoCommand {
	protected RobotOutput output;
	protected Input input;
	protected Feedback feedback;
	protected boolean pause;

	protected Drivebase drivebase;

	public AutoCommand() {
		init();
	}

	public AutoCommand(boolean pause) {
		this.pause = pause;
		init();
	}

	public void init() {
		output = RobotOutput.getInstance();
		input = Input.getInstance();
		feedback = Feedback.getInstance();

		drivebase = Drivebase.getInstance();
	}

	public abstract void run();

	public abstract void reset();
}
