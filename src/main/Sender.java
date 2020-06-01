package main;

import main.answers.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Sender {
	private DatagramChannel channel;
	private SocketAddress serverAddress;
	private ByteBuffer buffer = ByteBuffer.allocate(16384);


	public Sender(DatagramChannel channel, SocketAddress serverAddress) {
		this.channel = channel;
		this.serverAddress = serverAddress;
	}

	public void send(main.answers.Request request) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(request);
			buffer.put(byteArrayOutputStream.toByteArray());
			objectOutputStream.flush();
			byteArrayOutputStream.flush();
			buffer.flip();
			for(int i=0; i<10; i++) {
				channel.send(buffer, serverAddress);
			}
			objectOutputStream.close();
			byteArrayOutputStream.close();
			buffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
