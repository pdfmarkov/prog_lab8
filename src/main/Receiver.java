package main;

import main.answers.Answer;

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

	private ByteBuffer buffer;
	public Receiver(DatagramChannel channel, SocketAddress serverAddress) {
		this.channel = channel;
		this.serverAddress = serverAddress;
		this.buffer = ByteBuffer.allocate(16384);
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				buffer.clear();
				channel.connect(serverAddress);
				channel.receive(buffer);
				buffer.flip();

				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
				ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
				main.answers.Answer answer = (main.answers.Answer) objectInputStream.readObject();

				if (answer.getAnswer().equals("BigData")){
					System.out.println("Слишком большой объем данных. Ожидаемое количество пакетов:");
						buffer.clear();
						channel.receive(buffer);
						buffer.flip();
						byteArrayInputStream = new ByteArrayInputStream(buffer.array());
						objectInputStream = new ObjectInputStream(byteArrayInputStream);
						main.answers.Answer countAnswer = (main.answers.Answer) objectInputStream.readObject();
						countAnswer.printAnswer();
						String bigAnswer ="";
					for( int i=0;i<Integer.parseInt(countAnswer.getAnswer()); i++){
						buffer.clear();
						channel.receive(buffer);
						buffer.flip();
						byteArrayInputStream = new ByteArrayInputStream(buffer.array());
						objectInputStream = new ObjectInputStream(byteArrayInputStream);
						main.answers.Answer newAnswer = (main.answers.Answer) objectInputStream.readObject();
						System.out.print(newAnswer.getAnswer());
					}
				} else { answer.printAnswer(); }

				objectInputStream.close();
				byteArrayInputStream.close();
				buffer.clear();
				channel.disconnect();
			} catch (PortUnreachableException | IllegalStateException e) {
				System.out.println("Сервер не доступен");
				e.getMessage();
				e.printStackTrace();
				try {
					channel.disconnect();
				} catch (IOException ex) {
//					System.out.println("Сервер не доступен");
				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public synchronized void start() {
		this.setDaemon(true);
		super.start();
	}


}
