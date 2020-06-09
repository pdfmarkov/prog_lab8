package markovpetr.graphic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import markovpetr.main.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateController implements Initializable{

    public TextField idField;
    public TextField nameField;
    public TextField coord_xField;
    public TextField coord_yField;
    public TextField heightField;
    public TextField passportField;
    public TextField loc_nameField;
    public TextField loc_xField;
    public TextField loc_yField;
    public TextField userField;
    public ComboBox colorBox;
    public ComboBox countryBox;



    public void upd(ActionEvent actionEvent) {
        String upd = "update "+idField.getText()+"\n"+nameField.getText()+"\n"+coord_xField.getText()+"\n"+coord_yField.getText()+"\n"+
                heightField.getText()+"\n"+passportField.getText()+"\n"+colorBox.getValue().toString()+"\n"+
                countryBox.getValue().toString()+"\n"+loc_xField.getText()+"\n"+loc_yField.getText()+"\n"+ loc_nameField.getText();
        Main.client.executeCommand(upd);
        Main.mainController.buildDataWithProp();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userField.setText(Main.username);
        userField.setEditable(false);

        ObservableList<String> colors = FXCollections.observableArrayList("red", "green", "yellow", "white");
        colorBox.setItems(colors);
        colorBox.setValue("red");

        ObservableList<String> countries = FXCollections.observableArrayList("uk", "italy", "vatican");
        countryBox.setItems(countries);
        countryBox.setValue("uk");

    }
}
