package markovpetr.client;

import markovpetr.answers.Answer;
import markovpetr.graphic.MainController;
import markovpetr.main.Main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


public class Receiver extends Thread {

	private DatagramChannel channel;
	private SocketAddress serverAddress;
	//private MainController mainController;
	private String line;
	private boolean ready;

	private ByteBuffer buffer;
	public Receiver(DatagramChannel channel, SocketAddress serverAddress) {
		this.channel = channel;
		this.serverAddress = serverAddress;
		//this.mainController = mainController;
		this.buffer = ByteBuffer.allocate(16384);
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			//synchronized (Main.answerLine) {
				try {
					buffer.clear();
					channel.connect(serverAddress);
					channel.receive(buffer);
					buffer.flip();

					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
					ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
					Answer answer = (Answer) objectInputStream.readObject();

					if (answer.getAnswer().equals("BigData")) {
						System.out.println("Слишком большой объем данных. Ожидаемое количество пакетов:");
						buffer.clear();
						channel.receive(buffer);
						buffer.flip();
						byteArrayInputStream = new ByteArrayInputStream(buffer.array());
						objectInputStream = new ObjectInputStream(byteArrayInputStream);
						Answer countAnswer = (Answer) objectInputStream.readObject();
						countAnswer.printAnswer();
						String bigAnswer;
						for (int i = 0; i < Integer.parseInt(countAnswer.getAnswer()); i++) {
							buffer.clear();
							channel.receive(buffer);
							buffer.flip();
							byteArrayInputStream = new ByteArrayInputStream(buffer.array());
							objectInputStream = new ObjectInputStream(byteArrayInputStream);
							Answer newAnswer = (Answer) objectInputStream.readObject();
							System.out.print(newAnswer.getAnswer());
						}
					} else {
						//answer.printAnswer();
						Main.answerLine = answer.getAnswer();
						ready = true;
					}

					objectInputStream.close();
					byteArrayInputStream.close();
					buffer.clear();
					channel.disconnect();

				} catch (IllegalStateException | IOException | ClassNotFoundException e) {
					Main.answerLine = "Сервер не доступен";
					ready = true;
//					e.getMessage();
//					e.printStackTrace();
					try { channel.disconnect(); } catch (IOException ex) {
						Main.answerLine = "Не удалось получить ответ";
						ready = true;
//						ex.getMessage();
//						ex.printStackTrace();
					}
				}
			}
		}
	//}

	@Override
	public synchronized void start() {
		this.setDaemon(true);
		super.start();
	}

	public boolean getReady() {
		return this.ready;
	}
	public void setReady(boolean b) {
		this.ready = b;
	}
}
