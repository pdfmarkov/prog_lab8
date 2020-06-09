package markovpetr.main;

import com.markovpetr.command.entity.Person;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import markovpetr.client.Client;
import markovpetr.graphic.MainController;
import markovpetr.graphic.UserController;


import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;


public class Main extends Application {

    public static String answerLine="";
    public static Client client = new Client();
    public static String username;
    public static ResourceBundle resourceBundle;
    public static MainController mainController;
    public static Person person;
    public static Integer id;


    @Override
    public void start(Stage primaryStage){
        try {
            Main.client.launch();
            Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_ru", Locale.forLanguageTag("ru"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../graphic/user.fxml"), Main.resourceBundle);
            Parent root = loader.load();
            primaryStage.setTitle("Авторизация");
            primaryStage.setScene(new Scene(root, 640, 480));
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();
            primaryStage.setOnCloseRequest(t -> System.exit(0));

            primaryStage.show();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }


}
