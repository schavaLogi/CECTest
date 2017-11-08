package com.logitech.lip.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Repeat Rule is used to run test cases given number of times either it's
 * success or failure UseCase: Like regression test
 * 
 * @Test
 * @Repeat(count=2) public void testMethod()
 */
public class RepeatRule implements TestRule {

	private static class RepeatStatement extends Statement {

		private final int times;
		private final Statement statement;
		private final Description description;

		private RepeatStatement(int times, Statement statement, Description description) {
			this.times = times;
			this.statement = statement;
			this.description = description;
		}

		@Override
		public void evaluate() throws Throwable {
			for (int i = 0; i < times; i++) {
				try {
					if (times > 1) {
						System.out.println("Test case " + description.getDisplayName() + "Will run " + times + "times");
					}
					statement.evaluate();
				} catch (Throwable t) {
					System.err.println(
							"Test case failed =" + description.getDisplayName() + ": run " + (i + 1) + " failed");
				}
			}
		}
	}

	@Override
	public Statement apply(Statement statement, Description description) {
		Statement result = statement;
		Repeat repeat = description.getAnnotation(Repeat.class);
		if (repeat != null) {
			int times = repeat.count();
			result = new RepeatStatement(times, statement, description);
		}
		return result;
	}
}
