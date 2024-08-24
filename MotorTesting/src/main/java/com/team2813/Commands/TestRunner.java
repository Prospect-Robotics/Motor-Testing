package com.team2813.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.team2813.MotorTest;
import com.team2813.Subsystems.MotorTester;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

public class TestRunner extends Command {
	public Queue<MotorTest> tests = new ArrayDeque<>();
	private int currentTest = 0;
	private final MotorTester motorTester;
	public static final String PACKAGE_NAME = "com.team2813.Commands";
	public TestRunner() {
		motorTester = new MotorTester();
		motorTester.findMotor();
		for (Class<? extends MotorTest> test : getAllClasses(PACKAGE_NAME)) {
			try {
				Constructor<? extends MotorTest> constructor = test.getConstructor(MotorTester.class);
				//DriverStation.reportWarning(String.format("Found class: %s", test.getCanonicalName()), false);
				tests.add(constructor.newInstance(motorTester));
			} catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
				// If no constructor exists, don't add anything (eat the exception)
				// or, cannot access the constructor, so we can't add it (eat the exception)
				// or, class is abstract, so we cannot add it. also eat the exception :3
			} catch (InvocationTargetException e) {
				// the constructor threw an exception, we shouldn't eat this exception
				throw new RuntimeException("A test constructor threw an exception", e);
			}
		}
	}

	@Override
	public void initialize() {
		motorTester.findMotor();
		MotorTest nextTest = tests.peek();
		if (nextTest != null) {
			nextTest.initialize();
		}
	}

	@Override
	public void execute() {
		MotorTest test = tests.peek();
		if (test == null || test.isFinished()) {
			test = advance(true);
		}
		if (test == null) {
			return;
		}
		try {
			test.execute();
		} catch (Exception e) {
			DriverStation.reportError(String.format("[%s] Failed With Exception: %s", test.getTestName(),  e.getMessage()), e.getStackTrace());
			advance(false);
		}
	}

	private MotorTest advance(boolean success) {
		MotorTest curr = tests.poll();
		if (curr == null) {
			return null;
		}
		if (success) {
			DriverStation.reportWarning(String.format("[%s] Succedded", curr.getTestName()), false);
		}
		
		curr.end(!success);
		curr = tests.peek();
		if (curr != null) {
			curr.initialize();
		}
		return curr;
	}

	@Override
	public void end(boolean interrupted) {
		if (interrupted) {
			while (!tests.isEmpty()) {
				tests.remove().end(interrupted);
			}
		}
	}

	@Override
	public boolean isFinished() {
		return currentTest >= tests.size();
	}

	public static Collection<Class<? extends MotorTest>> getAllClasses(String packageName) {
		try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			return bufferedReader.lines()
				.filter(line -> line.endsWith(".class"))
				.flatMap(line -> getClasses(line, packageName))
				.collect(Collectors.toSet());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Stream<Class<? extends MotorTest>> getClasses(String className, String packageName) {
		try {
            Class<?> rawClass =  Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
			if (MotorTest.class.isAssignableFrom(rawClass)) {
				// guaranteed to succed, as rawClass extends MotorTest (isAssignableFrom test)
				@SuppressWarnings("unchecked")
				Class<? extends MotorTest> castedClass = (Class<? extends MotorTest>) rawClass;
				return Stream.of(castedClass);
			}
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return Stream.empty();
	}
}
