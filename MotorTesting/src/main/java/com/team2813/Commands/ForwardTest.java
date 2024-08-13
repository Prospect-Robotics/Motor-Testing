package com.team2813.Commands;

import com.team2813.MotorTest;
import com.team2813.Subsystems.MotorTester;
import com.team2813.lib2813.control.ControlMode;

import edu.wpi.first.wpilibj.Timer;

public class ForwardTest extends MotorTest {
	public ForwardTest(MotorTester motorTester) {
		super(motorTester);
	}
	
	@Override
	public String getTestName() {
		return "Forward Test";
	}

	double start;
	boolean checked;

	@Override
	public void initialize() {
		checked = false;
		start = Timer.getFPGATimestamp();
		motorTester.getMotor().set(ControlMode.DUTY_CYCLE, 1);
	}

	@Override
	public void execute() {
		if (Timer.getFPGATimestamp() - start >= 0.5) {
			checked = true;
			double velocity = motorTester.getMotor().getVelocity();
			if (velocity < -1) {
				throw new RuntimeException("Running in wrong direction");
			}
			if (velocity < 50) {
				throw new RuntimeException("Expected at least 50rps, was ");
			}
		}
	}

	class NotEnoughVelocityException extends RuntimeException {
		private final double expected;
		private final double actual;
		NotEnoughVelocityException(double expected, double actual) {
			super(String.format("Expected a velocity of %.2d, but was %.2d"));
			this.expected = expected;
			this.actual = actual;
		}

		public double getExpectedVelocity() {
			return expected;
		}

		public double getActualVelocity() {
			return actual;
		}
	}

	@Override
	public boolean isFinished() {
		return checked;
	}

	@Override
	public void end(boolean interrupted) {
		motorTester.getMotor().set(ControlMode.DUTY_CYCLE, 0);
	}
}
