package main.answers;

import main.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class BigDataAnswer extends main.answers.Answer implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
	public BigDataAnswer() { super("BigData"); }

	@Override
	public void logAnswer() {
		logger.info(answer);
	}

	@Override
	public void printAnswer() {
		System.err.println(answer);
	}
}
