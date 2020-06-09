package markovpetr.graphic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import markovpetr.main.Main;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ShowController implements Initializable{

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
    public Button readyButton;
    public TextField dateField;


    public void show(ActionEvent actionEvent) {
        Stage stage = (Stage) readyButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        idField.setEditable(false);
        idField.setText(String.valueOf(Main.person.getId()));
        userField.setEditable(false);
        userField.setText(Main.person.getOwner().getLogin());
        nameField.setEditable(false);
        nameField.setText(Main.person.getName());
        coord_xField.setEditable(false);
        coord_xField.setText(String.valueOf(Main.person.getCoordinates().getX()));
        coord_yField.setEditable(false);
        coord_yField.setText(String.valueOf(Main.person.getCoordinates().getY()));
        heightField.setEditable(false);
        heightField.setText(String.valueOf(Main.person.getHeight()));
        passportField.setEditable(false);
        passportField.setText(Main.person.getPassportID());
        loc_nameField.setEditable(false);
        loc_nameField.setText(Main.person.getLocation().getName());
        loc_xField.setEditable(false);
        loc_xField.setText(String.valueOf(Main.person.getLocation().getX()));
        loc_yField.setEditable(false);
        loc_yField.setText(String.valueOf(Main.person.getLocation().getY()));
        colorBox.setEditable(false);
        colorBox.setValue(Main.person.getHairColor().toString());
        countryBox.setEditable(false);
        countryBox.setValue(Main.person.getNationality().toString());
        dateField.setEditable(false);
        String str = LocalTime.parse(Main.person.getCreationDate().toLocalTime().toString()) +" "+ LocalDate.parse(Main.person.getCreationDate().toLocalDate().toString());
        dateField.setText(str);


    }
}
