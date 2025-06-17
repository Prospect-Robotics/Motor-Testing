package com.team2813;

public class NotEnoughVelocityException extends RuntimeException {
	private final double expected;
	private final double actual;
	public NotEnoughVelocityException(double expected, double actual) {
		super(String.format("Expected a velocity of %.2f, but was %.2f", expected, actual));
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
