package markovpetr.client;

import com.markovpetr.command.commands.Command;
import com.markovpetr.command.commands.CommandManager;
import com.markovpetr.command.commands.exceptions.CommandAlreadyExistsException;
import com.markovpetr.command.commands.exceptions.NotFoundCommandException;
import com.markovpetr.command.commands.specific.*;
import com.markovpetr.command.entity.Location;
import com.markovpetr.command.entity.Person;
import com.markovpetr.command.entity.User;
import markovpetr.answers.Request;
import markovpetr.main.Main;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;


public class CommandReader {
	private Sender sender;
	private User user;
	private File tmp = new File("tmp");
	private FileWriter tmpWriter = new FileWriter(tmp, false);


	public CommandReader(Sender sender) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, CommandAlreadyExistsException, ClassNotFoundException, IOException {
		this.sender = sender;

		CommandManager manager = CommandManager.getInstance();
		//System.out.println(manager);
		manager.initCommand(AddCommand.class, "add", "Добавляет элемент в коллекцию", Person.class);
		//manager.initCommand(AddIfMinCommand.class, "add_if_min", "Добавляет элемент в коллекцию, если его " +
		//"значение меньше, чем у минимального в коллекции", Person.class);
		manager.initCommand(ClearCommand.class, "clear", "Очищает коллекцию");
		manager.initCommand(CountGreaterThanLocationCommand.class, "count_greater_than_location",
				"Выводит количество элементов, значение поля location которых больше заданного", Location.class);
		manager.initCommand(ExecuteScriptCommand.class, "execute_script",
				"Считывает и испольняет скрипт из файла", String.class);
		manager.initCommand(HelpCommand.class, "help", "Выводит справку по коммандам");
		manager.initCommand(InfoCommand.class, "info", "Выводит информацию о коллекции");
		manager.initCommand(MaxByCoordinatesCommand.class, "max_by_coordinates",
				"Выводи элемент коллекции, у которого значение поля coordinates является максимальным");
		manager.initCommand(MinByIdCommand.class, "min_by_id",
				"Выводит элемент коллекции, индетификатор которого минимален");
		manager.initCommand(RemoveByIdCommand.class, "remove_by_id",
				"Удаляет элемент с заданным id", String.class);
		manager.initCommand(RemoveGreaterCommand.class, "remove_greater",
				"Удаляет из коллекции все элементы, превыщающий заданный", Person.class);
		manager.initCommand(RemoveHeadCommand.class, "remove_head", "Выводит и удаляет первый элемент коллекции");
		manager.initCommand(ShowCommand.class, "show", "Выводит все элементы коллекции");
		manager.initCommand(UpdateCommand.class, "update", "Обновляет значение элемента id",
				String.class, Person.class);
		manager.initCommand(AuthCommand.class, "auth", "Авторизует пользователя", User.class);
		manager.initCommand(RegisterCommand.class, "register", "Региструет пользователя", User.class);

	}

	public void read() {

		Scanner scanner = new Scanner(System.in);

		while(true) {
			try {
				//System.out.println("Введите команду: ");
				String line = scanner.nextLine().trim();
				readCommand(line, scanner);
			} catch (NotFoundCommandException | IllegalArgumentException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void readFXMLCommand(String arg) {
			try {
				Scanner fxmlScanner = new Scanner(arg);
				while (fxmlScanner.hasNextLine()) {
					String line = fxmlScanner.nextLine().trim();
					readCommand(line, fxmlScanner);
				}
				fxmlScanner.close();
			} catch (NotFoundCommandException e) {
				e.printStackTrace();
			}
	}


	private void readCommand(String line, Scanner scanner) throws NotFoundCommandException{
		String name = CommandManager.parseName(line);

		if (name.equals("exit")) {
			System.exit(0);
		} else if (name.equals("execute_script")) {
			Object[] args = CommandManager.parseArgs(line);
			File file = new File("C:\\Users\\chann\\Desktop\\lab7\\CoolerClient\\src\\markovpetr\\resources\\" + args[0]);
			if (!file.exists())
				Main.answerLine = ("Скрипта не существует");
			else if (file.exists() && !file.canRead())
				Main.answerLine = ("Скрипт невозможно прочитать, проверьте права файла(права на чтение)");
			else if (file.exists() && !file.canExecute())
				Main.answerLine = ("Скрипт невозможно выполнить, проверьте права файла (права на выполнение)");
			else {
				try {
					Scanner fileScanner = new Scanner(file);
					while (fileScanner.hasNextLine()) {
						String fileLine = fileScanner.nextLine().trim();
						readCommand(fileLine, fileScanner);
					}
					fileScanner.close();
				} catch (FileNotFoundException e) {
					Main.answerLine =("Скрипта не существует");
				}
			}
		} else if(name.equals("auth")) {
			Object[] args = CommandManager.parseArgs(line);
			Command command = CommandManager.getCommand(name);
			Object[] fillableArg = CommandManager.getFillableArgs(command, scanner);
			args = CommandManager.concatArgs(args, fillableArg);
			CommandManager.validate(command, args);
			user = (User) args[0];
			sender.send(new Request(user, command, args));
		} else {
			Object[] args = CommandManager.parseArgs(line);
			Command command = CommandManager.getCommand(name);
			Object[] fillableArg = CommandManager.getFillableArgs(command, scanner);
			args = CommandManager.concatArgs(args, fillableArg);

			CommandManager.validate(command, args);
			sender.send(new Request(user, command, args));
//				System.out.println("Команда успешна провалидирована");
		}
	}
}
