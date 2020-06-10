package markovpetr.graphic;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import markovpetr.client.Client;
import markovpetr.main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class UserController extends Thread implements Initializable {

        @FXML private Button authButton;
        @FXML private TextField loginField;
        @FXML private PasswordField passwordField;
        @FXML private Button registerButton;



        @Override
        public void initialize(URL location, ResourceBundle resources) {
                setDaemon(true);

        }

        public void auth(ActionEvent actionEvent) {

                if (!loginField.getText().equals("") || !passwordField.getText().equals("")) {
                        Main.client.executeCommand("auth\n" + loginField.getText() + "\n" + passwordField.getText());
                        waitForAnswer();
                        if (!Main.answerLine.equals("Пользователь с таким логином или паролем не найден") &&
                                !Main.answerLine.equals("Сервер не доступен") &&
                                !Main.answerLine.equals("Не удалось получить ответ")) {
                                Main.username = loginField.getText();
                                authButton.getScene().getWindow().hide();
                                FXMLLoader loader= new FXMLLoader(getClass().getResource("main.fxml"),Main.resourceBundle);
                                Parent root;
                                try{
                                        root = loader.load();
                                        Stage stage = new Stage();
                                        stage.setTitle("Работа с БД");
                                        stage.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
                                        stage.setScene(new Scene(root));
                                        stage.setResizable(false);
                                        stage.sizeToScene();
                                        stage.setOnCloseRequest(t -> System.exit(0));
                                        stage.showAndWait();
                                } catch (IOException e){
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.initModality(Modality.APPLICATION_MODAL);
                                        alert.initStyle(StageStyle.UNDECORATED);
                                        alert.setTitle("Error");
                                        alert.setHeaderText("Auth error");
                                        alert.setContentText("Не удалось запустить основное окно");
                                        alert.showAndWait().ifPresent(rs -> {});;
                                }
                        } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.initModality(Modality.APPLICATION_MODAL);
                                alert.initStyle(StageStyle.UNDECORATED);
                                alert.setTitle("Error");
                                alert.setHeaderText("Auth error");
                                alert.setContentText(Main.answerLine);
                                alert.showAndWait().ifPresent(rs -> {});
                        }
                } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initStyle(StageStyle.UNDECORATED);
                        alert.setTitle("Error");
                        alert.setHeaderText("Auth error");
                        alert.setContentText("Заполните все поля!");
                        alert.showAndWait().ifPresent(rs -> {});
                }
        }

        public void register(ActionEvent actionEvent) {
                FXMLLoader loader = new FXMLLoader();
                        if (!loginField.getText().equals("")&&!passwordField.getText().equals("")) {
                                Main.client.executeCommand("register\n" + loginField.getText() + "\n" + passwordField.getText());
                                waitForAnswer();
                                Main.username = loginField.getText();
                                Main.client.executeCommand("auth\n" + loginField.getText() + "\n" + passwordField.getText());
                                waitForAnswer();
                                registerButton.getScene().getWindow().hide();
                                loader.setLocation(getClass().getResource("main.fxml"));
                                Parent root;
                                try{
                                        root = FXMLLoader.load(getClass().getResource("main.fxml"));
                                        Stage stage = new Stage();
                                        stage.setScene(new Scene(root));
                                        stage.showAndWait();
                                } catch (IOException e){
                                        e.printStackTrace();
                                }

                }

        }

        public void rus(ActionEvent actionEvent) {
                Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_ru", Locale.forLanguageTag("ru"));
                Scene scene = authButton.getScene();
                try{
                        scene.setRoot(FXMLLoader.load(getClass().getResource("user.fxml"), Main.resourceBundle));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void serb(ActionEvent actionEvent) {
                Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_sr", Locale.forLanguageTag("sr"));
                Scene scene = authButton.getScene();
                try{
                        scene.setRoot(FXMLLoader.load(getClass().getResource("user.fxml"), Main.resourceBundle));
                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        public void bolg(ActionEvent actionEvent) {
                Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_bl", Locale.forLanguageTag("bl"));
                Scene scene = authButton.getScene();
                try{
                        scene.setRoot(FXMLLoader.load(getClass().getResource("user.fxml"), Main.resourceBundle));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void esp(ActionEvent actionEvent) {
                Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_es", Locale.forLanguageTag("es"));
                Scene scene = authButton.getScene();
                try{
                        scene.setRoot(FXMLLoader.load(getClass().getResource("user.fxml"), Main.resourceBundle));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void eng(ActionEvent actionEvent) {
                Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_en", Locale.forLanguageTag("en"));
                Scene scene = authButton.getScene();
                try{
                        scene.setRoot(FXMLLoader.load(getClass().getResource("user.fxml"), Main.resourceBundle));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void  waitForAnswer(){
                try { this.sleep(150); } catch (InterruptedException e) { e.printStackTrace(); }
        }
}
