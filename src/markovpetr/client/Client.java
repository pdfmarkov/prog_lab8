package markovpetr.client;

import com.markovpetr.command.commands.exceptions.CommandAlreadyExistsException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class Client{
	private CommandReader reader;
	private Receiver receiver;
	private Sender sender;
	private SocketAddress address = new InetSocketAddress("localhost", 8801);

	public static void main(String[] args) {

	}

	public void launch() {
		try {

			DatagramChannel channel = DatagramChannel.open();
			sender = new Sender(channel, address);
			reader = new CommandReader(sender);

			receiver = new Receiver(channel, address);
			receiver.start();

		} catch (IOException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | CommandAlreadyExistsException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void executeCommand(String line){
		receiver.setReady(false);
		reader.readFXMLCommand(line);
	}

	public boolean isReceiverReady() {
		return receiver.getReady();
	}

	public Receiver getReceiver() { return receiver; }
}