package markovpetr.graphic;

import com.markovpetr.command.entity.*;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import markovpetr.main.Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.paint.Color;

public class MainController extends Thread implements Initializable {

    final int WINDOW_X = 896;
    final int WINDOW_Y = 502;

    @FXML
    public TextField inputField;
    @FXML
    public TextArea outputField;

    @FXML
    public Button exitButton;
    @FXML
    public Button helpButton;
    @FXML
    public Button addButton;
    @FXML
    public Button minbyidButton;
    @FXML
    public Button maxbycoordinatesButton;
    @FXML
    public Button countButton;
    @FXML
    public Button showButton;
    @FXML
    public Button updateButton;
    @FXML
    public Button removebyidButton;
    @FXML
    public Button removeheadButton;
    @FXML
    public Button removegreaterButton;
    @FXML
    public Button readyButton;
    @FXML
    public Button scriptButton;
    @FXML
    public Button clearButton;

    @FXML
    public TableColumn<Person, Long> idColoumn;
    @FXML
    public TableColumn<Person, String> nameColoumn;
    @FXML
    public TableColumn<Person, Location> locationColoumn;
    @FXML
    public TableColumn<Person, LocalDateTime> creatColoumn;
    @FXML
    public TableColumn<Person, Double> heightColoumn;
    @FXML
    public TableColumn<Person, String> passportColoumn;
    @FXML
    public TableColumn<Person, Color> haircolorColoumn;
    @FXML
    public TableColumn<Person, Country> nationalityColoumn;
    @FXML
    public TableColumn<Person, User> userColoumn;
    @FXML
    public TableColumn<Person, Coordinates> coordColoumn;

    @FXML
    public TextField idField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField heightField;
    @FXML
    public TextField passportField;
    @FXML
    public TextField haircolorField;
    @FXML
    public TextField nationalityField;
    @FXML
    public TextField locationField;
    @FXML
    public TextField userField;
    @FXML
    public TextField creatField;
    @FXML
    public TextField coordField;

    @FXML
    public Canvas canvas = new Canvas(WINDOW_X, WINDOW_Y);
    @FXML
    private Text username;
    @FXML
    private TableView<Person> dbTable;

    private List<Color> colors = new ArrayList<>();

