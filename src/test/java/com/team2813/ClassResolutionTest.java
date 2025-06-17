package com.team2813;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.team2813.Commands.ForwardTest;
import com.team2813.Commands.LongForwardTest;
import com.team2813.Commands.LongReverseTest;
import com.team2813.Commands.ReverseTest;
import com.team2813.Commands.TestRunner;

public class ClassResolutionTest {
	@Test
	public void EnsureContainsClasses() {
		List<Class<? extends MotorTest>> classes = new ArrayList<>(TestRunner.getAllClasses());
		assertTrue("ForwardTest", classes.contains(ForwardTest.class));
		assertTrue("ReverseTest", classes.contains(ReverseTest.class));
		assertTrue("LongForwardTest", classes.contains(LongForwardTest.class));
		assertTrue("LongReverseTest", classes.contains(LongReverseTest.class));
	}
}
