package dev.noaln.project2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

import java.io.IOException;


public class DatabaseApplication extends Application {
    //private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    //private static String username="N01440422";
    //private static String pass = "Fall20210422";

    @FXML
    private Button loginButton;
    @FXML
    private TextField address;
    @FXML
    private TextField username;
    @FXML
    private TextField password;


    static Connection con=null;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabaseControllerFX.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("SQL Controller");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) throws SQLException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        launch();
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent actionEvent) {
        try{

            con = DriverManager.getConnection(address.getText(), username.getText(), password.getText());
            System.out.println("Connected to Database: [" + con.getCatalog() + "]");

            //We should launch the "Edit" view from this point because we have a good connection.
        }
        catch (Exception E){
            System.out.println("Something went wrong, Please try again! ");
        }

    }

    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
    }
}