    private String pressReady = " " + Main.resourceBundle.getString("ready_line_text");
    private ObservableList<Person> masterData = FXCollections.observableArrayList();
    private List<Person> persons = new LinkedList<>();
    private List<Integer> users_keys = new ArrayList<>();
    private AnimationTimer timer;
    private Timeline timeline;
    private DoubleProperty opacity;
    private DoubleProperty koef;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.ORANGE);
        colors.add(Color.HOTPINK);
        colors.add(Color.AQUA);
        colors.add(Color.SILVER);
        colors.add(Color.CHOCOLATE);

        setDaemon(true);
        setUsername(Main.username);
        Main.mainController = this;

        idColoumn.setCellValueFactory(new PropertyValueFactory<Person, Long>("id"));
        nameColoumn.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
        coordColoumn.setCellValueFactory(new PropertyValueFactory<Person, Coordinates>("coordinates"));
        creatColoumn.setCellValueFactory(new PropertyValueFactory<Person, LocalDateTime>("creationDate"));
        heightColoumn.setCellValueFactory(new PropertyValueFactory<Person, Double>("height"));
        passportColoumn.setCellValueFactory(new PropertyValueFactory<Person, String>("passportID"));
        haircolorColoumn.setCellValueFactory(new PropertyValueFactory<Person, Color>("hairColor"));
        nationalityColoumn.setCellValueFactory(new PropertyValueFactory<Person, Country>("nationality"));
        locationColoumn.setCellValueFactory(new PropertyValueFactory<Person, Location>("location"));
        userColoumn.setCellValueFactory(new PropertyValueFactory<Person, User>("owner"));

        creatColoumn.setCellFactory(column -> {
            TableCell<Person, LocalDateTime> cell = new TableCell<Person, LocalDateTime>() {
                private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yy");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        Date out = Date.from(item.atZone(ZoneId.systemDefault()).toInstant());
                        setText(format.format(out));
                    }
                }
            };

            return cell;
        });

        buildDataWithProp();


        FilteredList<Person> filteredData = new FilteredList<>(masterData, p -> true);
        createListeners(filteredData);
        SortedList<Person> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(dbTable.comparatorProperty());
        dbTable.setItems(sortedData);

        opacity  = new SimpleDoubleProperty();
        koef  = new SimpleDoubleProperty();

        timeline = new Timeline(

                new KeyFrame(Duration.seconds(0),
                        new KeyValue(opacity, 0),
                        new KeyValue(koef, 0.3)
                ),
                new KeyFrame(Duration.seconds(3),
                        new KeyValue(opacity, 1),
                        new KeyValue(koef, 1.0)
                )

        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //buildDataWithProp();
                draw(opacity, koef);
            }
        };

        timer.start();
        timeline.play();

    }

    public void setUsername(String line) {
        username.setText(line);
    }

    /******************
     *    BUTTONS     *
     *******************/



    public void update(ActionEvent actionEvent) {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("update.fxml"),Main.resourceBundle);
        Parent root2;
        try {
            root2 = loader.load();
            Stage stage2 = new Stage();
            stage2.setTitle("Update");
            stage2.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
            stage2.setScene(new Scene(root2));
            stage2.setResizable(false);
            stage2.sizeToScene();
            stage2.showAndWait();

            stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    buildDataWithProp();
                }
            });

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error");
            alert.setHeaderText("Person error");
            alert.setContentText("Can't open update window");
        }
    }

    public void remove(ActionEvent actionEvent) {
        inputField.clear();
        AtomicReference<String> remove_by_id = new AtomicReference<>("remove_by_id ");
        outputField.setText("Введите id" + pressReady);
        readyButton.setOnAction(event1 -> {
            remove_by_id.set(remove_by_id + inputField.getText());
            inputField.clear();
            Main.client.executeCommand(String.valueOf(remove_by_id));
            waitForAnswer();
            outputField.setText(Main.answerLine);
            buildDataWithProp();
        });
    }

    public void removehead(ActionEvent actionEvent) {
        Main.client.executeCommand("remove_head");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void removegreater(ActionEvent actionEvent) {
        FXMLLoader loader= new FXMLLoader(getClass().getResource("removegreater.fxml"),Main.resourceBundle);
        Parent root2;
        try {
            root2 = loader.load();
            Stage stage2 = new Stage();
            stage2.setTitle("Remove");
            stage2.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
            stage2.setScene(new Scene(root2));
            stage2.setResizable(false);
            stage2.sizeToScene();
            stage2.showAndWait();

            stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    buildDataWithProp();
                }
            });

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error");
            alert.setHeaderText("Person error");
            alert.setContentText("Can't open update window");
        }
    }

    public void help(ActionEvent actionEvent) {
        Main.client.executeCommand("help");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void add(ActionEvent actionEvent) {

        FXMLLoader loader= new FXMLLoader(getClass().getResource("add.fxml"),Main.resourceBundle);
        Parent root1;
        try {
            root1 = loader.load();
            Stage stage1 = new Stage();
            stage1.setTitle("Add");
            stage1.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
            stage1.setScene(new Scene(root1));
            stage1.setResizable(false);
            stage1.sizeToScene();
            stage1.showAndWait();

            stage1.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    buildDataWithProp();
                }
            });

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error");
            alert.setHeaderText("Remove error");
            alert.setContentText("Can't open remove's window");
        }
    }

    public void minbyid(ActionEvent actionEvent) {
        Main.client.executeCommand("min_by_id");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void maxbycoordinates(ActionEvent actionEvent) {
        Main.client.executeCommand("max_by_coordinates");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void countgreaterthanlocation(ActionEvent actionEvent) {
        inputField.clear();
        AtomicReference<String> count = new AtomicReference<>("count_greater_than_location\n");
        outputField.setText("Ввод объека Location:\nВведите x" + pressReady);
        readyButton.setOnAction(event1 -> {
            count.set(count + inputField.getText() + "\n");
            inputField.clear();
            outputField.setText("Введите y" + pressReady);
            readyButton.setOnAction(event2 -> {
                count.set(count + inputField.getText() + "\n");
                inputField.clear();
                outputField.setText("Введите название локации" + pressReady);
                readyButton.setOnAction(event3 -> {
                    count.set(count + inputField.getText());
                    inputField.clear();
                    Main.client.executeCommand(String.valueOf(count));
                    waitForAnswer();
                    outputField.setText(Main.answerLine);
                    buildDataWithProp();
                });
            });
        });
    }

    public void show(ActionEvent actionEvent) {
        Main.client.executeCommand("show");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void execute_script(ActionEvent actionEvent) {
        inputField.clear();
        AtomicReference<String> execute_script = new AtomicReference<>("execute_script ");
        outputField.setText("Введите имя файла вместе с форматом" + pressReady);
        readyButton.setOnAction(event1 -> {
            execute_script.set(execute_script + inputField.getText());
            System.out.println(execute_script);
            inputField.clear();
            Main.client.executeCommand(String.valueOf(execute_script));
            waitForAnswer();
            outputField.setText(Main.answerLine);
            buildDataWithProp();
        });
    }

    public void clear(ActionEvent actionEvent) {
        Main.client.executeCommand("clear");
        waitForAnswer();
        outputField.setText(Main.answerLine);
        buildDataWithProp();
    }

    public void exitWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void ready(ActionEvent actionEvent) {
    }

    /***********************
     *    Tech methods     *
     ***********************/

    public void buildDataWithProp() {
        masterData.clear();
        fillPersons();
        masterData = FXCollections.observableArrayList();
        masterData.addAll(persons);
        dbTable.setItems(masterData);
    }

    public void fillPersons() {
        persons.clear();
        Connection c;
        try {
            c = DBConnect.connect();

            String SQL = "SELECT * from persons";
            ResultSet rs = c.createStatement().executeQuery(SQL);

            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                int coords_id = rs.getInt("coordinates_id");
                LocalDateTime creationDate = rs.getTimestamp("creationdate").toLocalDateTime();
                double height = rs.getDouble("height");
                String passport = rs.getString("passport");
                String hairColor = rs.getString("haircolor");
                String nationality = rs.getString("nationality");
                int location_id = rs.getInt("location_id");
                int user_id = rs.getInt("user_id");
                Location location = null;
                Coordinates coordinates = null;
                User user = null;

                users_keys.add(user_id);

                PreparedStatement preparedStatement = c.prepareStatement("select * from locations where id=?");
                preparedStatement.setInt(1, location_id);
                if (preparedStatement.execute()) {
                    ResultSet lrs = preparedStatement.getResultSet();
                    if (lrs.next()) {
                        long x = lrs.getLong("x");
                        int y = lrs.getInt("y");
                        String lname = lrs.getString("name");
                        location = new Location(x, y, lname);
                    }
                }

                preparedStatement = c.prepareStatement("select * from coordinates where id=?");
                preparedStatement.setInt(1, coords_id);
                if (preparedStatement.execute()) {
                    ResultSet crs = preparedStatement.getResultSet();
                    if (crs.next()) {
                        double x = crs.getDouble("x");
                        float y = crs.getFloat("y");
                        coordinates = new Coordinates(x, y);
                    }
                }

                preparedStatement = c.prepareStatement("select * from users where id=?");
                preparedStatement.setInt(1, user_id);
                if (preparedStatement.execute()) {
                    ResultSet urs = preparedStatement.getResultSet();
                    if (urs.next()) {
                        String login = urs.getString("login");
                        String password = urs.getString("password");
                        user = new User(login, password);
                    }
                }

                Person person = new Person(id, name, creationDate, location, coordinates, height, passport, hairColor, nationality, user);
                persons.add(person);

//                rs = c.createStatement().executeQuery("select users.id from users where login="+Main.username);
//                while (rs.next()) {
//                    Main.client_id = rs.getInt("id");
//                }
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Error");
            alert.setHeaderText("Data error");
            alert.setContentText("Не удалось загрузить бд");
            alert.showAndWait().ifPresent(rs -> {
            });

        }
    }

    public void startDraw(Event event) {
        timer.start();
        timeline.play();
    }

    public void drawPerson(int x, int y, int size, int color, DoubleProperty op, DoubleProperty koef) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
//            FileInputStream inputstream = new FileInputStream("C:\\Users\\chann\\Desktop\\lab7\\CoolerClient\\src\\markovpetr\\pictures\\" + color + ".png");
//            Image image = new Image(inputstream);
//            gc.drawImage(image, x, y, size, size);

//        Circle circle = new Circle(x,y,size);
//        circle.setRadius(size/2);
//        circle.centerXProperty().bind(canvas.widthProperty().multiply(x).divide(100));
//        circle.centerYProperty().bind(canvas.widthProperty().multiply(y).divide(100));
//        circle.setFill(Paint.valueOf("RED"));

//        FadeTransition st = new FadeTransition(Duration.millis(3000), circle);
//        st.setFromValue(0);
//        st.setToValue(1);
//        st.play();
        gc.setFill(colors.get(color).deriveColor(0, 1, 1, op.get()));
        gc.fillOval(x, y, koef.doubleValue() * size, koef.doubleValue() * size);


    }

    public void draw(DoubleProperty op,DoubleProperty koef) {

        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Person person : persons) {
            int color_owner_id = (int) (users_keys.get(persons.indexOf(person)) % 7);
            int x = person.getCoordinates().getX().intValue();
            int y = (int) person.getCoordinates().getY();
            int size = (int) (person.getHeight() / 2);
//            double offset = System.currentTimeMillis() / 300.;
            drawPerson(x, y, size, color_owner_id, op, koef);
        }

    }

    public void showPerson(MouseEvent mouseEvent) {
        double mouse_x = mouseEvent.getX();
        double mouse_y = mouseEvent.getY();
        for (Person person : persons) {
            int x = person.getCoordinates().getX().intValue();
            int y = (int) person.getCoordinates().getY();
            int size = (int) (person.getHeight() / 2);
            double offset = System.currentTimeMillis() / 300.;

            if(mouse_x > x && mouse_x < x + size && mouse_y > y +Math.sin(offset)*20&& mouse_y < y +Math.sin(offset)*20+size){
//                if (person.getOwner().getLogin() == Main.username) {
//                    Main.person = person;
//                    try {
//                        FXMLLoader loader = new FXMLLoader(getClass().getResource("show.fxml"), Main.resourceBundle);
//                        loader.load();
//                        Parent root = loader.getRoot();
//                        Stage stage = new Stage();
//                        stage.setScene(new Scene(root));
//                        stage.showAndWait();
//                    } catch (IOException ex) {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Error");
//                        alert.setHeaderText("Show error");
//                        alert.setContentText("Can't show this person");
//                        alert.showAndWait().ifPresent(rs -> {});
//                    }
//
//                } else{

                    Main.person = person;
                    FXMLLoader loader= new FXMLLoader(getClass().getResource("show.fxml"),Main.resourceBundle);
                    Parent root2;
                    try {
                        root2 = loader.load();
                        Stage stage2 = new Stage();
                        stage2.setTitle("Show");
                        stage2.getIcons().add(new Image(getClass().getResourceAsStream("../pictures/eva.png")));
                        stage2.setScene(new Scene(root2));
                        stage2.setResizable(false);
                        stage2.sizeToScene();
                        stage2.showAndWait();

//                        stage2.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                            public void handle(WindowEvent we) {
//                                buildDataWithProp();
//                            }
//                        });

                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.initStyle(StageStyle.UNDECORATED);
                        alert.setTitle("Error");
                        alert.setHeaderText("Show error");
                        alert.setContentText("Can't open show window");
                    }

            }
        }
        //System.out.println(mouse_x + " " + mouse_y);
    }

    public void createListeners(FilteredList<Person> filteredData) {

        idField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getId()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getName()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getLocation()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        creatField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getCreationDate()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getHeight()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        passportField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getPassportID()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        haircolorField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getHairColor()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        nationalityField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getNationality()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        userField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getOwner().getLogin()).contains(value)) {
                    return true;
                } else return false;
            });
        });

        coordField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String value = newValue;
                if (String.valueOf(person.getCoordinates()).contains(value)) {
                    return true;
                } else return false;
            });
        });
    }

    public void rus(ActionEvent actionEvent) {
        Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_ru", Locale.forLanguageTag("ru"));
        Scene scene = readyButton.getScene();
        try{
            scene.setRoot(FXMLLoader.load(getClass().getResource("main.fxml"), Main.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serb(ActionEvent actionEvent) {
        Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_sr", Locale.forLanguageTag("sr"));
        Scene scene = readyButton.getScene();
        try{
            scene.setRoot(FXMLLoader.load(getClass().getResource("main.fxml"), Main.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void bolg(ActionEvent actionEvent) {
        Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_bl", Locale.forLanguageTag("bl"));
        Scene scene = readyButton.getScene();
        try{
            scene.setRoot(FXMLLoader.load(getClass().getResource("main.fxml"), Main.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void esp(ActionEvent actionEvent) {
        Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_es", Locale.forLanguageTag("es"));
        Scene scene = readyButton.getScene();
        try{
            scene.setRoot(FXMLLoader.load(getClass().getResource("main.fxml"), Main.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eng(ActionEvent actionEvent) {
        Main.resourceBundle = ResourceBundle.getBundle("markovpetr.resources.Locale_en", Locale.forLanguageTag("en"));
        Scene scene = readyButton.getScene();
        try{
            scene.setRoot(FXMLLoader.load(getClass().getResource("main.fxml"), Main.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitForAnswer(){
        try {
            this.sleep(100);
        } catch (InterruptedException e) { }
    }


//    public void buildData(){
//        Connection c;
//        data = FXCollections.observableArrayList();
//        masterData = FXCollections.observableArrayList();
//        try {
//            c = DBConnect.connect();
//            String SQL = "SELECT persons.id, persons.name, coordinates.x, coordinates.y,\n" +
//                    "persons.creationdate, persons.height, persons.passport,\n" +
//                    "persons.haircolor, persons.nationality, locations.x,\n" +
//                    "locations.y, locations.name, users.login \n" +
//                    "FROM \n" +
//                    "persons\n" +
//                    "INNER JOIN\n" +
//                    "coordinates\n" +
//                    "on persons.coordinates_id = coordinates.id\n" +
//                    "INNER JOIN\n" +
//                    "users\n" +
//                    "on persons.user_id = users.id\n" +
//                    "INNER JOIN\n" +
//                    "locations\n" +
//                    "on persons.location_id = locations.id";
//
//            ResultSet rs = c.createStatement().executeQuery(SQL);
//
//            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
//                final int j = i;
//                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
//                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
//                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
//                        return new SimpleStringProperty(param.getValue().get(j).toString());
//                    }
//                });
//
//                newdbTable.getColumns().addAll(col);
//
//            }
//
//            while (rs.next()) {
//
//                ObservableList<String> row = FXCollections.observableArrayList();
//                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//
//                    row.add(rs.getString(i));
//                }
//                data.add(row);
//
//            }
//
//            newdbTable.setItems(data);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.initStyle(StageStyle.UNDECORATED);
//            alert.setTitle("Error");
//            alert.setHeaderText("Data error");
//            alert.setContentText("Не удалось загрузить бд");
//            alert.showAndWait().ifPresent(rs -> { });
//        }
//    }

}