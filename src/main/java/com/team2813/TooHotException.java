package com.team2813;

public class TooHotException extends RuntimeException {
	private final double expected;
	private final double actual;
	public TooHotException(double expected, double actual) {
		super(String.format("Expected a temperature under %.2f, but was %.2f", expected, actual));
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
