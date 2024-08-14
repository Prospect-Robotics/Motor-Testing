package com.team2813.Commands;

import com.team2813.MotorTest;
import com.team2813.NotEnoughVelocityException;
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
				throw new NotEnoughVelocityException(50, velocity);
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
