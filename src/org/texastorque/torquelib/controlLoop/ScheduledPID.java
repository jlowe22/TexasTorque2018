package org.texastorque.torquelib.controlLoop;

import java.util.Arrays;

import org.texastorque.util.ArrayUtils;
import org.texastorque.util.Integrator;
import org.texastorque.util.MathUtils;
import org.texastorque.util.TorqueTimer;

import edu.wpi.first.wpilibj.Timer;

public class ScheduledPID {

	private final double[] gainDivisions;
	private final double[] pGains;
	private final double[] iGains;
	private final double[] dGains;
	
	private int currentGainIndex;
	
	private final double setPoint;
	private final double maxOutput;
	private double lastError;
	
	private final Integrator integrator;
	private final TorqueTimer timer;

	private ScheduledPID(double setPoint, double maxOutput, int count) {
		this.gainDivisions = new double[count - 1];
		this.pGains = new double[count];
		this.iGains = new double[count];
		this.dGains = new double[count];
		
		this.setPoint = setPoint;
		this.maxOutput = maxOutput;
		
		this.integrator = new Integrator();
		
		this.timer = new TorqueTimer();
	}
	
	public double calculate(double processVar) {
		handleFirstCalculation();

		double error = processVar - this.setPoint;
		updateGainIndex(error);

		double pGain = this.pGains[this.currentGainIndex];
		double iTerm = integralTerm(error);
		double dTerm = derivativeTerm(error);
		
		double output = pGain * (error + iTerm + dTerm);  // Standard PID form.
		
		this.lastError = error;

		timer.startLap();  // Measure deltaTime starting now.
		
		return MathUtils.clamp(output, -maxOutput, maxOutput);
	}
	
	private double integralTerm(double error) {
		double gain = this.iGains[this.currentGainIndex];
		double dt = timer.lapTime();
		double integral = integrator.calculate(error, dt);
		
		return gain * integral;
	}
	
	private double derivativeTerm(double error) {
		double dt = timer.lapTime();
		
		if (dt == 0) return 0;
		
		double gain = this.dGains[this.currentGainIndex];
		double de = error - this.lastError;
		
		return gain * (de/dt);
		
	}
	
	private void handleFirstCalculation() {
		if (timer.isRunning()) return;
		
		timer.start();
	}
	
	/** Calculates the index for the current gain values.
	 * 
	 * @param error The current error in the process variable.
	 * @return The integer index for the gain values.
	 */
	private int updateGainIndex(double error) {
		if (error <= this.gainDivisions[0]) {
			this.currentGainIndex = 0;
			return this.currentGainIndex;
		}
		
		for (int i = 0; i < gainDivisions.length - 2; i++) {
			double leftBound = this.gainDivisions[i];
			double rightBound = this.gainDivisions[i + 1];
			
			if (leftBound <= error && error < rightBound) {
				this.currentGainIndex = i + 1;
				return this.currentGainIndex;
			}
		}

		this.currentGainIndex = gainDivisions.length;
		return this.currentGainIndex;
	}
	
	@Override
	public String toString() {
		return "ScheduledPID [gainDivisions=" + Arrays.toString(gainDivisions) + ", pGains=" + Arrays.toString(pGains)
				+ ", iGains=" + Arrays.toString(iGains) + ", dGains=" + Arrays.toString(dGains) + ", currentGainIndex="
				+ currentGainIndex + ", setPoint=" + setPoint + ", maxOutput=" + maxOutput + ", lastError=" + lastError
				+ ", integrator=" + integrator + ", timer=" + timer + "]";
	}



	public static class Builder {
		
		private final ScheduledPID pid;
		
		public Builder(double setPoint, double maxOutput, int count) {
			this.pid = new ScheduledPID(setPoint, maxOutput, count);
		}
		
		public Builder setRegions(double... regions) {
			ArrayUtils.bufferAndFill(regions, pid.gainDivisions);
			
			// Divisions should always be specified in ascending order.
			if (!ArrayUtils.isSorted(regions, true)) {
				Arrays.sort(pid.gainDivisions);
				System.out.println("Gain schedule was not ordered correctly.");
			}
			
			return this;
		}
		
		public Builder setPGains(double... pGains) {
			ArrayUtils.bufferAndFill(pGains, pid.pGains);
			return this;
		}
		
		public Builder setIGains(double... iGains) {
			ArrayUtils.bufferAndFill(iGains, pid.iGains);
			return this;
		}
		
		public Builder setDGains(double... dGains) {
			ArrayUtils.bufferAndFill(dGains, pid.dGains);
			return this;
		}
		
		public ScheduledPID build() {
			return this.pid;
		}
	}
}
