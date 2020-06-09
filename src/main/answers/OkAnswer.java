package main.answers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OkAnswer extends Answer implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(Answer.class);

	public OkAnswer(String answer) {
		super(answer);
	}

	@Override
	public void logAnswer() {
		logger.info(answer);
	}

	@Override
	public void printAnswer() {
		System.out.println(answer);
	}
}
