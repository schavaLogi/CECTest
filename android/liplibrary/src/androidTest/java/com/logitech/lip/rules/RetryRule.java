package com.logitech.lip.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * RetryRule Rule is used to run test cases upon failure only UseCase: Flaky
 * Test cases
 * 
 * Usage :
 * 
 * @Rule RetryRule rule = new RetryRule();
 * 
 * @Test
 * @Retry(count=2) public void testMethod()
 */
public class RetryRule implements TestRule {

	private static class RetryStatement extends Statement {

		private final int retryCount;
		private final Statement statement;
		private final Description description;

		private RetryStatement(int count, Statement statement, Description description) {
			this.retryCount = count;
			this.statement = statement;
			this.description = description;
		}

		@Override
		public void evaluate() throws Throwable {
			Throwable caughtThrowable = null;
			for (int i = 0; i <= retryCount; i++) {
				try {
					statement.evaluate();
					return; // Upon success
				} catch (Throwable t) {
					caughtThrowable = t;
					System.err.println("Test case failed reason =" + description.getDisplayName() + ": run " + (i + 1)
							+ " failed");
				}
			}
			System.err.println(description.getDisplayName() + ": giving up after " + retryCount + " failures");
			throw caughtThrowable;
		}
	}

	@Override
	public Statement apply(Statement statement, Description description) {
		Statement result = statement;
		Retry repeat = description.getAnnotation(Retry.class);
		if (repeat != null) {
			int count = repeat.count();
			result = new RetryStatement(count, statement, description);
		}
		return result;
	}
}
