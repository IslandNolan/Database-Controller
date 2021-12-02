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
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabasesScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Controller");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, MalformedURLException {
       Connection con = DriverManager.getConnection("jdbc:mariadb://10.253.126.96/db","root","jyzma0");




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