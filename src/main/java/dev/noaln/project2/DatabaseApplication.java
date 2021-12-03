package dev.noaln.project2;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

import java.io.IOException;


public class DatabaseApplication extends Application {
    private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    private static String username="N01440422";
    private static String pass = "Fall20210422";


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabasesScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Controller");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try{
            Connection con = DriverManager.getConnection(connection, username, pass);
            System.out.println("Connected to Database: [" + con.getCatalog() + "]");
        }
        catch(Exception E){
            E.printStackTrace();
            System.out.println("An error occurred while connecting to the specified database..");
            System.exit(0); //Reset and prompt for new connection if failed.
        }


        launch();
    }


    //region FXML
    @FXML
    private Label welcomeText;

    @FXML
    protected void onQuitButtonClick() {
        System.out.println("Quit Button Clicked.. Application Closed!");
        System.exit(0);
    }
    @FXML
    protected void onCreditsButton(){

    }


    //endregion

}