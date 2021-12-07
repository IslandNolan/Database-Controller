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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

import java.io.IOException;


public class DatabaseApplication extends Application {
    private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    private static String username="N01440422";
    private static String pass = "Fall20210422";
    static Connection con=null;


    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabasesScene.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 640, 480);

        Text t1 = new Text("Server Address:");
        t1.setLayoutX(115);
        t1.setLayoutY(115);
        TextField serverAddress = new TextField("jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2");
        serverAddress.setLayoutY(100);
        serverAddress.setLayoutX(225);


        TextField username = new TextField("N01440422");
        username.setLayoutY(150);
        username.setLayoutX(225);
        Text t2 = new Text("Login Name:");
        t2.setLayoutX(115);
        t2.setLayoutY(165);


        Text t3 = new Text("Password:");
        t3.setLayoutX(115);
        t3.setLayoutY(215);
        TextField password = new TextField("");
        password.setLayoutY(200);
        password.setLayoutX(225);

        Button b = new Button("Connect to Database.. ");
        b.setLayoutX(225);
        b.setLayoutY(250);

        Group root = new Group();
        root.getChildren().addAll(serverAddress,b,password,username,t1,t2,t3);


        b.setOnAction(e -> {
            connectButtonClicked(serverAddress,username,password);
        });

        Scene s = new Scene(root,640,480);


        stage.setTitle("SQL Controller");
        stage.setResizable(false);
        stage.setScene(s);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        launch();
    }
    public void connectButtonClicked(TextField address, TextField user, TextField pass) {
        try{
            String addr = address.getText();
            String us = user.getText();
            String pas = pass.getText();
            con = DriverManager.getConnection(addr, us, pas);
            System.out.println("Connected to Database: [" + con.getCatalog() + "]");

            //We should launch the "Edit" view from this point because we have a good connection.
        }
        catch (Exception E){
            System.out.println("Something went wrong, Please try again! ");
            pass.clear();
        }

    }

}