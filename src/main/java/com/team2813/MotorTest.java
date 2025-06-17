package com.team2813;

import java.util.Objects;

import com.team2813.Subsystems.MotorTester;

import edu.wpi.first.wpilibj2.command.Command;

public abstract class MotorTest extends Command {
	protected MotorTester motorTester;
	public MotorTest(MotorTester motorTester) {
		this.motorTester = Objects.requireNonNull(motorTester, "motorTester should not be null");
		addRequirements(motorTester);
	}
	public abstract String getTestName();
}