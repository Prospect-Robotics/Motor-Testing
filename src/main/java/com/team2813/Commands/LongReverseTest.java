package com.team2813.Commands;

import com.team2813.MotorTest;
import com.team2813.NotEnoughVelocityException;
import com.team2813.TooHotException;
import com.team2813.Subsystems.MotorTester;
import com.team2813.lib2813.control.ControlMode;

import edu.wpi.first.wpilibj.Timer;

public class LongReverseTest extends MotorTest {
	public LongReverseTest(MotorTester motorTester) {
		super(motorTester);
	}
	
	@Override
	public String getTestName() {
		return "Long Reverse Test";
	}

	double start;
	boolean checked;

	@Override
	public void initialize() {
		checked = false;
		start = Timer.getFPGATimestamp();
		motorTester.getMotor().set(ControlMode.DUTY_CYCLE, -1);
	}

	@Override
	public void execute() {
		if (Timer.getFPGATimestamp() - start >= 10) {
			checked = true;
			double velocity = motorTester.getMotor().getVelocity();
			if (velocity > 1) {
				throw new RuntimeException("Running in wrong direction");
			}
			if (velocity > -50) {
				throw new NotEnoughVelocityException(-50, velocity);
			}
			double temp = motorTester.getTemp().orElseThrow(AssertionError::new);
			if (temp > 50) {
				throw new TooHotException(50, temp);
			}
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
