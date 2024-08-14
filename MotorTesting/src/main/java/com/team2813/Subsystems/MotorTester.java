package com.team2813.Subsystems;

import com.team2813.IdGetter;
import com.team2813.IdGetter.DeviceNotFoundException;
import com.team2813.lib2813.control.Encoder;
import com.team2813.lib2813.control.InvertType;
import com.team2813.lib2813.control.PIDMotor;
import com.team2813.lib2813.control.encoders.CancoderWrapper;
import com.team2813.lib2813.control.motors.TalonFXWrapper;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorTester extends SubsystemBase {

	private PIDMotor motor;
	private Encoder encoder = new CancoderWrapper(1);
	private int id;

	public void findMotor() {
		try {
			IdGetter getter = new IdGetter();
			id = getter.getId();
		} catch (DeviceNotFoundException e) {
			DriverStation.reportError(e.getMessage(), false);
			id = 0;
		}
		motor = new TalonFXWrapper(id, InvertType.CLOCKWISE);
	}

	public PIDMotor getMotor() {
		return motor;
	}

	public Encoder getEncoder() {
		return encoder;
	}

	private DoublePublisher velocity = NetworkTableInstance.getDefault().getDoubleTopic("motor_velocity").publish();

	@Override
	public void periodic() {
		velocity.accept(motor.getVelocity());
	}
}
